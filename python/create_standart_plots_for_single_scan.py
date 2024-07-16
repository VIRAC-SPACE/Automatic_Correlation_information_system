import argparse
import sys
import os

from vex import Vex


def main(obs_name, scan):
    path = os.environ['dirrectory'] + obs_name + "/results/"
    vex = Vex(os.environ['dirrectory'] + obs_name + "/" + obs_name + ".vix")
    source = vex["SCHED"][scan]["source"]

    standart_plots_path = path + "/" + "standartplots/"
    if not os.path.exists(standart_plots_path + "/" + source):
        os.system("mkdir -p " + standart_plots_path + "/" + source)

    standarplots = os.environ['standardplots']
    ms_file = obs_name + "_" + scan + ".ms"

    os.system("cd " + path + " && " + standarplots + " " + ms_file + " ir " + " -weight " + source)
    plot_files = [file for file in os.listdir(path) if file.endswith(".ps")]
    for plot_file in plot_files:
        os.system("convert -quality 300 -colorspace RGB -rotate 90 " + path + plot_file + " " +
                  path + plot_file.replace(".ps", ".png"))
    os.system("cd " + path + " && " + "mv *.png " + standart_plots_path  + "/" + source)
    os.system("mv " + path + "*.ps " + standart_plots_path  + "/" + source)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='create standart plots')
    parser.add_argument('obs_name', type=str, help='observation name')
    parser.add_argument('scan', type=str, help='scan name')
    args = parser.parse_args()
    main(args.obs_name, args.scan)
    sys.exit(0)

