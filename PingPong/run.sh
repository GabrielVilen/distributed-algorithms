#!/bin/bash
source teachnet.path

java -jar $tn_path \
--cp . \
--config PingPongconfig.txt \
--compile

javac -cp ../teachnet.jar PingPong.java
#java -cp .:../teachnet.jar teachnet/view/TeachnetFrame

# Windows Users use 
# java -cp .;..\teachnet.jar teachnet/view/TeachnetFrame
