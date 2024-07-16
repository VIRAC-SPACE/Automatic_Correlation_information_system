package com.main.vlbi.models;

import java.util.ArrayList;
import java.util.Map;

public class Control_file {
    private String scan;
    private String output_file;
    private int number_channels;
    private boolean cross_polarize;
    private String stop;
    private String start;
    private ArrayList<String> stations;
    private ArrayList<String> channels;
    private String exper_name;
    private int message_level;
    private Double integr_time;
    private String delay_directory;
    private String reference_station;
    private String html_output;
    private String setup_station;
    private ArrayList<String> multi_phase_scans;
    private Double sub_integr_time;
    private boolean multi_phase_center;
    private int fft_size_correlation;
    private int fft_size_delaycor;

    private ArrayList<Map<String, ArrayList<String>>> data_sources;

    private String correlator;

    public Control_file() {
        stations = new ArrayList<>();
        channels = new ArrayList<>();
        data_sources = new ArrayList<>();
        multi_phase_scans = new ArrayList<>();
    }

    public void setOutput_file(String output_file) {
        this.output_file = output_file;
    }

    public void setDelay_directory(String delay_directory) {
        this.delay_directory = delay_directory;
    }

    public ArrayList<Map<String, ArrayList<String>>> getData_sources() {
        return data_sources;
    }

    public String getScan() {
        return scan;
    }

    public void setHtml_output(String html_output) {
        this.html_output = html_output;
    }

    public String getCorrelator() {
        return correlator;
    }

    public void setCorrelator(String correlator) {
        this.correlator = correlator;
    }

    public String getExper_name() {
        return this.exper_name;
    }
}