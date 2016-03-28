# ObsSM 0.3 version
ALMA Log's State Machine Parser

#Discovery: Transitions

This is a new application to discover transitions into the log to generate a SCXML model.

##Usage:
```
python2 Discovery/src/main.py -v [states.json file]
```
It generates a xml document, so you can save it using:
```
python2 Discovery/src/main.py -scxml [states.json file] > [model file]
```
#ObsSM: Interpreter

##Compiling process

The interpreter is designed to work on java version >= 1.7

* Download sources https://github.com/javierivanov/ObsSM/archive/obssm-0.3.zip
* Unzip files

```
unzip obssm-0.3.zip
```

* Enter to the project folder

```
cd ObsSM-obssm-0.3/ObsSM/
```

* Compile using Maven

You have to have installed Maven for this step.

```
mvn clean dependency:copy-dependencies package
```

#Execute the interpreter
```
java -cp "target/ObsSM-0.3.jar:target/dependency/*" org.alma.obssm.Run /path/to/folder/model/ [listener port(default 8888)]
```

#Execute the new built-in GUI
```
java -cp "target/ObsSM-0.3.jar:target/dependency/*" org.alma.obssm.Run
```


The SCXML model must be named model.xml and the transitions constraints must be
named transitions.json and both of them have to be on the same folder.

#How to send logs to the interpreter


You can send line logs through the `logSender.py` script.
If you want to use another port, host, python version or whatever, the scritp is very easy to modify (few lines).
You can build your own implementation on another language.

* New on this version 0.3: it will send the xml logs available on the web repo
 of ALMA servers. So, you will be able to analyze the state of the arrays on near real time.
 
* To run the log sender:
```
python scripts/logSender.py
```
