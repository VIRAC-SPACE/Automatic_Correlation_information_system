#!/bin/bash

experiment_name=$1

control_file=$experiment_name.ctrl
vix_file=$experiment_name.vix

cd "/mnt/VLBI/correlations/sfxc/$experiment_name/" 
mpirun --debug-daemons -np 16  --hostfile machines -mca /  rankfile  sfxc  $control_file $vix_file 
python2 $produce_html_plotpage  $vix_file  $control_file

exit 0
