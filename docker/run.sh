#!/bin/bash
#
if [ "$#" != 4 ]; then
 echo " Usage: ./run-java-demoapp.sh access_key secret_key kvs_stream ffmpeg_bin_dir"
 exit
fi
ACCESS_KEY=$1
SECRET_KEY=$2
KVS_STREAM=$3
DFFMPEG_BIN=$4
mvn package

# Start the demo app
java -Daws.accessKeyId=${ACCESS_KEY} -Daws.secretKey=${SECRET_KEY} -jar target/RTC-to-KVS-0.0.1-SNAPSHOT.jar --KVS_STREAM=${KVS_STREAM} --FFMPEG_BIN=${DFFMPEG_BIN}