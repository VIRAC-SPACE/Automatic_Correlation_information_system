#!/bin/bash
#make_dir.sh aa1 sfxc 2021 Jul 22 07

experiment_name=$1
experiment_type=$2
year=$3
month=$4
day=$5
monthd=$6 

SFXC_resources=$HOME"/SFXC_resources/"
DIR="$dirrectory2"

if [ ! -d "$DIR$experiment_type/$experiment_name" ]; then 
	mkdir "$DIR$experiment_type/$experiment_name"
fi


path="/Docs/Dokumenti/Observations/$year/VIRAC/$month/$year.$monthd.$day\ $experiment_name/"
scp $flexbuff1_user@$flexbuff1_ip:"$path/$experiment_name.vex"  "$DIR$experiment_type/$experiment_name/$experiment_name.vex"


if [ $experiment_type = "sfxc" ]; then
        touch "$DIR$experiment_type/$experiment_name/$experiment_name.vix"
	touch "$DIR$experiment_type/$experiment_name/$experiment_name.ctrl"
	cp $SFXC_resources* "$DIR$experiment_type/$experiment_name"
fi

if [ $experiment_type = "kana" ]; then
	cp $SFXC_resources"machines" "$DIR$experiment_type/$experiment_name"
	cp $SFXC_resources"rankfile" "$DIR$experiment_type/$experiment_name"
fi

exit 0

