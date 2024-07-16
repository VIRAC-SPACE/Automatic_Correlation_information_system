package com.main.vlbi.vexparser;

import java.util.ArrayList;

public class VexScan {
    private String scanNumber;
    private String startTime;
    private String stopTime;
    private ArrayList<String> stationsArrayList;

    public VexScan(String start, String stop, ArrayList<? extends String> arr, String number) {
        this.scanNumber = number;
        this.startTime = start;
        this.stopTime = stop;
        this.stationsArrayList = new ArrayList<>(arr);
    }

    public String getScanNumber() {
        return scanNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public ArrayList<String> getStationsArrayList() {
        return stationsArrayList;
    }

}