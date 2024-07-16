package com.main.vlbi.models;

import java.util.ArrayList;

public class Scan {
    private ArrayList<String> stationLists;
    private String starTime;
    private String stopTime;
    private String scanNumber;

    public Scan() {
    }

    public void setScanNumber(String scanNumber) {
        this.scanNumber = scanNumber;
    }

    public void setStationLists(ArrayList<String> stationArray) {
        this.stationLists = stationArray;
    }

    public void setStarTime(String startTime) {
        this.starTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

}