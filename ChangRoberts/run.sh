#!/bin/bash
source ../teachnet.path

# java -jar $tn_path \
# --cp . \
# --config ChangRobertsConfig.txt \
# --compile

java -jar $tn_path \
--cp . \
--config ChangRobertsWorst.txt \
--compile

# java -jar $tn_path \
# --cp . \
# --config ChangRobertsWorst.txt \
# --compile

javac -cp $tn_path ChangRoberts.java
#java -cp .:../teachnet.jar teachnet/view/TeachnetFrame

# Windows Users use 
#java -cp .;..\teachnet.jar teachnet/view/TeachnetFrame
