#!/bin/bash
# Demo run of Java SDK application for sending video streams
# within the docker container
#
if [ "$#" != 3 ]; then
 echo " Usage: ./run-java-demoapp.sh access_key secret_key kvs_stream"
 exit
fi
ACCESS_KEY=$1
SECRET_KEY=$2
KVS_STREAM=$3
DFFMPEG_BIN=$4
mvn package
# Create a temporary filename in /tmp directory
jar_files=$(mktemp)
# Create classpath string of dependencies from the local repository to a file
mvn -Dmdep.outputFile=$jar_files dependency:build-classpath
classpath_values=$(cat $jar_files)
# Start the demo app
java -classpath target/RTC-to-KVS-0.0.1-SNAPSHOT.jar:$classpath_values -Daws.accessKeyId=${ACCESS_KEY} -Daws.secretKey=${SECRET_KEY} -DKVS_STREAM=${KVS_STREAM} -DDFFMPEG_BIN=${DFFMPEG_BIN} com.devfactory.recorder.Application