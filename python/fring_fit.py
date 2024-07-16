import sys
import os
import argparse
import time
import json

import numpy as np

from vex import Vex
from sfxcdata import SFXCData

os.environ['TZ'] = 'UTC'
time.tzset()


def change_clock_line(line, r, o):
    l = line.split(":")
    l[1] = str(r) + " usec"
    l[3] = o + "\n"
    
    l = " : ".join(l)
    return l
    
    
def get_old_clocks(vix_file):
    with open(vix_file, "r") as vix_data:
        vix_lines = vix_data.readlines()
    
    clock_block_start_index = 0    
    for l in range(0, len(vix_lines)):
       if "$CLOCK;" in vix_lines[l]:
           clock_block_start_index = l
           break
    
    rt32_clock_vix_index = 0
    rt16_clock_vix_index = 0       
    for l in range(clock_block_start_index, len(vix_lines)):
        if "def IR;" in vix_lines[l]:
            rt32_clock_vix_index = l + 1
        elif  "def IB;" in vix_lines[l]:
            rt16_clock_vix_index = l + 1
            
            
    rt32 = vix_lines[rt32_clock_vix_index]
    rt16 = vix_lines[rt16_clock_vix_index]        
    return {"rt32_clock":[float(rt32.split(":")[1].replace("usec", "").strip()), rt32.split(":")[3].strip()], 
            "rt16_clock":[float(rt16.split(":")[1].replace("usec", "").strip()), rt16.split(":")[3].strip()]}       


def get_pols(ch):
    pol1 = ch[2]
    pol2 = ch[3]
    
    return (pol1, pol2)


def get_cross_key_index(keys):
    for key in keys:
        if "Ib" in key and "Ir" in key:
            return keys.index(key)


def get_sfxc_offsets(cor_file):
    data = SFXCData(cor_file)
    nchan = data.nchan
    
    integr = {}
    vis =  data.vis
    cross_key_index = get_cross_key_index(data.vis.keys())
    bl = data.vis.keys()[cross_key_index]
    vis = vis[bl]
    a = 0
    
    offsets = []
    while True:
        for ch in data.vis[bl]:
            pol1, pol2 = get_pols(ch)
            if pol1 == pol2:
                if ch not in integr.keys():
                    integr[ch] = np.zeros(nchan+1, dtype='c16')  
		    integr[ch] += data.vis[bl][ch].vis
		else:
		    integr[ch] += data.vis[bl][ch].vis
		    	    
	a += 1   
        if not data.next_integration():
            break
            
    for ch in integr:
        vis = integr[ch]
        n = vis.size
        lags = abs(np.fft.fftshift(np.fft.irfft(vis)))
        lag = lags.argmax() - n + 1
	offsets.append(np.abs(lag))
	
    return offsets	    


def vex2time(str):
    tupletime = time.strptime(str, "%Yy%jd%Hh%Mm%Ss")
    return time.mktime(tupletime)
    
    
def time2vex(secs):
    tupletime = time.gmtime(secs)
    return time.strftime("%Yy%jd%Hh%Mm%Ss", tupletime)


