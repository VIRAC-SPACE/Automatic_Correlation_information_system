#!/bin/sh

# This is the directory where the ephemerides will
# be stored
measdir=/usr/local/share/casacore/data

# attempt to move to that directory
echo cd "${measdir}"
cd "${measdir}"
if [ $? -ne 0 ]; then
    echo "$0: Failed to cd into ${measdir}"
    exit 1
fi

# remove old data
rm -f WSRT_Measures.ztar

# ftp over the new stuff
wget ftp://ftp.astron.nl/outgoing/Measures/WSRT_Measures.ztar 2>&1
if [ $? -ne 0 ]; then
    echo "$0: Failed to retrieve latest measures data from WSRT"
    exit 1
fi

# unzip + untar
tar zvxf WSRT_Measures.ztar
if [ $? -ne 0 ]; then
    echo "$0: Failed to unbzip2/extract ephemeris data!"
    exit 1
fi

exit 0
