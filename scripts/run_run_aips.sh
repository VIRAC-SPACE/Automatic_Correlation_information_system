#! /bin/bash

expername=$1
year=$2
month=$3
day=$4
monthd=$5

sh ~/scripts/run_aips.sh $expername $year $month $day $monthd  </dev/null >/dev/null 2>&1 

exit 0

