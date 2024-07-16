package com.main.vlbi.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.main.vlbi.models.VexFile;
import com.main.vlbi.services.VexFileValidation;
import com.main.vlbi.services.implementations.ExperimentServiceImpl;
import com.main.vlbi.vexparser.VexParser;
import com.main.vlbi.vexparser.VexScan;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RestController
@Controller
@RequestMapping(path = "/vex")
public class Vex_file_controllers {
    private final static String experimentPath = System.getenv("dirrectory2");
    private static String experimentName;
    private static String experimentType;
    private final String pythonPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), "python") + "/";
    private final String scriptPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), "scripts") + "/";
    private static final Logger logger = Logger.getLogger(ctlr_file_controller.class.getName());
    private boolean addClock;

    private static String corrType;
    private static String passType;

    @Autowired
    private ExperimentServiceImpl experimentServiceImpl;

    public static String getExperimentName() {
        return experimentName;
    }

    public static void setExperimentName(String experimentName) {
        Vex_file_controllers.experimentName = experimentName;
    }

    public static String getExperimentType() {
        return experimentType;
    }

    public static void setExperimentType(String experimentTYPE) {
        Vex_file_controllers.experimentType = experimentTYPE;
    }

    public static Object getCorrType() {
        return corrType;
    }

    public static String getPassType() {
        return passType;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/getvexfile")
    public @ResponseBody
    void geVexFile(@RequestBody String requestString) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<VexFile>() {
        }.getType();
        VexFile vex = gson.fromJson(requestString, type);
        String vexFileName = vex.getVexName();
        experimentType = vex.getType().toLowerCase();
        String vexfile = vex.getVexfile();
        corrType = vex.getCorrType();
        passType = vex.getPassType();

        if (vexFileName.contains(".vix")) {
            vexFileName = vexFileName.replace(".vix", ".vex");
        }

        BufferedWriter out = null;
        int AddClockValue;

        experimentName = vexFileName.replace(".vex", " ").trim().toLowerCase();
        String vexFilePath = experimentPath + experimentType + "/" + experimentName + '/';
        AddClockValue = vex.getAddClock();

        ProcessBuilder pb;
        Process p;
        try {
            pb = new ProcessBuilder("sh", scriptPath + "make_dir.sh", experimentName,  experimentType);
            pb.redirectErrorStream(true);
            p = pb.start();
            p.waitFor();
            out = new BufferedWriter(new FileWriter(vexFilePath + experimentName + ".vex"));
            out.write(vexfile);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (out != null) {
                out.close();
            } else {
                logger.severe("Buffer writer is not open");
            }
        }

        setAddClock(AddClockValue == 1);
        update_vex();
        VexParser vexParser = new VexParser(vexFilePath, Arrays.asList(vexfile.split("\n")));
        vexParser.extractSched();
        if (Objects.equals(experimentType, "sfxc")){
            create_control_file();
            experimentServiceImpl.createSFXC(experimentName, vexParser.getStartTime(), corrType);
        }
        if (Objects.equals(experimentType, "kana")) {
            experimentServiceImpl.createKANA(experimentName, vexParser.getStartTime());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/getclocks")
    public @ResponseBody
    void getclocks(@RequestBody String requestString) {
        VexParser vexParser = new VexParser(experimentPath + experimentType + "/" + experimentName + '/' + experimentName + ".vex");
        ArrayList<VexScan> vexScanList = vexParser.extractSched();
        ArrayList<String> AllStations = new ArrayList<>();

        for (VexScan vexScan : vexScanList) {
            AllStations.addAll(vexScan.getStationsArrayList());
        }

        ArrayList<String> allstationlistu = (ArrayList<String>) AllStations.stream().distinct().collect(Collectors.toList());
        ArrayList<String> clockList = new ArrayList<>((Arrays.asList(requestString.substring(11, requestString.length() - 2).split(","))));

        String startTime = vexParser.getStartTime();
        String stopTime = vexParser.getStopTime();

        ProcessBuilder pb;
        Process p = null;

        try {
            pb = new ProcessBuilder("python2", pythonPath + "/get_mid.py", startTime, stopTime);
            pb.redirectErrorStream(true);
            p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e2) {
            e2.printStackTrace();
        }

        assert p != null;
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String mid = null;
        try {
            mid = stdInput.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 0;
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(experimentPath + experimentType + "/" + experimentName + '/' + experimentName + ".vix", true)));
            out.println("*---------------------------------------------------------------------------------------\n");
            out.println("$CLOCK;                  *  Experiment: " + experimentName + "\n");
            out.println("*---------------------------------------------------------------------------------------\n");
            out.println("*                 valid from           offset     mid     rate");

            while (clockList.size() != 0) {

                String ClockEarly = clockList.get(0).replaceAll("^\"|\"$", "");
                String Rate = clockList.get(1).replaceAll("^\"|\"$", "");

                out.println("def " + allstationlistu.get(i).toUpperCase() + ";" + "\n"
                        + "     clock_early = " + startTime + " : " + ClockEarly + " usec " + " : " + mid + " : " + Rate + " ;" + "\n" + "enddef;" + "\n *  \n");
                clockList.remove(0);
                clockList.remove(0);

                i++;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            } else {
                logger.severe("Buffer writer is not open");
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/changeClocks")
    public @ResponseBody
    void changeClocks(@RequestBody String requestString) {
        ArrayList<String> clockList = new ArrayList<>((Arrays.asList(requestString.substring(11, requestString.length() - 2).split(","))));
        String vex_file = experimentPath + experimentType + "/" + experimentName + "/" + Vex_file_controllers.getExperimentName() + ".vix";
        ProcessBuilder pb;
        Process p;

        try {
            pb = new ProcessBuilder("python2", pythonPath + "/change_clock.py", vex_file, clockList.toString().replace("[", "").replace("]", "").replaceAll("\\s+", ""));
            pb.redirectErrorStream(true);
            p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e2) {
            e2.printStackTrace();
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/validatevex")
    public @ResponseBody
    String validateVexFile(@RequestBody String requestString) {
        Gson gson = new Gson();
        Type type = new TypeToken<VexFile>() {
        }.getType();
        VexFile vex = gson.fromJson(requestString, type);
        String vex_file_content = vex.getVexfile();
        VexFileValidation vexFileValidation = new VexFileValidation();
        Map<String, Boolean> vexValid = new HashMap<>();
        Type listType = new TypeToken<Map<String, Boolean>>() {
        }.getType();
        vexValid.put("vex_file_valid", vexFileValidation.validateVexFile(Arrays.asList(vex_file_content.split("\n"))));
        Gson objGson = new GsonBuilder().setPrettyPrinting().create();
        return objGson.toJson(vexValid, listType);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/returnvexfile")
    public @ResponseBody
    void returnVexFile(HttpServletResponse response, @RequestParam String exp_code, @RequestParam String experiment_type) {
        FileInputStream is = null;
        String path = experimentPath + experiment_type + "/" + exp_code + "/" + exp_code + ".vix";
        try {
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
                logger.severe("Buffer writer is not open");
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getallstations")
    public @ResponseBody
    String getAllStations() {
        VexParser vexParser = new VexParser(experimentPath + experimentType + "/" + experimentName + '/' + experimentName + ".vex");
        ArrayList<VexScan> vexScansList = vexParser.extractSched();
        ArrayList<String> allstationlist = new ArrayList<>();
        for (VexScan vexScan : vexScansList) {
            allstationlist.addAll(vexScan.getStationsArrayList());
        }
        ArrayList<String> allstationlistu = (ArrayList<String>) allstationlist.stream().distinct().collect(Collectors.toList());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, ArrayList<String>> stations = new HashMap<>();
        stations.put("AllStations", allstationlistu);
        Type listType = new TypeToken<Map<String, ArrayList<String>>>() {
        }.getType();
        return gson.toJson(stations, listType);
    }

    public void update_vex() {
        boolean withOutClock = isAddClock();
        String vex_path = experimentPath + experimentType + "/" + experimentName + '/' + experimentName + ".vex";
        String vix_path = experimentPath + experimentType + "/" + experimentName + '/' + experimentName + ".vix";

        if (withOutClock) {
            ProcessBuilder pb;
            Process p;

            try {
                pb = new ProcessBuilder("python2", pythonPath + "/vex_update_without_clock.py", vex_path, vix_path);
                pb.redirectErrorStream(true);
                p = pb.start();
                p.waitFor();
            } catch (IOException | InterruptedException e2) {
                e2.printStackTrace();
            }
        } else {
            ProcessBuilder pb;
            Process p;

            try {
                pb = new ProcessBuilder("python2", pythonPath + "/vex_update.py", vex_path, vix_path);
                pb.redirectErrorStream(true);
                p = pb.start();
                p.waitFor();
            } catch (IOException | InterruptedException e2) {
                e2.printStackTrace();
            }
        }

    }

    public void create_control_file() {
        ProcessBuilder pb;
        Process p;
        String path = experimentPath + experimentType + "/" + experimentName + '/' + experimentName;
        try {
            pb = new ProcessBuilder("python2", pythonPath + "/vex2ccf.py", path + ".vex", path + ".ctrl");
            pb.redirectErrorStream(true);
            p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e2) {
            e2.printStackTrace();
        }
    }

    public boolean isAddClock() {
        return addClock;
    }

    public void setAddClock(boolean addClock) {
        this.addClock = addClock;
    }
}
