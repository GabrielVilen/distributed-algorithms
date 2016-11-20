#!/bin/bash
source ../teachnet.path

java -jar $tn_path \
--cp . \
--config FloodingAckRingconfig.txt \
--compile

java -jar $tn_path \
--cp . \
--config FloodingAckconfig.txt \
--compile

java -jar $tn_path \
--cp . \
--config FloodingAckCubeconfig.txt \
--compile

javac -cp $tn_path FloodingAck.java
#java -cp .:../teachnet.jar teachnet/view/TeachnetFrame

# Windows Users use 
# java -cp .;..\teachnet.jar teachnet/view/TeachnetFrame
