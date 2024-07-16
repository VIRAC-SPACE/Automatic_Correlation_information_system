#!/bin/bash

experiment_name=$1
scan=$2
dir="$dirrectory/$experiment_name/"

sudo sh $update_measure_data

cd $dir

$j2ms2 $experiment_name"_"$scan".cor" -o $experiment_name"_"$scan".ms"

sudo $tConvert $experiment_name"_"$scan".ms" $experiment_name"_"$scan".IDI"


if [ ! -d "results/" ]; then
	mkdir results/
fi


if [  -d "results/"$experiment_name"_"$scan".ms" ]; then
	rm -rvf  "results/"$experiment_name"_"$scan".ms"
fi

mv  $experiment_name"_"$scan".ms" results
mv  $experiment_name"_"$scan".IDI" results

python2 "$python_dir_2/create_standart_plots_for_single_scan.py" $experiment_name $scan
