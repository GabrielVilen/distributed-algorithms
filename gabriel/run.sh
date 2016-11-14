#!/bin/bash
source teachnet.path

java -jar $tn_path \
--cp . \
--config G_Floodingconfig.txt \
--compile

java -jar $tn_path \
--cp . \
--config G_Floodingconfig.txt \
--compile

javac -cp ../teachnet.jar G_Flooding.java
#java -cp .:../teachnet.jar teachnet/view/TeachnetFrame

# Windows Users use 
java -cp .;..\teachnet.jar teachnet/view/TeachnetFrame