def main(exper):
    os.environ['TZ'] = 'UTC'
    time.tzset()
    
    path_corr = os.environ['dirrectory'] + exper
    path_data = os.environ['data_directory'] + exper.lower() + "/" + exper.lower()
    
    vex_file = path_corr + "/" + exper + ".vex"
    vix_file = path_corr + "/" + exper + ".vix"
    ctrl_file = path_corr + "/" + exper + ".ctrl"
    key_file = path_corr + "/" + exper + ".key"
    
    old_clocks = get_old_clocks(vix_file)
       
    fring_sources = []
    with open(key_file) as kf:
        kf_lines = kf.readlines()
        for line in kf_lines:
            if "fringe" in line and line.split("=")[1].split()[0].replace("'", "") not in fring_sources:
                fring_sources.append(line.split("=")[1].split()[0].replace("'", ""))
              
    vex = Vex(vex_file)
    sched = vex["SCHED"]
    
    fring_scans = []
    for scan in sched:
        
        if sched[scan]["source"] in fring_sources:
            fring_scans.append(scan)
            
    fring_fit_scan = ""
    for scan in fring_scans:
        file_name_ir = path_data + "_ir_" + scan.lower() + ".m5a"
        file_name_ib = path_data + "_ib_" + scan.lower() + ".m5a"
        
        print(file_name_ir)
        print(file_name_ib)
        
        if os.path.isfile(file_name_ir) and os.path.isfile(file_name_ib):
           fring_fit_scan = scan
           break
          
    if len(fring_fit_scan) == 0:
        print("fring fit failled no data files")
        sys.exit(1)
    else:       
        print("fring fit will be done with scan " + fring_fit_scan)
    
    
    with open(ctrl_file, "r") as control_file:
        control_data = json.load(control_file)
    
    if "multi_phase_scans" in control_data.keys():
        del control_data["multi_phase_scans"]
        
    if "fft_size_correlation" in control_data.keys():
        del control_data["fft_size_correlation"]
        
    if "multi_phase_center" in control_data.keys():
        del control_data["multi_phase_center"]
        
    if "fft_size_delaycor" in control_data.keys():
        del control_data["fft_size_delaycor"] 
        
    if "scans" in control_data.keys():
        del control_data["scans"] 
        
    control_data["scan"] = fring_fit_scan
    control_data["output_file"] = "file://" + exper + ".cor_" + fring_fit_scan.replace("No", "")
        
    control_data["number_channels"] = 1024
    control_data["channels"] = ["CH01", "CH02", "CH03", "CH04", "CH05", "CH06", "CH07", "CH08",
                                        "CH09", "CH10", "CH11", "CH12", "CH13", "CH14", "CH15", "CH16"]
                                        
    control_data["data_sources"]["Ir"] = ["file://" + path_data + "_ir_" + fring_fit_scan.lower() + ".m5a"]
    control_data["data_sources"]["Ib"] = ["file://" + path_data + "_ib_" + fring_fit_scan.lower() + ".m5a"]
    
    scans_times = control_data["scans_times"]
    control_data["start"] = scans_times[int(fring_fit_scan.replace("No", ""))-1][0]
    control_data["stop"] = time2vex(vex2time(scans_times[int(fring_fit_scan.replace("No", ""))-1][0]) + 60)
    
    pass_name = "fringe"    
    if not os.path.exists(path_corr + "/delays"):
        os.system("mkdir -p " + path_corr + "/delays/")
    
    if not os.path.exists(path_corr + "/results"):
        os.system("mkdir -p " + path_corr + "/results/")
            
    bw = float(vex["FREQ"].items()[0][1]["chan_def"][3].replace("MHz", ""))   
    
    
    vex_out_tmp = vix_file + "tmp"
    os.system("cp " + vix_file + " " + vex_out_tmp)
    
    vix_lines = list()
    with open(vix_file, 'r') as contents:
        lines = contents.readlines()
        for line in lines:
            vix_lines.append(line)
            
    iter_names = ["rt32_add", "rt32_minus", "rt16_add", "rt16_minus"]    
    for iter in range(0, 5):
        
        control_data["html_output"] = "file://" + path_corr  + "/results/clock_search" + "/" + "iter_" + str(iter)
       
        with open(ctrl_file, "w") as control_file2:
            control_file2.write(json.dumps(control_data, indent=2))
        print("created control file", ctrl_file)
                
        os.system("cd " + path_corr  + "/" + " &&" + " rm -rfv delays/* ")
        os.system("cd " + path_corr  + "/" + " &&" + " rm -rfv chex* ")
        os.system("cd " + path_corr  + "/" + " &&" + " rm -rfv dynamic_channel_extractor* ")
        os.system("cd " + path_corr  + "/" + " &&" +
                  " mpirun --debug-daemons -np 16  --hostfile machines -mca /  rankfile  sfxc " + ctrl_file + " " + vix_file)
        os.system("cd " + path_corr + "/" + " &&" + " python2 " + os.environ['produce_html_plotpage']  + vix_file + " " + ctrl_file)
        
        cor_file = path_corr + "/" + exper + ".cor_" + fring_fit_scan.replace("No", "")
        sfxc_offsets = get_sfxc_offsets(cor_file)
        
        if np.std(sfxc_offsets) > 2:
            print("Clock search failed offset for each channel are to different")
            sys.exit(1)
            
        elif np.std(sfxc_offsets) < 2 and np.mean(sfxc_offsets) <= 1:
            print("Clock search done")
            break
       
        else:
            if iter < 4:
                avg_sfxc_offset = np.mean(sfxc_offsets)
                sfxc_offset_change = avg_sfxc_offset * (1/(bw*2))
        
                for l in range(0, len(vix_lines)):
                    if "clock_early" in vix_lines[l]:
                        telescope = vix_lines[l-1].split()[1].replace(";", "")
                        iter_name = iter_names[iter]
                        
                        if iter_name == "rt32_add":
                             if telescope == "IR":
                                 gps_offset = old_clocks["rt32_clock"][0] 
                                 gps_rates = old_clocks["rt32_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset + sfxc_offset_change, gps_rates) 
                             elif telescope == "IB":
                                 gps_offset = old_clocks["rt16_clock"][0] 
                                 gps_rates = old_clocks["rt16_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset, gps_rates)
                                  
                        elif iter_name == "rt32_minus":
                             if telescope == "IR":
                                 gps_offset = old_clocks["rt32_clock"][0] 
                                 gps_rates = old_clocks["rt32_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset - sfxc_offset_change, gps_rates) 
                             elif telescope == "IB":
                                 gps_offset = old_clocks["rt16_clock"][0] 
                                 gps_rates = old_clocks["rt16_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset, gps_rates)
                                 
                        elif iter_name == "rt16_add":
                             if telescope == "IR":
                                 gps_offset = old_clocks["rt32_clock"][0] 
                                 gps_rates = old_clocks["rt32_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset, gps_rates) 
                             elif telescope == "IB":
                                 gps_offset = old_clocks["rt16_clock"][0] 
                                 gps_rates = old_clocks["rt16_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset + sfxc_offset_change, gps_rates)
                                 
                                 
                        elif iter_name == "rt16_minus":
                             if telescope == "IR":
                                 gps_offset = old_clocks["rt32_clock"][0] 
                                 gps_rates = old_clocks["rt32_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset, gps_rates) 
                             elif telescope == "IB":
                                 gps_offset = old_clocks["rt16_clock"][0] 
                                 gps_rates = old_clocks["rt16_clock"][1]
                                 vix_lines[l] = change_clock_line(vix_lines[l], gps_offset - sfxc_offset_change, gps_rates)    
                                                            
                with open(vix_file, "w") as vix_out:
                    for v in vix_lines:
                        vix_out.write(v)
                
        
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Create clock search')
    parser.add_argument('exper_name', type=str, help='Experiment name')
    args = parser.parse_args()
    main(args.exper_name)
    sys.exit(0)
