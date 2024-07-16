#! /bin/bash

exper_name=$1
scan=$2

if [ ! -d "/mnt/VLBI/data/$exper_name" ]; then
	mkdir /mnt/VLBI/data/$exper_name
fi

sh ~/scripts/send_data_files_from_flexbuff1.sh $exper_name $scan
sh ~/scripts/send_data_files_from_flexbuff2.sh $exper_name $scan

exit 0
