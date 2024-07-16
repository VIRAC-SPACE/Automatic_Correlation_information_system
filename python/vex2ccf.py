#! /usr/bin/python2

# Standard Python modules.
import os
import sys
import time

# The json module is new in Python 2.6; fall back on simplejson if it
# isn't available.
try:
    import json
except ImportError:
    import simplejson as json
    pass

# JIVE Python modules.
from vex import Vex

# Proper time.
os.environ['TZ'] = "UTC"
time.tzset()


def vex2time(str):
    tupletime = time.strptime(str, "%Yy%jd%Hh%Mm%Ss")
    return time.mktime(tupletime)


def time2vex(secs):
    tupletime = time.gmtime(secs)
    return time.strftime("%Yy%jd%Hh%Mm%Ss", tupletime)


def get_mode(vex):
    sched = vex['SCHED']
    for scan in sched:
        break
    return sched[scan]['mode']


def get_scan_stop(scan):
    time = vex2time(scan['start'])
    time += max(float(station[2].split()[0]) for station in scan.getall('station'))
    return time2vex(time)


def get_start(vex):
    sched = vex['SCHED']
    for scan in sched:
        return sched[scan]['start']
    return ""


def get_stop(vex):
    sched = vex['SCHED']
    for scan in sched:
        continue
    return get_scan_stop(sched[scan])


def get_scans(vex):
    sched = vex['SCHED']
    return [(sched[scan]['start'], get_scan_stop(sched[scan])) for scan in sched]


def get_stations(vex):
    return [station for station in vex['STATION']]


def get_channels(vex):
    mode = get_mode(vex)
    freq = vex['MODE'][mode]['FREQ'][0]
    channels = []
    for chan_def in vex['FREQ'][freq].getall('chan_def'):
        channels.append(chan_def[4])
        continue
    return channels


def vex2ccf(vex):
    json_output = dict()
    json_output["exper_name"] = vex["GLOBAL"]["EXPER"]
    json_output["channels"] = get_channels(vex)
    json_output["start"] = get_start(vex)
    json_output["stop"] = get_stop(vex)
    json_output["reference_station"] = ""
    json_output["setup_station"] = str(get_stations(vex)[0])
    json_output["cross_polarize"] = True
    json_output["multi_phase_center"] = False
    json_output["sub_integr_time"] = 15000
    json_output["fft_size_correlation"] = 8192
    json_output["fft_size_delaycor"] = 8192
    json_output["number_channels"] = 1024
    json_output["integr_time"] = 2.0
    json_output["message_level"] = 1
    json_output["delay_directory"] = "file://delays"
    json_output["output_file"] = "file://" + vex["GLOBAL"]["EXPER"] + ".cor_0001"
    json_output["data_sources"] = {}
    json_output["stations"] = get_stations(vex)
    json_output["scans"] = []
    json_output["scans_times"] = get_scans(vex)
    return json_output


def main():
    os.environ['TZ'] = "UTC"
    time.tzset()
    vex = Vex(sys.argv[1])
    control_file = open(sys.argv[2], "w")
    control_file.write(json.dumps(vex2ccf(vex), indent=4))


if __name__ == "__main__":
    main()
    sys.exit(0)
    
