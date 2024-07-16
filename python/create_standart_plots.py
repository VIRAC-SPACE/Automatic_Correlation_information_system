import argparse
import sys
import os

from vex import Vex


def main(obs_name, pass_name):
    path = os.environ['dirrectory'] + obs_name + "/results/"
    vex = Vex(os.environ['dirrectory'] + obs_name + "/" + obs_name + ".vix")
    scans = list(vex['SCHED'])
    sources = []
    for scan in scans:
        sources.append(vex['SCHED'][scan]['source'])
    
    sources = list(set(sources))
    stations = list(vex['STATION'])

    standart_plots_path = path + "/" + pass_name + "/" + "standartplots/"
    if not os.path.exists(standart_plots_path):
        os.system("mkdir -p " + standart_plots_path)
            
    for source in sources:
        if not os.path.exists(standart_plots_path + source):
            os.system("mkdir -p " + standart_plots_path + source)

        ms_file = path + "/" + pass_name + "/" + obs_name + "_" + pass_name  + ".ms"
        for station in stations:
            os.system("sh $HOME/scripts/create_standart_plots.sh " + source + " " + obs_name + " " + pass_name + " " + ms_file + " " + station)
        
        plot_files = [file for file in os.listdir(standart_plots_path + source) if file.endswith(".ps")]
         
        for plot_file in plot_files:
           os.system("convert  -rotate 90 " + standart_plots_path + source + "/" + plot_file + " " + standart_plots_path + source + "/" + plot_file.replace(".ps", ".png"))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='create standart plots')
    parser.add_argument('obs_name', type=str, help='observation name')
    parser.add_argument('pass_name', type=str, help='pass name')
    args = parser.parse_args()
    main(args.obs_name, args.pass_name)
    sys.exit(0)

