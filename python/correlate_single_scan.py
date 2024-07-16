import argparse
import os
import sys


def main(exper_name, scan_name):
    path_corr = os.environ['dirrectory']
    
    if not os.path.exists(path_corr +  exper_name + "/delays"):
        os.system("mkdir " + path_corr  +  exper_name + "/delays")
    
    os.system("cd " + path_corr + exper_name + "/" + " &&" +
              " mpirun --debug-daemons -np 16  --hostfile machines -mca /  rankfile  sfxc " +
              exper_name + ".ctrl " + exper_name + ".vix")

    os.system("cd " + path_corr + exper_name + "/" + " &&" + " python2 " + os.environ['produce_html_plotpage'] + exper_name + ".vix " + exper_name + ".ctrl")

    os.system("python2 $HOME/python/html_plot_page.py " + exper_name + " " + scan_name.replace("n", "N"))
    os.system("sh $HOME/scripts/fits_files_single.sh " + exper_name + " " + scan_name.replace("n", "N"))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Correlate single scan')
    parser.add_argument('exper_name', type=str, help='Experiment name')
    parser.add_argument('scan_name', type=str, help='scan name')
    args = parser.parse_args()
    main(args.exper_name, args.scan_name)
    sys.exit(0)

