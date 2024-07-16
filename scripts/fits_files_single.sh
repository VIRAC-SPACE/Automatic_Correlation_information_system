#!/bin/bash

experiment_name=$1
scan_name=$2

ssh -tt $flexbuff1_user2@$flexbuff1_ip  << ENDSSH
        sh $create_fits_single $experiment_name  $scan_name && exit
        exit 0
ENDSSH

exit 0
