#!/bin/bash

experiment_name=$1
scan=$2
scanl=$3


if [ ! -d "/mnt/VLBI/correlations/sfxc/$experiment_name" ]; then
	mkdir $dirrectory$experiment_name
fi


if [ ! -d "/mnt/VLBI/data/$experiment_name" ]; then
	mkdir $data_directory$experiment_name
fi


if [ ! -e "/mnt/VLBI/data/"$experiment_name/$experiment_name"_ir_"$scanl".m5a" ]; then
	 sh ~/scripts/send_data_files_from_flexbuff1.sh $experiment_name $scanl
fi


if [ ! -e "/mnt/VLBI/data/"$experiment_name/$experiment_name"_ib_"$scanl".m5a" ]; then
	 sh ~/scripts/send_data_files_from_flexbuff2.sh $experiment_name $scanl
fi


cd "/mnt/VLBI/correlations/sfxc/$experiment_name/"

if [ ! -d "delays/" ]; then
	mkdir delays/
fi


if [ ! -d "results/" ]; then
	mkdir results/
fi


rm -rfv delays/*
rm -rfv chex*
rm -rfv dynamic_channel_extractor*

control_file=$experiment_name.ctrl
vix_file=$experiment_name.vix

echo "/mnt/VLBI/correlations/sfxc/$experiment_name/"$vix_file


#mpirun --debug-daemons -np 16  --hostfile machines -mca /  rankfile  sfxc  $control_file $vix_file 
python2 $produce_html_plotpage  $vix_file  $control_file


ssh -tt $flexbuff1_user2@$flexbuff1_ip  << ENDSSH
        sh $create_fits_files $experiment_name  $scan && exit
        exit 0
ENDSSH


exit 0

