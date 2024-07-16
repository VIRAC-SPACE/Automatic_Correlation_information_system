#!/bin/bash

experiment_name=$1
pass_name=$2
dir="$dirrectory$experiment_name/"


sudo sh $update_measure_data


cd $dir


$j2ms2 $experiment_name.cor* -o $experiment_name"_"$pass_name".ms"

sudo $tConvert $experiment_name"_"$pass_name".ms" $experiment_name"_"$pass_name".IDI"


if [ ! -d "results/$pass_name" ]; then
	mkdir -p results/$pass_name
fi


if [  -d "results/"$pass_name"/"$experiment_name"_"$pass_name".ms" ]; then
	rm -rvf "results/"$pass_name"/"$experiment_name"_"$pass_name".ms"
fi


mv  $experiment_name"_"$pass_name".ms" results/$pass_name
mv  $experiment_name"_"$pass_name".IDI" results/$pass_name


exit 0
