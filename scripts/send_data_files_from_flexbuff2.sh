#! /bin/bash

exper_name=$1
scan=$2
experName=$exper_name"_""ib_"$scan*
echo $experName

if [ ! -d "/mnt/VLBI/data/$exper_name" ]; then
	mkdir /mnt/VLBI/data/$exper_name
fi


ssh -tt $flexbuff1_user@$flexbuff2_ip << ENDSSH
        StartJ5  && exit
        exit 0
ENDSSH


ssh -tt $flexbuff1_user@$flexbuff2_ip << ENDSSH
	m5copy --allow_overwrite  --blame_guifre vbs:///$experName file://127.0.0.1:/mnt/VLBI/data/$exper_name/ -udp -m 9000 && exit
        exit
ENDSSH


exit 0

