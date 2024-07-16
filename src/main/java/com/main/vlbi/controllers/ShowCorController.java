package com.main.vlbi.controllers;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


@RestController
@Controller
@RequestMapping(path = "/results")
public class ShowCorController {
    private final static String experimentPath = System.getenv("dirrectory2");

    @RequestMapping(method = RequestMethod.GET, path = "/getplot")
    public @ResponseBody
    void getPlot(HttpServletResponse response, @RequestParam String name) {

        InputStream is = null;
        String path = experimentPath + "/" + Vex_file_controllers.getExperimentType() + "/" + Vex_file_controllers.getExperimentName() + "/" +
                "results/fringe/" + ctlr_file_controller.getScan() + "/";
        try {
            is = new FileInputStream(path + name);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getplotmulti", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    ResponseEntity<Resource> getPlotMulti(@RequestParam String name, @RequestParam String passType, @RequestParam String scan, @RequestParam String exp_code) throws IOException {
        String path = experimentPath + "/" + "sfxc" + "/" + exp_code + "/" + "results/" + passType.toLowerCase() + "/fringe/" + scan + "/";

        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(path + name)
        ));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getaipsnames")
    public @ResponseBody
    ArrayList<String> getAIPSNames(@RequestParam String experimentNAME, @RequestParam String correlator){
        ArrayList<String> plotsAIPSnames = new ArrayList<>();

        String path = experimentPath + correlator.toLowerCase() + "/" + experimentNAME + "/" + "results/aips/";

        File plotNames_ = new File(path);
        String[] plotNames = plotNames_.list();
        assert plotNames != null;
        for (String pn : plotNames){
            if (pn.endsWith(".pdf")){
                plotsAIPSnames.add(pn);
            }
        }

        return plotsAIPSnames;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getplotsaips", produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody ResponseEntity<Resource> getPlotAIPS(@RequestParam String name, @RequestParam String exp_code) throws IOException {
        String path = experimentPath + "/sfxc/" + exp_code + "/" + "results/aips/";

        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(path + name)));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/getstandartplots")
    public @ResponseBody
    void getStandartPlot(HttpServletResponse response, @RequestParam String name) {
        InputStream is = null;
        String path = experimentPath + "/" + Vex_file_controllers.getExperimentType() + "/" + Vex_file_controllers.getExperimentName() + "/" +
                "results/standartplots/" + ctlr_file_controller.getSource() + "/" ;
        try {
            is = new FileInputStream(path + name);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getstandartplotsmulti", produces = "application/json; charset=UTF-8")
    public @ResponseBody
    void getStandartPlotMulti(HttpServletResponse response, @RequestParam String name, @RequestParam String passType, @RequestParam String source, @RequestParam String exp_code) {
        source = source.replace(" ", "+");
        InputStream is = null;
        String path = experimentPath + "sfxc/" + exp_code + "/results/" + passType.toLowerCase() + "/standartplots/" + source + "/" +  name;
        try {
            is = new FileInputStream(path);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getstandartplotnames",  produces = "application/json; charset=UTF-8")
    public @ResponseBody
    ArrayList<String> getStanDartPlotNames(){
        ArrayList<String> plotNamesPNG = new ArrayList<>();
        String path = experimentPath + "/" + Vex_file_controllers.getExperimentType() + "/" + Vex_file_controllers.getExperimentName() + "/" +
                "results/standartplots/" + "/" + ctlr_file_controller.getSource() + "/";
        File plotNames_ = new File(path);
        String[] plotNames = plotNames_.list();
        for (String pn : plotNames){
            if (pn.endsWith(".png")){
                plotNamesPNG.add(pn);
            }
        }

        return plotNamesPNG;
    }


    @RequestMapping(method = RequestMethod.GET, path = "/getstandartplotnamesmulti",  produces = "application/json; charset=UTF-8")
    public @ResponseBody
    ArrayList<String> getStanDartPlotNamesMulti(@RequestParam String passType, @RequestParam String source, @RequestParam String exp_code){
        ArrayList<String> plotNamesPNG = new ArrayList<>();

        source = source.replace(" ", "+");
        String path = experimentPath + "/" + "sfxc/"  + exp_code + "/" +
                "results/" + passType.toLowerCase() + "/standartplots/" + source + "/";

        File plotNames_ = new File(path);
        String[] plotNames = plotNames_.list();
        assert plotNames != null;
        for (String pn : plotNames){
            if (pn.endsWith(".png")){
                plotNamesPNG.add(pn);
            }
        }

        return plotNamesPNG;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getkanaplot")
    public @ResponseBody
    void getKanaPlot(HttpServletResponse response, @RequestParam String name){
        String path = experimentPath + Vex_file_controllers.getExperimentType() + "/" + Vex_file_controllers.getExperimentName() + "/" + ctlr_file_controller.getKana_ctrl_param() + "/results/png/";
        InputStream is = null;
        try {
            is = new FileInputStream(path + name);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getkanaplotnames")
    public @ResponseBody
    String[] getKanaPlotNames(){
        String path = experimentPath + Vex_file_controllers.getExperimentType() + "/" + Vex_file_controllers.getExperimentName() + "/" + ctlr_file_controller.getKana_ctrl_param() + "/results/png/";
        File directoryPath = new File(path);
        return directoryPath.list();
    }
}
