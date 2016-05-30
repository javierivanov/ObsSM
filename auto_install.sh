#!/bin/bash
#Auto install Obssm

if ! command -v mvn >/dev/null 2>&1 ; then
  echo "mvn command not found";
  echo "A Maven installation is required";
  exit 1;
fi

if ! command -v wget >/dev/null 2>&1 ; then
  echo "wget command is required";
  exit 1;
fi

if ! command -v unzip >/dev/null 2>&1 ; then
  echo "unzip command is required";
  exit 1;
fi

if [ ! -d ObsSM-master ]; then
  wget https://github.com/javierivanov/ObsSM/archive/master.zip
  unzip master.zip
  rm master.zip
fi

cd ObsSM-master/ObsSM/

mvn clean dependency:copy-dependencies package
java -cp "target/ObsSM-1.0.jar:target/dependency/*" org.alma.obssm.Run

