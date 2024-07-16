package com.main.vlbi.models;

import java.util.ArrayList;

public class Control_file_data {
    private String experimentName;
    private ArrayList<String> channelArray;
    private String globalStartTime;
    private String globalStopTime;
    private String[][] stations;
    private ArrayList<Scan> dataArray;
    private ArrayList<String> scans;
    private ArrayList<String> sources;

    public Control_file_data() {
        channelArray = new ArrayList<>();
        dataArray = new ArrayList<>();
        scans = new ArrayList<>();
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public void setChannelArray(ArrayList<String> channelArray) {
        this.channelArray = channelArray;
    }

    public void setGlobalStartTime(String globalStartTime) {
        this.globalStartTime = globalStartTime;
    }

    public void setGlobalStopTime(String globalStopTime) {
        this.globalStopTime = globalStopTime;
    }

    public void setStations(String[][] stationList) {
        this.stations = stationList;
    }

    public void setDataArray(ArrayList<Scan> arrayOfJsons) {
        this.dataArray = arrayOfJsons;
    }

    public void setScans(ArrayList<String> scans) {
        this.scans = scans;
    }

    public void setSources(ArrayList<String> sources) {
        this.sources = sources;
    }
}
