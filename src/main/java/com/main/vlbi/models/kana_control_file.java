package com.main.vlbi.models;

public class kana_control_file {
    private String job;
    private String bufferBegin;
    private String bufferEnd;
    private String delayBegin;
    private String delayEnd;

    private  String  dataDir;
    private String realTime;
    private String kanaInAddress;
    private int kanaInPort;
    private String kanaOutAddress;
    private int kanaOutPort;

    private int rt_batch_length;

    private String correlator;

    public void setRt_batch_length(int rt_batch_length) {
        this.rt_batch_length = rt_batch_length;
    }

    public int getRt_batch_length() {
        return rt_batch_length;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public void setRealTime(String realTime) {
        this.realTime = realTime;
    }

    public void setKanaInAddress(String kanaInAddress) {
        this.kanaInAddress = kanaInAddress;
    }

    public void setKanaInPort(int kanaInPort) {
        this.kanaInPort = kanaInPort;
    }

    public void setKanaOutAddress(String kanaOutAddress) {
        this.kanaOutAddress = kanaOutAddress;
    }

    public void setKanaOutPort(int kanaOutPort) {
        this.kanaOutPort = kanaOutPort;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getRealTime() {
        return realTime;
    }

    public String getKanaInAddress() {
        return kanaInAddress;
    }

    public int getKanaInPort() {
        return kanaInPort;
    }

    public String getKanaOutAddress() {
        return kanaOutAddress;
    }

    public int getKanaOutPort() {
        return kanaOutPort;
    }

    public String getBufferBegin() {
        return bufferBegin;
    }

    public void setBufferBegin(String bufferBegin) {
        this.bufferBegin = bufferBegin;
    }

    public String getBufferEnd() {
        return bufferEnd;
    }

    public void setBufferEnd(String bufferEnd) {
        this.bufferEnd = bufferEnd;
    }

    public String getDelayBegin() {
        return delayBegin;
    }

    public void setDelayBegin(String delayBegin) {
        this.delayBegin = delayBegin;
    }

    public String getDelayEnd() {
        return delayEnd;
    }

    public void setDelayEnd(String delayEnd) {
        this.delayEnd = delayEnd;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String input) {
        job = input;
    }

    public String getCorrelator() {
        return correlator;
    }

    public void setCorrelator(String correlator) {
        this.correlator = correlator;
    }
}
