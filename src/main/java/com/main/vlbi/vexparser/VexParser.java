package com.main.vlbi.vexparser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VexParser {
    private String path;
    private String line = "";
    private String startTime = "";
    private String stopTime = "";
    private String experimentName = "";
    private List<String> vex_file_lines;

    public VexParser(String path) {
        this.path = path;

        Path vex_file_path = Paths.get(this.path);
        try {
            this.vex_file_lines = Files.readAllLines(vex_file_path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VexParser(String path, List<String> vex_file_lines) {
        this.path = path;
        this.vex_file_lines = vex_file_lines;
    }

    public String getExperimentName() {
        returnExperimentName();
        return experimentName.trim();
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    private String returnExperimentName() {
        String line = "";

        for (String vex_file_line : this.vex_file_lines) {
            line = vex_file_line;
            if (line.startsWith("*")) {
                continue;
            } else if (line.contains("exper_name")) {
                experimentName = line.split("=")[1];
                int idx = experimentName.indexOf(";");
                experimentName = experimentName.substring(0, idx);
                setExperimentName(experimentName);
                break;
            }
        }

        return line.trim();
    }

    public String getStartTime() {
        for (String vex_file_line : this.vex_file_lines) {
            if (vex_file_line.contains("exper_nominal_start")) {
                this.startTime = vex_file_line.split("=")[1].trim().replace(";", "");
                break;
            }

        }
        return this.startTime;
    }

    public ArrayList<String> getSources() {
        ArrayList<String> sources = new ArrayList<>();
        for (String vex_file_line : this.vex_file_lines) {
            if (vex_file_line.contains("source = ")) {
                String source = vex_file_line.split("=")[1].trim().replace(";", "");
                if (!sources.contains(source)){
                    sources.add(source);
                }
            }
        }
        return sources;
    }


    public String getStopTime() {
        for (String vex_file_line : this.vex_file_lines) {
            if (vex_file_line.contains("exper_nominal_stop")) {
                this.stopTime = vex_file_line.split("=")[1].trim().replace(";", "");
                break;
            }
        }
        return this.stopTime;
    }

    public String getdate() {
        String date = null;
        for (String vex_file_line : this.vex_file_lines) {
            if (vex_file_line.contains("*    date     : ")) {
                date=vex_file_line.split(":")[1];
                break;
            }
        }
        return date;
    }

    public String getSource(String scan) {
        String source = null;
        for (String vex_file_line : this.vex_file_lines) {
            if (vex_file_line.contains(scan)) {
                int line_index = this.vex_file_lines.indexOf(vex_file_line);
                String line = this.vex_file_lines.get(line_index + 3);
                source = line.split("=")[1].trim().replace(";", "");
                break;
            }
        }
        return source;
    }

    public ArrayList<VexScan> extractSched() {
        ArrayList<VexScan> vexScansList = new ArrayList<>();
        ArrayList<String> stationList = new ArrayList<>();
        int sched_start_index = 0;

        String scan_number = "";
        String start = "";
        String stop = "";

        for (int i = 0; i < this.vex_file_lines.size(); i++) {
            line = this.vex_file_lines.get(i);
            if (line.contains("SCHED")) {
                sched_start_index = i;
            }
        }
        for (int i = sched_start_index; i < this.vex_file_lines.size(); i++) {
            line = this.vex_file_lines.get(i);
            if (line.startsWith("*")) {
                continue;
            } else if (line.startsWith("scan")) {
                scan_number = line.split(" ")[1].trim().replace(";", "");
            } else if (line.contains("start")) {
                start = line.split("=")[1].trim().replace(";", "");
            } else if (line.contains("station")) {
                String station = line.split("=")[1].split(":")[0].trim();
                stationList.add(station);
                int duration = Integer.parseInt(line.split("=")[1].split(":")[2].trim().replace("sec", "").trim());
                int mins_tmp = duration / 60;
                int secs_tmp = duration - (mins_tmp * 60);
                String year = start.substring(0, 4);
                String days = start.substring(5, 8);
                String hours = start.substring(9, 11);
                String minutes = start.substring(12, 14);
                String secundes = start.substring(15, 17);
                minutes = Integer.toString(Integer.parseInt(minutes) + mins_tmp);

                if (secs_tmp > 0) {
                    secundes = Integer.toString(Integer.parseInt(secundes) + secs_tmp);
                    if (Integer.parseInt(secundes) > 59) {
                        secundes = "00";
                        minutes = Integer.toString(Integer.parseInt(minutes) + 1);
                    } else if (secundes.length() == 1) {
                        secundes = "0" + secundes;
                    }
                }

                if (Integer.parseInt(minutes) > 59) {
                    minutes = "00";
                    hours = Integer.toString(Integer.parseInt(hours) + 1);
                } else if (minutes.length() == 1) {
                    minutes = "0" + minutes;
                }

                if (Integer.parseInt(hours) > 23) {
                    days = Integer.toString(Integer.parseInt(days) + 1);
                    hours = "00";
                } else if (hours.length() == 1) {
                    hours = "0" + hours;
                }

                stop = year + "y" + days + "d" + hours + "h" + minutes + "m" + secundes + "s";
            } else if (line.contains("endscan")) {
                VexScan vexScan = new VexScan(start, stop, stationList, scan_number);
                vexScansList.add(vexScan);
                stationList.clear();
            }
        }
        return vexScansList;
    }


    public int getChannels() {
        return extractChannels();
    }

    private int extractChannels() {
        int numOfChannels = 0;
        for (String vex_file_line : this.vex_file_lines) {
            line = vex_file_line;
            if (line.contains("chan_def =")) {
                numOfChannels++;
            }
        }
        return numOfChannels;
    }

}
