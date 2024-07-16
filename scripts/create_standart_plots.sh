#!/bin/bash

source=$1
obs_name=$2
pass_name=$3
ms_file=$4
station=$5



path="$dirrectory"$obs_name"/results/"
standart_plots_path=$path"/"$pass_name"/standartplots/"

ssh -tt $flexbuff1_user2@$flexbuff1_ip  << ENDSSH
        cd $standart_plots_path$source && standardplots $ms_file $station -weight $source && exit
        exit 0
ENDSSH

exit 0
