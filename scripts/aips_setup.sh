#!/bin/bash
# sh aips_setup.sh 18g1b 2021 Jul 22 07

expername=$1
year=$2
month=$3
day=$4
monthd=$5


expername_lower="${expername,,}"

dir=$dirrectory


rm $HOME/AIPSSETUP
touch /home/sfxc/AIPSSETUP
echo export DATAA=$dir$expername/results/contiuum/ >> $HOME/AIPSSETUP
echo export DATAB=$dir$expername/results/line/ >> $HOME/AIPSSETUP
echo export DATAC=$dir$expername/ >> /home/sfxc/AIPSSETUP
echo export DATAD=$dir$expername/results/aips/ >> $HOME/AIPSSETUP


if [ ! -d $dir$expername/results/aips/ ]; then
	mkdir -p $dir$expername/results/aips/
fi


scp $flexbuff1_user@$flexbuff1_ip:"/Docs/Dokumenti/Observations/$year/VIRAC/$month/$year.$monthd.$day\ $expername_lower/*.antabfs" $dir/$expername
scp $flexbuff1_user@$flexbuff1_ip:"/Docs/Dokumenti/Observations/$year/VIRAC/$month/$year.$monthd.$day\ $expername_lower/*.uvflgfs" $dir/$expername


exec bash --login 

exit 0
