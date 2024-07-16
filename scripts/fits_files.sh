#!/bin/bash

experiment_name=$1
pass_name=$2

ssh -tt $flexbuff1_user2@$flexbuff1_ip  << ENDSSH
        sh $create_fits_files_multi $experiment_name  $pass_name && exit
        exit 0
ENDSSH

exit 0
