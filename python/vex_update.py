#! /usr/bin/python2

# Standard Python modules
import os
import sys
import time

import vex
import eop
import gps

from bitstream import create_bitstreams_and_threads_block, get_modes

os.environ['TZ'] = 'UTC'
time.tzset()


def vex2time(str):
    tupletime = time.strptime(str, "%Yy%jd%Hh%Mm%Ss")
    return time.mktime(tupletime)


def time2vex(secs):
    tupletime = time.gmtime(secs)
    return time.strftime("%Yy%jd%Hh%Mm%Ss", tupletime)


def get_start(vex):
    sched = vex['SCHED']
    for scan in sched:
        return sched[scan]['start']
    return ""


def get_stop(vex):
    sched = vex['SCHED']
    for scan in sched:
        start = vex2time(sched[scan]['start'])
        duration = int(sched[scan]['station'][2].split()[0])
        continue
    return time2vex(start + duration)


def update(src, dest, vexfile):
    v = vex.parse(src.read())
    exper = v['GLOBAL']['EXPER']
    start = get_start(v)
    start = vex2time(start)
    tm = time.gmtime(start - 86400)
    
    has_eop = False
    has_clock = False
    has_tapelog_obs = False
    has_THREADS = False
    has_bitstreams = False
    
    threadBlock = ""
    bitstreams = ""

    lines_writed = []
    
    if "EOP" in v:
        has_eop = True
    if "CLOCK" in v:
        has_clock = True
    if "TAPELOG_OBS" in v:
        has_tapelog_obs = True
    if "THREADS" in v:
        has_THREADS = True
    if "BITSTREAMS" in v:
        has_bitstreams = True
    
    src.seek(0)
    
    stations = list()
    for station in v["STATION"]:
        stations.append(station)
    
    mode_index = 0
    station_index = 0
    modes = create_bitstreams_and_threads_block(vexfile)[2]
    modes_like_index = []

    lines = src.readlines()
    for mode in modes:
        for line in lines:
            if line == "def " + mode + ";\n":
                modes_like_index.extend([i for i, x in enumerate(lines) if x == line])
    modes_like_index = sorted(list(set(modes_like_index)))

    if exper in modes:
        modes_like_index.pop(0)

    skip = True
    if len(modes_like_index) == 1:
        skip = False
        
    mode_indx = 0
    for line in lines:
        l = line
        if "ref $EXPER" in line:
            dest.write(line + "     ref $EOP = EOP%d;\n" % tm.tm_yday)
            continue
        
        if "ref $DAS" in line:
            station = stations[station_index]
            dest.write(line + "     ref $CLOCK = %s;\n" % station.upper()  + "     ref $TAPELOG_OBS = %s;\n" % station.upper())
            station_index = station_index + 1 
            continue

        index_for_lines = [i for i, x in enumerate(lines) if x == line]
        for indx in index_for_lines:
            if indx in modes_like_index:
                if not skip:
                    print("yes")
                    mode = modes[mode_indx]
                    block = create_bitstreams_and_threads_block(vexfile)[1][1][mode][0] # threads ielase
                    # block in create_bitstreams_and_threads_block(vexfile)[0][1][mode] bitstream ielase
                    lines_writed.append(l)
                    lines_writed.append("     ref $THREADS =  " + block[0] + ":" + block[1] + ";\n")
                    l = l + "     ref $THREADS =  " + block[0] + ":" + block[1] + ";\n"
                    dest.write(l)
                    mode_indx += 1

                skip = False
            continue

        if line not in lines_writed:
            dest.write(line)

    if not has_THREADS:
        dest.write("*" + 77 * "-" + "\n")
        threadBlock = create_bitstreams_and_threads_block(vexfile)[1][0]
        dest.write(threadBlock)
        
    if not has_bitstreams:
        dest.write("*" + 77 * "-" + "\n")
        bitstreams = create_bitstreams_and_threads_block(vexfile)[0][0]
        dest.write(bitstreams)
    
    if not has_tapelog_obs:
        dest.write("*" + 77 * "-" + "\n")
        dest.write("$TAPELOG_OBS;\n")
        for station in v['STATION']:
            dest.write("*\n")
            dest.write("def %s;\n" % station.upper())
            dest.write("     VSN = 1 : %s-eVLBI : %s : %s;\n"  \
                         % (station, get_start(v), get_stop(v)))
            dest.write("enddef;\n")
            continue
    
    if not has_clock:
        dest.write("*" + 77 * "-" + "\n")
        gps.create_clock_block(v, dest)

    if not has_eop:
        dest.write("*" + 77 * "-" + "\n")
        eop.create_eop_block(v, dest)

    return


if __name__ == "__main__":
    output = open(sys.argv[2], "w")
    fp = open(sys.argv[1], "r")
    vexfile = sys.argv[1]
    update(fp, output, vexfile)
    fp.close()
    output.close()
    sys.exit(0)
