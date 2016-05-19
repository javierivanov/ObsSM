#!/bin/bash
#Auto install Obssm

if ! command_loc="$(type -p "mvn")" || [ -z "$command_loc" ]; then
  echo "mvn command not found";
  echo "A Maven installation is required";
  exit 1;
fi

if ! command_loc="$(type -p "wget")" || [ -z "$command_loc" ]; then
  echo "wget command is required";
  exit 1;
fi

if ! command_loc="$(type -p "unzip")" || [ -z "$command_loc" ]; then
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
java -cp "target/ObsSM-0.4.jar:target/dependency/*" org.alma.obssm.Run

