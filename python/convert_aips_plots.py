#! /usr/bin/python3
# -*- coding: utf-8 -*-
import argparse
import os
import sys


def main(aips_directory):
    aips_plots = [file for file in os.listdir(aips_directory) if file.endswith(".eps")]

    for ap in aips_plots:
        os.system('convert ' + aips_directory + "/" + ap + ' ' + aips_directory + "/" +  ap.replace(".eps", ".pdf"))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Convert AIPS plots to PNG')
    parser.add_argument('aipsdir', type=str, help='AIPS output directory')
    args = parser.parse_args()
    main(args.aipsdir)
    sys.exit(0)

