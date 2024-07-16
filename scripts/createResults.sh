#!/bin/bash

experiment_name=$1
experiment_type=$2

DIR=$dirrectory

cd "$DIR$experiment_type"


zip -r  $experiment_name.zip $experiment_name/

exit 0
