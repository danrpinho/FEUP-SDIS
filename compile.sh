#!/bin/sh
killall rmiregistry
rm bin -rf
mkdir bin
cd ./src
find -name "*.java" > sources.txt
javac -d ../bin @sources.txt
rm sources.txt
cd ../bin
rmiregistry &

#javac -d destDir channels/*.java client/*.java initiators/*.java peer/*.java rmi/*.java utils/*.java 
#rmiregistry &
#java -classpath bin -Djava.rmi.server.codebase=file:bin/ peer.Peer 1 1 comms 224.0.0.0 4445 224.0.0.0 4446 224.0.0.0 4447
#java -classpath bin -Djava.rmi.server.codebase=file:bin/ client.Client comms BACKUP test1.txt 1