#!/bin/bash
source teachnet.path

java -jar $tn_path \
--cp . \
--config Echoconfig.txt \
--compile

java -jar $tn_path \
--cp . \
--config EchoRingconfig.txt \
--compile

javac -cp ../teachnet.jar Echo.java
#java -cp .:../teachnet.jar teachnet/view/TeachnetFrame

# Windows Users use 
# java -cp .;..\teachnet.jar teachnet/view/TeachnetFrame
