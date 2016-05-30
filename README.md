# ObsSM 1.0 version
ALMA Log's State Machine Parser

For more information see: https://github.com/javierivanov/ObsSM/wiki

# ObsSM: Interpreter

* Easy build and run
```sh
curl https://raw.githubusercontent.com/javierivanov/ObsSM/master/auto_install.sh -o - |sh
```


## Compiling process, normal procedure

The interpreter works on java version >= 1.7 (Oracle)
Tested on Ubuntu 15.10, Oracle jdk 7, maven2.

* Download sources https://github.com/javierivanov/ObsSM/archive/master.zip
* Unzip files

```sh
wget https://github.com/javierivanov/ObsSM/archive/master.zip
unzip master.zip
```

* Enter to the project folder

```sh
cd ObsSM-master/ObsSM/
```

* Compile using Maven

**You have to have installed Maven for this step.**
**You can check this, trying with the command ```mvn```.**
**Anyway you can install it from here: [https://maven.apache.org]**
 
```sh
mvn clean dependency:copy-dependencies package
```
or
```sh
./build.sh
```

## Execute the interpreter
```sh
java -cp "target/ObsSM-1.0.jar:target/dependency/*" org.alma.obssm.Run
```
or
```sh
./obssm.sh
```


# How to send logs to the interpreter

ObsSM includes a built-in ElasticSearch retriever, and search data directly from the ES server.


**Not available yet!**
Also you can send line logs through the `logSender.py` script.
If you want to use another port, host, python version or whatever, the scritp is very easy to modify (few lines).
You can build your own implementation on another language.

* To run the log sender:
```
python2 scripts/logSender.py
```

# Discovery: Transitions

This is an application to discover transitions into the log, generating a SCXML model.

## Usage:

* To select dates for learning, you have to edit the ```main.py``` file.


```sh
python2 Discovery/src/main.py [states.json file]
```
It generates a xml document, so you can save it using:
```sh
python2 Discovery/src/main.py [states.json file] > [model file]
```
