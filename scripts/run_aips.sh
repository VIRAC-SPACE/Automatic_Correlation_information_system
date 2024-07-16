#!/bin/bash

expername=$1
year=$2
month=$3
day=$4
monthd=$5

dir=$dirrectory$expername/results/aips/


#sh ~/scripts/aips_setup.sh 18g1b 2021 Jul 22 07
sh ~/scripts/aips_setup.sh $expername $year $month $day $monthd

ParselTongue ~/python/irib.py $expername
python3 ~/python/convert_aips_plots.py $dir


exit 0
