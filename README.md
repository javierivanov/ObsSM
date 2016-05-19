# ObsSM 0.4 version
ALMA Log's State Machine Parser

#ObsSM: Interpreter

* Easy build and run
```
curl https://raw.githubusercontent.com/javierivanov/ObsSM/master/auto_install.sh -o - |sh
```


##Compiling process, normal procedure

The interpreter works on java version >= 1.7

* Download sources https://github.com/javierivanov/ObsSM/archive/master.zip
* Unzip files

```
wget https://github.com/javierivanov/ObsSM/archive/master.zip
unzip master.zip
```

* Enter to the project folder

```
cd ObsSM-master/ObsSM/
```

* Compile using Maven

**You have to have installed Maven for this step.**
**You can check this, trying with the command ```mvn```.**
**Anyway you can install it from here: [https://maven.apache.org]**
 
```
mvn clean dependency:copy-dependencies package
```
or
```
./build.sh
```

##Execute the interpreter
```
java -cp "target/ObsSM-0.4.jar:target/dependency/*" org.alma.obssm.Run
```
or
```
./obssm.sh
```


#How to send logs to the interpreter

ObsSM includes a built-in ElasticSearch retriever, and search data directly from the ELK server.


**Not available yet!**
Also you can send line logs through the `logSender.py` script.
If you want to use another port, host, python version or whatever, the scritp is very easy to modify (few lines).
You can build your own implementation on another language.

* New on this version 0.3: it will send the xml logs available on the web repo
 of ALMA servers. So, you will be able to analyze the state of the arrays on near real time.

* To run the log sender:
```
python scripts/logSender.py
```
#Discovery: Transitions

This is an application to discover transitions into the log, generating a SCXML model.

##Usage:

* To select dates for learning, you have to edit the ```main.py``` file.


```
python2 Discovery/src/main.py [states.json file]
```
It generates a xml document, so you can save it using:
```
python2 Discovery/src/main.py [states.json file] > [model file]
```
