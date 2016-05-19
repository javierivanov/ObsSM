#Â!/bin/bash

curl -C - -O wget https://github.com/javierivanov/ObsSM/archive/master.zip > master.zip
unzip master.zip
rm master.zip
cd ObsSM-master/ObsSM/
mvn clean dependency:copy-dependencies package
java -cp "target/ObsSM-0.4.jar:target/dependency/*" org.alma.obssm.Run

