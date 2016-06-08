#!/bin/bash
SOURCE="${BASH_SOURCE[0]}"
DIR="$(dirname $SOURCE)" 
cd $DIR
java -cp "target/ObsSM-1.0.jar:target/dependency/*" org.alma.obssm.Run $@ 
