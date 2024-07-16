package com.main.vlbi.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.main.vlbi.models.*;
import com.main.vlbi.vexparser.VexParser;
import com.main.vlbi.vexparser.VexScan;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.logging.Logger;

@RestController
@Controller
@RequestMapping(path = "/ctlr")
public class ctlr_file_controller {
    private static String scan;

    private final static String experimentPath = System.getenv("dirrectory2");
    private final String pythonPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), "python") + "/";
    private final String scriptPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), "scripts") + "/";
    private static final Logger logger = Logger.getLogger(ctlr_file_controller.class.getName());

    private static String kana_ctrl_param;

    public static String getScan() {
        return ctlr_file_controller.scan;
    }

    public static void setScan(String scan) {
        ctlr_file_controller.scan = scan;
    }

    private void createDataProcessingDirectory(String experimentName, String experimentType, String project){
        ProcessBuilder pb;
        Process p;
        try {
            pb = new ProcessBuilder("bash", scriptPath + "make_dir.sh", experimentName,  experimentType, project);
            pb.redirectErrorStream(true);
            p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequestMapping(method = RequestMethod.POST, path = "/createdirgetvexfile")
    public
    void createdirgetvexfile(@RequestBody ArrayList<obs> observations) {
       for (obs observation : observations){
           createDataProcessingDirectory(observation.getExp_code(), observation.getCorrelator().toLowerCase(), observation.getProject());
       }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/returncontrolfiledata")
    public @ResponseBody
    String returnControlFileData(@RequestBody String observation_) {
        Gson gson_input = new Gson();
        Type type_input = new TypeToken<obs>() {}.getType();
        obs observation = gson_input.fromJson(observation_, type_input);

        Control_file_data control_file = new Control_file_data();
        Gson objGson = new GsonBuilder().setPrettyPrinting().create();
        VexParser vexParser = new VexParser(
                experimentPath +  observation.getCorrelator().toLowerCase() + "/" + observation.getExp_code() + '/' + observation.getExp_code() + ".vex");
        ArrayList<VexScan> vexScanList = vexParser.extractSched();
        int channels = vexParser.getChannels();

        ArrayList<String> channelArray = new ArrayList<>();
        String chanName;
        for (int i = 1; i <= channels; i++) {
            chanName = "CH" + String.format("%02d", i);
            channelArray.add(chanName);
        }

        int nr_of_scans = vexScanList.size();
        String experimentNameFromFile = vexParser.getExperimentName().toLowerCase().trim();
        if (!experimentNameFromFile.equals(Vex_file_controllers.getExperimentName()))
            experimentNameFromFile = Vex_file_controllers.getExperimentName();

        String StartTime = vexParser.getStartTime();
        String StopTime = vexParser.getStopTime();

        String[] StationsString = new String[nr_of_scans];
        for (int i = 0; i < nr_of_scans; i++)
            StationsString[i] = "";
        String[][] stationList = new String[nr_of_scans][];
        ArrayList<String> scanList = new ArrayList<>();
        StringBuilder strBuild;
        ArrayList<Scan> scans = new ArrayList<>();
        ArrayList<String> sources = vexParser.getSources();

        for (int i = 0; i < nr_of_scans; i++) {
            scanList.add(vexScanList.get(i).getScanNumber());
            StationsString[i] += vexScanList.get(i).getStationsArrayList().toString();
            strBuild = new StringBuilder(StationsString[i]);
            strBuild.setCharAt(0, ' ');
            strBuild.setCharAt(strBuild.length() - 1, ' ');
            StationsString[i] = strBuild.toString().trim();
            stationList[i] = StationsString[i].split(", ");

            Scan scanForStation = new Scan(); //null;
            scanForStation.setStarTime(vexScanList.get(i).getStartTime());
            scanForStation.setStopTime(vexScanList.get(i).getStopTime());
            scanForStation.setScanNumber(vexScanList.get(i).getScanNumber());

            ArrayList<String> stationArray = new ArrayList<>();
            Collections.addAll(stationArray, stationList[i]);
            scanForStation.setStationLists(stationArray);
            scans.add(scanForStation);
        }

        control_file.setChannelArray(channelArray);
        control_file.setDataArray(scans);
        control_file.setExperimentName(observation.getExp_code());
        control_file.setGlobalStartTime(StartTime);
        control_file.setGlobalStopTime(StopTime);
        control_file.setScans(scanList);
        control_file.setStations(stationList);
        control_file.setSources(sources);

        Type type = new TypeToken<Control_file_data>() {
        }.getType();
        return objGson.toJson(control_file, type);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/sendcontrolfiledata")
    public @ResponseBody
    void createControlFile(@RequestBody String requestString) {
        Type type_ = new TypeToken<Control_file>() {}.getType();
        Gson controlFileGson_ = new Gson();
        Control_file controlFile_ = controlFileGson_.fromJson(requestString, type_);
        String correalator = controlFile_.getCorrelator().toLowerCase();

        if (correalator.equals("sfxc")) {
            Type type = new TypeToken<Control_file>() {}.getType();
            Gson controlFileGson = new Gson();
            Control_file controlFile = controlFileGson.fromJson(requestString, type);
            controlFile.setHtml_output("file://results/fringe");
            controlFile.setDelay_directory("file://delays");
            controlFile.setOutput_file("file://" + controlFile.getExper_name() + "_" + controlFile.getScan() + ".cor");
            setScan(controlFile.getScan());
            Gson outGsonObj = new Gson();
            String json = outGsonObj.toJson(controlFile, type);

            String ctrlPath = experimentPath + correalator + "/" + controlFile.getExper_name();
            Writer fileWriter = null;
            File ctrlFile = new File(ctrlPath + "/TMP.ctrl");

            try {
                fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ctrlFile), StandardCharsets.UTF_8));
                fileWriter.write(json);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                	logger.severe("Writer is not open");
                	
                }
            }

            makingControlFilePretty(ctrlPath,  controlFile.getExper_name());
        }

        else if (correalator.equals("kana")) {
            Type type = new TypeToken<kana_control_file>() {}.getType();
            Gson controlFilegson = new Gson();
            kana_control_file controlFile = controlFilegson.fromJson(requestString, type);

            kanaVexToCtrl(controlFile.getJob().toUpperCase(), controlFile.getBufferBegin(), controlFile.getBufferEnd(),
                    controlFile.getDelayBegin(), controlFile.getDelayEnd(), controlFile.getDataDir(),
                    controlFile.getRealTime(), controlFile.getKanaInAddress(), controlFile.getKanaInPort(),
                    controlFile.getKanaOutAddress(), controlFile.getKanaOutPort(), controlFile.getRt_batch_length());

            if (controlFile.getJob().equalsIgnoreCase("M3")) {
                this.kana_ctrl_param = Vex_file_controllers.getExperimentName() + "_" + controlFile.getJob().toUpperCase()
                        + "_" + controlFile.getBufferBegin() + "_" + controlFile.getBufferEnd()
                        + "_" + controlFile.getDelayBegin() + "_" + controlFile.getDelayEnd();
            }
            else{
                this.kana_ctrl_param = Vex_file_controllers.getExperimentName() + "_" + controlFile.getJob().toUpperCase()
                        + "_" + controlFile.getBufferBegin() + "_" + controlFile.getBufferEnd();
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/returncontrolfile")
    public @ResponseBody
    void returnControlfile(HttpServletResponse response, @RequestParam String exp_code, @RequestParam String experiment_type) {
        FileInputStream is = null;
        String path = null;
        if (experiment_type.equals("sfxc")){
            path = experimentPath + experiment_type + "/" + exp_code + "/" + exp_code + ".ctrl";
        }
        else if (experiment_type.equals("kana")){
            path = experimentPath + experiment_type + "/" + exp_code + "/" + this.kana_ctrl_param + "/" +  exp_code + ".ctrl";
        }


        try {
            assert path != null;
            is = new FileInputStream(path);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.severe("file stream was not close");
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/corfile")
    public @ResponseBody
    void createCorFile(@RequestParam String exp_code, @RequestParam String experiment_type,
                       @RequestParam String corr_type, @RequestParam String pass_type) {
        if (experiment_type.equals("sfxc")) {
            if (corr_type.equals("multi")) {
                if (pass_type.equals("Contiuum_Line")) {
                    ProcessBuilder pb2 = new ProcessBuilder("sh", scriptPath + "/run_correlate_all_scans.sh", exp_code, "line");

                    Process p2 = null;
                    try {
                        p2 = pb2.start();
                        p2.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    assert p2 != null;
                    p2.destroy();

                    ProcessBuilder pb1 = new ProcessBuilder("sh", scriptPath + "/run_correlate_all_scans.sh", exp_code,"contiuum");
                    Process p1 = null;

                    try {
                        p1 = pb1.start();
                        p1.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    assert p1 != null;
                    p1.destroy();

                    VexParser vexParser = new VexParser(
                            experimentPath + experiment_type + "/" + exp_code + '/' + exp_code + ".vex");
                    String date = vexParser.getdate();

                    HashMap<String, String> months = new HashMap<>();
                    months.put("Jan", "01");
                    months.put("Feb", "02");
                    months.put("Mar", "03");
                    months.put("Apr", "04");
                    months.put("May", "05");
                    months.put("Jun", "06");
                    months.put("Jul", "07");
                    months.put("Aug", "08");
                    months.put("Sep", "09");
                    months.put("Oct", "10");
                    months.put("Nov", "11");
                    months.put("Dec", "12");

                    String year = date.split(" ")[4].trim();
                    String month = date.split(" ")[3].trim();
                    String day = date.split(" ")[2].trim();
                    String monthd = months.get(month);

                    ProcessBuilder pb4 = new ProcessBuilder("sh", scriptPath  + "/run_run_aips.sh", exp_code, year, month, day, monthd);
                    Process p4 = null;

                    try {
                        p4 = pb4.start();
                        p4.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    assert p4 != null;
                    p4.destroy();

                }
                else{
                    ProcessBuilder pb3 = new ProcessBuilder("sh", scriptPath + "/run_correlate_all_scans.sh", exp_code, pass_type);
                    Process p3;

                    try {
                        p3 = pb3.start();
                        p3.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                ProcessBuilder pb1 = new ProcessBuilder("python3", pythonPath + "/correlate_single_scan.py", exp_code, ctlr_file_controller.getScan());
                Process p1;

                try {
                    p1 = pb1.start();
                    p1.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        else if (experiment_type.equals("kana")) {
            ProcessBuilder pb1 = new ProcessBuilder("python3", pythonPath + "/kana.py", experimentPath + exp_code + "/" + exp_code + "/" + this.kana_ctrl_param + "/" + exp_code + ".ctrl");
            Process p1;
            try {
                p1 = pb1.start();
                p1.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            ProcessBuilder pb2 = new ProcessBuilder("python3", pythonPath + "/kanaplotpng.py",  exp_code, this.kana_ctrl_param.replace(exp_code + "_", ""), this.kana_ctrl_param.split("_")[1].toLowerCase(Locale.ROOT));
            Process p2;
            try {
                p2 = pb2.start();
                p2.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private void makingControlFilePretty(String experimentParamPath, String experiment_name) {
        ProcessBuilder pb1 = new ProcessBuilder("python2", pythonPath + "/making_control_file_pretty.py", experimentParamPath + "/TMP.ctrl", experimentParamPath + "/" + experiment_name + ".ctrl");
        Process p1;

        try {
            p1 = pb1.start();
            p1.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void kanaVexToCtrl(String job, String bufferBegin, String bufferEnd, String delayBegin, String delayEnd, String DataDir, String RealTime,  String KanaInAddress, int KanaInPort,  String KanaOutAddress, int KanaOutPort, int rt_batch_length) {
        ProcessBuilder pb1 = new ProcessBuilder("python3", pythonPath + "/kanamakectrl.py", job, experimentPath + "/kana/" + Vex_file_controllers.getExperimentName()+"/"+ Vex_file_controllers.getExperimentName(),
                bufferBegin, bufferEnd, delayBegin, delayEnd, DataDir, RealTime, KanaInAddress + ":" + KanaInPort, KanaOutAddress+ ":" + KanaOutPort, Integer.toString(rt_batch_length));
        Process p1;
        try {
            p1 = pb1.start();
            p1.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getKana_ctrl_param() {
        return kana_ctrl_param;
    }

    public static String getSource(){
        VexParser vexParser = new VexParser(
                experimentPath + Vex_file_controllers.getExperimentType() + "/" + Vex_file_controllers.getExperimentName() + '/' + Vex_file_controllers.getExperimentName() + ".vex");
        ArrayList<VexScan> vexScanList = vexParser.extractSched();
        return vexParser.getSource(ctlr_file_controller.getScan());
    }
}
