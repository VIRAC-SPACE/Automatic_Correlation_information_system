#!/bin/bash

station=$1
month_plus_year=$2   # example jul21
expname=$3
path="/Docs/Dokumenti/"
exptype=$4

echo "station is $station"
if [ $station = "ir" ]; then
	path=$path"GPS_counts_RT32/gps/"
else
	path=$path"GPS_counts/gps/"
fi

path=$path$month_plus_year/"gps."$station
echo "Get file "$path

echo "scp $flexbuff1_user@$flexbuff1_ip:"$path "$dirrectory2/$exptype/$expname"
scp $flexbuff1_user@$flexbuff1_ip:$path "$dirrectory2/$exptype/$expname"

exit 0
