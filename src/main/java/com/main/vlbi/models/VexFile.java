package com.main.vlbi.models;

public class VexFile {
    private String vexName;
    private String vexfile;
    private String type;
    private int addClock;

    private String corrType;

    private String passType;

    public String getVexName() {
        return vexName;
    }

    public String getVexfile() {
        return vexfile;
    }

    public int getAddClock() {
        return addClock;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCorrType() { return corrType; }

    public String getPassType() {
        return passType;
    }
}
