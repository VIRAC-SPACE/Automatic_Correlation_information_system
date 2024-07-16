package com.main.vlbi.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.main.vlbi.models.Experiment;
import com.main.vlbi.models.Result;
import com.main.vlbi.services.implementations.ExperimentServiceImpl;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

import com.main.vlbi.models.Custom;

@RestController
@Controller
@RequestMapping(path = "/experiments")
public class ExperimentController {
    private final static String experimentPath = System.getenv("dirrectory2");
    private final String scriptPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), "scripts") + "/";
    private static final Logger logger = Logger.getLogger(ctlr_file_controller.class.getName());
    @Autowired
    private ExperimentServiceImpl experimentServiceImpl;

    @RequestMapping(method = RequestMethod.GET, path = "/all")
    public @ResponseBody
    Iterable<Experiment> getAllExperiments() {
        return experimentServiceImpl.getAllExperiments();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getexperimentbyname")
    public @ResponseBody
    Optional<Experiment> getExperimentByName(@RequestParam String experimentNAME) {
        String ExperimentName = experimentNAME.split("\\.")[0];
        return experimentServiceImpl.getExperimentByName(ExperimentName);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getexperimentcorrtype")
    public @ResponseBody
    String getExperimentCorrType(@RequestParam String experimentNAME) {
        Optional<Experiment> experiment_ = experimentServiceImpl.getExperimentByName(experimentNAME);
        Experiment experiment = experiment_.get();

        Custom text = new Custom();
        text.setText(experiment.getCorr_type());
        Gson objGson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<Custom>() {
        }.getType();
        return objGson.toJson(text, type);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getexperimentbynameandtype")
    public @ResponseBody
    Optional<Experiment> getExperimentByNameAndType(@RequestParam String experimentNAME, @RequestParam String experimentTYPE) {
        String ExperimentName = experimentNAME.split("\\.")[0];
        return experimentServiceImpl.getExperimentByNameAndType(ExperimentName, experimentTYPE);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/setexperimentname")
    public @ResponseBody
    void SetExperimentName(@RequestBody @RequestParam String experimentNAME) {
        Vex_file_controllers.setExperimentName(experimentNAME);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/setexperimenttype")
    public @ResponseBody
    void SetExperimentType(@RequestBody @RequestParam String experimentTYPE) {
        Vex_file_controllers.setExperimentType(experimentTYPE.toLowerCase(Locale.ROOT));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/delete")
    public @ResponseBody
    void deleteExperiment(@RequestParam String experimentNAME) {
        Optional<Experiment> experiment_tmp = experimentServiceImpl.getExperimentByName(experimentNAME);
        Experiment experiment = experiment_tmp.get();
        Long id = experiment.getId();
        experimentServiceImpl.Delete(id);

        String path = experimentPath + "/" +  Vex_file_controllers.getExperimentType()  + "/" + experimentNAME;
        File experimentFiles = new File(path);

        String[] entries = experimentFiles.list();
        assert entries != null;
        for (String s : entries) {
            File currentFile = new File(experimentFiles.getPath(), s);
            currentFile.delete();
        }

        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/downloadexperiment")
    public @ResponseBody
    void downloadExperiment(HttpServletResponse response) throws FileNotFoundException {
        ProcessBuilder pb;
        Process p;

        try {
            pb = new ProcessBuilder(scriptPath + "createResults.sh", Vex_file_controllers.getExperimentName(), Vex_file_controllers.getExperimentType());
            pb.redirectErrorStream(true);
            p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e2) {
            e2.printStackTrace();
        }

        File file = getFile(experimentPath + Vex_file_controllers.getExperimentType() + "/" + Vex_file_controllers.getExperimentName() + ".zip");
        FileInputStream is = null;

        try {
            is = new FileInputStream(file);
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + Vex_file_controllers.getExperimentName() + ".zip");
            response.setHeader("Content-Length", String.valueOf(file.length()));
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

    @RequestMapping(method = RequestMethod.GET, path = "/getplothtml")
    public @ResponseBody
    String getplotshtml(@RequestParam String experimentNAME, @RequestParam String scan) {
        String path = experimentPath + "/sfxc/" + experimentNAME + "/results/fringe/" + scan + "/plots.html";
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert is != null;
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        Result result = new Result();
        result.setPlotContent(sb.toString());
        Gson objGson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<Result>() {
        }.getType();
        return objGson.toJson(result, type);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getplothtmlmulti")
    public @ResponseBody
    String getplotshtmlmulti(@RequestParam String experimentNAME, @RequestParam String scan, @RequestParam String passType) {
        String path = experimentPath + "/sfxc/" + experimentNAME + "/results/" + passType.toLowerCase() + "/fringe/" + scan + "/plots.html";
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert is != null;
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        Result result = new Result();
        result.setPlotContent(sb.toString());
        Gson objGson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<Result>() {
        }.getType();
        return objGson.toJson(result, type);
    }


    private File getFile(String FILE_PATH) throws FileNotFoundException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            throw new FileNotFoundException("file with path: " + FILE_PATH + " was not found.");
        }
        return file;
    }
}
