import argparse
import json
import os
import sys


def main(exper_name, pass_name):
    path_corr = os.environ['dirrectory']
    path_data = os.environ['data_directory'] + exper_name + "/"

    control_file_name = path_corr + exper_name + "/" + exper_name + ".ctrl"
    with open(control_file_name, "r") as control_file:
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
    
    scans_times = control_data["scans_times"]
    scan_names = {index: str(index) for index in range(1, len(scans_times) + 1)}

    if not os.path.exists(path_corr + exper_name + "/delays"):
        os.system("mkdir -p " + path_corr + exper_name + "/delays/")

    if not os.path.exists(path_corr + exper_name + "/results"):
        os.system("mkdir -p " + path_corr + exper_name + "/results/" + pass_name)

    control_data["html_output"] = "file://" + path_corr + exper_name + "/results/" + pass_name + "/fringe"
    
    
    for index in range(1, len(scans_times) + 1):
        control_data["scan"] = "000" + str(index)
        control_data["output_file"] = "file://" + exper_name + ".cor_000" + str(index)

        if len(str(scan_names[index])) == 1:
            tmp = "000"
        elif len(str(scan_names[index])) == 2:
            tmp = "00"
        else:
            tmp = "0"

        control_data["data_sources"]["Ir"] = [
            "file://" + path_data + exper_name + "_ir_no" + tmp + scan_names[index] + ".m5a"]
        control_data["data_sources"]["Ib"] = [
            "file://" + path_data + exper_name + "_ib_no" + tmp + scan_names[index] + ".m5a"]

        control_data["start"] = scans_times[index - 1][0]
        control_data["stop"] = scans_times[index - 1][1]

        if pass_name == "line":
            control_data["number_channels"] = 4096
            control_data["channels"] = ["CH05", "CH06"]
        elif pass_name == "all":
            control_data["number_channels"] = 4096
            control_data["channels"] = ["CH01", "CH02", "CH03", "CH04", "CH05", "CH06", "CH07", "CH08",
                                        "CH09", "CH10", "CH11", "CH12", "CH13", "CH14", "CH15", "CH16"]
        else:
            control_data["number_channels"] = 128
            control_data["channels"] = ["CH01", "CH02", "CH03", "CH04", "CH05", "CH06", "CH07", "CH08",
                                        "CH09", "CH10", "CH11", "CH12", "CH13", "CH14", "CH15", "CH16"]

        with open(control_file_name, "w") as control_file2:
            control_file2.write(json.dumps(control_data, indent=2))

        os.system("cd " + path_corr + exper_name + "/" + " &&" + " rm -rfv delays/* ")
        os.system("cd " + path_corr + exper_name + "/" + " &&" + " rm -rfv chex* ")
        os.system("cd " + path_corr + exper_name + "/" + " &&" + " rm -rfv dynamic_channel_extractor* ")
        os.system("cd " + path_corr + exper_name + "/" + " &&" +
                  " mpirun --debug-daemons -np 16  --hostfile machines -mca /  rankfile  sfxc " +
                  exper_name + ".ctrl " + exper_name + ".vix")

        os.system("cd " + path_corr + exper_name + "/" + " &&" +
                  " python2 " + os.environ['produce_html_plotpage'] + exper_name + ".vix " + exper_name + ".ctrl")

        scan_name = "No" + tmp + scan_names[index]
        os.system("python2 $HOME/python/html_plot_page_multi.py " + exper_name + " " + scan_name + " " + pass_name)
    
    os.system("sh $HOME/scripts/fits_files.sh " + exper_name + " " + pass_name)
    os.system("python2 $HOME/python/create_standart_plots.py " + exper_name + " " + pass_name)

    if not os.path.exists(path_corr + exper_name + "/results/" + pass_name + "/cor_files"):
        os.system("mkdir " + path_corr + exper_name + "/results/" + pass_name + "/cor_files")

    os.system("mv " + path_corr + exper_name + "/" + exper_name + ".cor* " +
              path_corr + exper_name + "/results/" + pass_name + "/cor_files/")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Correlate all scans')
    parser.add_argument('exper_name', type=str, help='Experiment name')
    parser.add_argument('pass_name', type=str, help='pass name')
    args = parser.parse_args()
    main(args.exper_name, args.pass_name)
    sys.exit(0)
