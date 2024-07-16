#! /usr/local/bin/python3.7
import sys, re, os, json
from collections import OrderedDict
from decimal import Decimal
from vex import Vex
import re
import codecs
from pprint import pprint

def get_mode(vex):
    sched = vex["SCHED"]
    for scan in sched:
        break
    return sched[scan]["mode"]

def get_discreditation(vex):
    mode = get_mode(vex)
    freq = vex["MODE"][mode]["FREQ"][0]
    matches = re.search("x(\\d+)MHz", freq)
    if not matches:
        print("Error: Cannot find frequency")
    return int(matches.group(1) + ("0" * 6))

def get_length(vex):
    sched = vex["SCHED"]
    for scan in sched:
        break
    length_str = sched[scan]["station"][2]
    return [int(s) for s in length_str.split() if s.isdigit()][0]

def doc2dic(filename):
    if not os.path.isfile(filename + ".doc"):
        print(filename + ".doc not found")
        exit(-1)
    os.system("antiword " + filename + ".doc > " + filename + ".txt")
    with open(filename + '.txt', 'r') as myfile:
        data = myfile.read().replace('\n', '').replace(' ', '')
    data = re.sub(".*\\|A3\\|\\|", "", data)
    data = re.sub("\\|\\|", "\n", data)
    
    jsonOut = list()
    for line in data.splitlines():
        lineList = line.split("|")
        jsonOut.append(OrderedDict())
        jsonOut[-1]["object"] = lineList[0]
        jsonOut[-1]["HH"] = lineList[1]
        jsonOut[-1]["MM"] = lineList[2]
        jsonOut[-1]["SS"] = lineList[3]
        jsonOut[-1]["FsF"] = lineList[4]
        jsonOut[-1]["F interf"] = lineList[5]
        jsonOut[-1]["A0"] = lineList[6]
        jsonOut[-1]["A1"] = lineList[7]
        jsonOut[-1]["A2"] = lineList[8]
        jsonOut[-1]["A3"] = lineList[9]
    os.system("rm " + filename + ".txt")
    return jsonOut

def vex2dic(filename, highest_job):
    if not os.path.isfile(filename + ".vax"):
        print(filename + ".vax not found")
        exit(-1)
    vex = Vex(filename + ".vax")
    json_out = OrderedDict()

    json_out["JOBS"] = [highest_job]
    json_out["OBSERVATION"] = vex["GLOBAL"]["EXPER"]
    json_out["OBSERVATION_LENGTH"] = get_length(vex)
    json_out["DISCRETIZATION(Hz)"] = get_discreditation(vex)

    json_out["M1_BUFFER_BEGIN"] = sys.argv[3]+"s"
    json_out["M1_BUFFER_END"] = sys.argv[4]+"s"
    json_out["M1_FFT_COMPRESS"] = 0.00005
    json_out["M1_SPECTRA_WINDOW(Hz)"] = 100_000

    if highest_job != 'M1':
        json_out["M2_Tu"] = 0.0002

    if highest_job == 'M3':
        json_out["M3_DELAY_BEGIN"] = sys.argv[5]
        json_out["M3_DELAY_END"] = sys.argv[6]

    json_out["STATIONS"] = OrderedDict()
    for station in vex["STATION"]:
        json_out["STATIONS"][station] = OrderedDict()

    return json_out

def get_BBCs(filename):
    BBCs = {}
    if not os.path.isfile(filename + ".log"):
        print(filename + ".log not found. continuing without..")
    else:
        for line in codecs.open(filename + ".log", "r", "utf-8", "ignore"):
            match = re.search(r'/(bbc(\d)+)[/=] *([\d\.]+)', line)
            if match != None:
                BBCs[match.group(1).upper()] = float(match.group(3))
    return BBCs

def get_RFs(vex, station):
    mode = get_mode(vex)
    freq = "0"
    for f in vex["MODE"][mode].getall("FREQ"):
        if f[1] == station:
            freq = f[0]
            break
    rfs = []
    for chan_def in vex["FREQ"][freq].getall("chan_def"):
        rf = float(re.search(r'([\d\.]+) *MHz', chan_def[1]).group(1)) * 1_000_000
        rfs.append(rf)
    return rfs

def get_LO(vex, station):
    mode = get_mode(vex)
    freq = "LO@0MHz"
    for i in vex["MODE"][mode].getall("IF"):
        if i[1] == station:
            freq = i[0]
            break
    lo = float(re.search(r'LO@([\d\.]+) *MHz', freq).group(1)) * 1_000_000
    return lo

def get_IFs(vex, station, filename):
    mode = get_mode(vex)
    bbcs = get_BBCs(station + "_" + filename)
    freq = "0"
    for f in vex["MODE"][mode].getall("FREQ"):
        if f[1] == station:
            freq = f[0]
            break
    ifs = []
    for chan_def in vex["FREQ"][freq].getall("chan_def"):
        If = 0
        if chan_def[5] in list(bbcs.keys()):
            If = float(bbcs[chan_def[5]]) * 1_000_000
        ifs.append(If)
    return ifs

def get_spike_freqs(station, filename):
    if not os.path.isfile(filename + ".vax"):
        print(filename + ".vax not found")
        exit(-1)
    vex = Vex(filename + ".vax")
    #rfs = get_RFs(vex, station)
    rf = 6668519200
    lo = get_LO(vex, station)
    ifs = get_IFs(vex, station, filename)
    spike_freqs = []
    for i in range(len(ifs)):
        if ifs[i] == 0:
            spike_freqs.append(get_discreditation(vex) / 2)
        else:
            spike_freqs.append(int(rf - (lo + ifs[i])))
    return spike_freqs

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Error: arguments not provided (makectrl.py [HIGHEST_JOB] [PATH])")
        exit(-1)
    highest_job = sys.argv[1].upper()
    path = os.path.split(sys.argv[2])
    os.chdir(path[0])
    filename = path[1]
    json_out = vex2dic(filename, highest_job)
    for station in json_out["STATIONS"]:
        if os.path.isfile(station + "_" + filename + ".log"):
            json_out["STATIONS"][station]["TARGET(Hz)"] = get_spike_freqs(station, filename)
        if os.path.isfile(station + "_" + filename + ".doc"):
            json_out["STATIONS"][station]["COEFF"] = doc2dic(station + "_" + filename)
    control_file = open(filename + ".ctrl", "w")
    control_file.write(json.dumps(json_out, indent=4))
    sys.exit(0)
    
