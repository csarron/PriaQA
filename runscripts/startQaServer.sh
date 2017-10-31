#!/usr/bin/env bash

# start the QA server

function print_usage {
  echo "Starts the QA server"
  echo "    Usage $0 <ip> <port>"
  echo "    Default example: $0 localhost 8080"
}

if [ "$1" == "help" ]; then
  print_usage
  exit
fi

hash java 2>/dev/null || {
  echo >&2 "$0: [ERROR] java is not installed. Aborting."
  exit 1
}

ip=localhost
port=8080

if [[ -n "$1" ]]; then
  ip=$1
fi
if [[ -n "$2" ]]; then
  port=$2
fi

# start from top directory
#cd ..;

export CLASSPATH=bin:lib/ml/maxent.jar:lib/ml/minorthird.jar:lib/nlp/jwnl.jar:lib/nlp/lingpipe.jar:lib/nlp/opennlp-tools.jar:lib/nlp/plingstemmer.jar:lib/nlp/snowball.jar:lib/nlp/stanford-ner.jar:lib/nlp/stanford-parser.jar:lib/nlp/stanford-postagger.jar:lib/qa/javelin.jar:lib/search/bing-search-java-sdk.jar:lib/search/googleapi.jar:lib/search/indri.jar:lib/search/yahoosearch.jar:lib/util/commons-logging.jar:lib/util/gson.jar:lib/util/htmlparser.jar:lib/util/log4j.jar:lib/util/trove.jar:lib/util/servlet-api.jar:lib/util/jetty-all.jar:lib/util/commons-codec-1.9.jar
export CLASSPATH=$CLASSPATH:lib/search/*:lib/search/galago/*

#export INDRI_INDEX=`pwd`/wiki_indri_index/
export INDRI_INDEX=/mnt/ssd/enwiki-index-ne/

export GALAGO_INDEX=/mnt/ssd/enwiki-offline-ne-galago-idx/

os=`uname`
if [[ "$os" == "Darwin" ]]; then
    # macOS
    export THREADS=$(sysctl -n hw.ncpu)
else
    # assume others are Linux
    export THREADS=$(cat /proc/cpuinfo | grep processor | wc -l)
fi

#export ENGINE=Indri
export ENGINE=Galago

#java -Djava.library.path=lib/search/ -server -Xms1024m -Xmx2048m  info.ephyra.OpenEphyraServer $ip $port

     #-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005  \
java -Djava.rmi.server.hostname=$ip \
     -Dcom.sun.management.jmxremote.port=3456 \
     -Dcom.sun.management.jmxremote.ssl=false \
     -Dcom.sun.management.jmxremote.authenticate=false \
    -Djava.library.path=lib/search/ -server -Xms1024m -Xmx2048m  \
    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005  \
     info.ephyra.OpenEphyraServer $ip $port 2>&1 | tee qaServer.log
#java -agentpath:/home/qqcao/jprof/bin/linux-x64/libjprofilerti.so=port=8849,nowait \
#     -agentlib:hprof=cpu=samples,depth=100,interval=7,lineno=y,thread=y,file=output.hprof \
#     -Djava.library.path=lib/search/ -server -Xms1024m -Xmx2048m  \
#     info.ephyra.OpenEphyraServer $ip $port
