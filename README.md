# ObsSM 1.0 version
ALMA Log's State Machine Parser

This is the *how to compile and run*, for further information see: https://github.com/javierivanov/ObsSM/wiki

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

## Downloading Logs from Elastic Search:

Help discovery.py
```sh
Usage: discovery.py [options]

Options:
  -h, --help            show this help message and exit
  -f JSON, --file=JSON  JSON Log translate document
  -v, --verbose         Show data from transitions
```

You can use the obssm.sh in grep mode to obtain logs:
```sh
./obssm.sh -c --grep --date_from="2016-05-22T21:16:22.037" --date_to="2016-05-23T21:16:22.037" --query="*"
```
So, you can use a pipe:
```sh
ObsSM/obssm.sh -c --grep --date_from="2016-05-23T20:16:22.037" --date_to="2016-05-23T21:16:22.037" --query="*" | python2 Discovery/src/discovery.py -f json-document.json
