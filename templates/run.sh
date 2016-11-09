#!/bin/bash
source teachnet.path

java -jar $tn_path \
--cp . \
--config myconfig.txt \
--compile

#javac -cp ../teachnet.jar MyAlgorithm.java
#java -cp .:../teachnet.jar teachnet/view/TeachnetFrame

# Windows Users use 
# java -cp .;..\teachnet.jar teachnet/view/TeachnetFrame
