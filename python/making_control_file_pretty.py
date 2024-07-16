import os
import pprint
import sys

try:
    import json
except ImportError:
    import simplejson as json

if __name__ == "__main__":
    control_file_ugly = sys.argv[1]  # TMP controlFile
    control_file = sys.argv[2]  # original controlFile

    with open(control_file_ugly, "r") as data_file:
        data_ugly = json.load(data_file)
        
    with open(control_file, "r") as data_file2:
        data_original = json.load(data_file2)

    data_original["stations"] = data_ugly["stations"]
    data_original["channels"] = data_ugly["channels"]
    data_original["cross_polarize"] = data_ugly["cross_polarize"]
    data_original["stop"] = data_ugly["stop"]
    data_original["number_channels"] = data_ugly["number_channels"]
    data_original["exper_name"] = data_ugly["exper_name"].encode('utf8')
    data_original["message_level"] = data_ugly["message_level"]
    data_original["integr_time"] = data_ugly["integr_time"]
    data_original["start"] = data_ugly["start"]
    data_original["delay_directory"] = data_ugly["delay_directory"]
    data_original["reference_station"] = data_ugly["reference_station"]
    data_original["output_file"] = data_ugly["output_file"]
    data_original["scan"] = data_ugly["scan"]
    data_original["setup_station"] = data_ugly["setup_station"]
    data_original["multi_phase_scans"] = data_ugly["multi_phase_scans"]
    data_original["sub_integr_time"] = data_ugly["sub_integr_time"]
    data_original["multi_phase_center"] = data_ugly["multi_phase_center"]
    data_original["fft_size_correlation"] = data_ugly["fft_size_correlation"]
    data_original["fft_size_delaycor"] = data_ugly["fft_size_delaycor"]
    data_original["html_output"] = data_ugly["html_output"]

    if "scans" in data_original.keys() and len(data_original["scans"]) == 0:
        del data_original["scans"]

    if not data_original["multi_phase_center"]:
        del data_original["multi_phase_center"]
        del data_original["fft_size_correlation"]
        del data_original["fft_size_delaycor"]
        del data_original["multi_phase_scans"]
    
    for source in range(0, len(data_ugly["data_sources"])):
        data_original["data_sources"] = {}
        data_original["data_sources"][data_ugly["data_sources"][source].keys()[0]] = \
            data_ugly["data_sources"][source].values()[0]
        
    delKeys = list()
    for source2 in range(0, len(data_original["data_sources"])):
        if len(data_original["data_sources"].values()[source2]) == 0:
            delKeys.append(data_original["data_sources"].keys()[source2])
            
    for key in range(0, len(delKeys)):
        del data_original["data_sources"][delKeys[key]]

    # pprint.pprint(data_original,  indent=4)

    with open(control_file, "w") as control_file2:
        control_file2.write(str(json.dumps(data_original, indent=2)))

    os.remove(control_file_ugly)
    sys.exit(0)
