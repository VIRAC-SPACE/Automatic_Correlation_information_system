#!/bin/bash

expername=$1
passname=$2

expername_lower="${expername,,}"

EXPER=$expername"_""*"
EXPER_lower="${EXPER,,}"


DIR="/mnt/VLBI/data/$expername_lower"

echo $DIR

mkdir -p $DIR

if [ "$(ls -A $DIR)" ]; then
     echo "Data is sent"
else

# Sending data from flexbuff1
ssh -tt $flexbuff1_user@$flexbuff1_ip  << ENDSSH
		StartJ5 && exit
		exit 0
ENDSSH

ssh -tt $flexbuff1_user@$flexbuff1_ip  << ENDSSH
		m5copy --allow_overwrite  --blame_guifre vbs:///$EXPER_lower file://127.0.0.1:$data_directory$expername_lower/ -udp -m 9000 && exit
		exit
ENDSSH



# Sending data from flexbuff2
ssh -tt $flexbuff1_user@$flexbuff2_ip  << ENDSSH
		StartJ5  && exit
		exit 0
ENDSSH

ssh -tt $flexbuff1_user@$flexbuff2_ip << ENDSSH
		m5copy --allow_overwrite  --blame_guifre vbs:///$EXPER_lower file://127.0.0.1:$data_directory$expername_lower/ -udp -m 9000 && exit
		exit
ENDSSH

fi

echo "Data is sent2"

# clock serch
python2 ~/python/fring_fit.py $expername

# running correlation
python3 ~/python/correlate_all_scans.py $expername $passname

exit 0
