# ObsSM 0.3 version
ALMA Log's State Machine Parser
##Notice
This is a new version, it has not been finished yet.

#Transition Discovery

This is a new application to discover transitions into the log to generate a SCXML model.

##Usage:
```
python3 Discovery/src/main.py [states.json file] [Log training file]
```
It generates a xml file, so you can save it with:
```
python3 Discovery/src/main.py ../../models/states.json [Log training file] > ../../models/model.xml
```

#Compiling Process

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
java -cp "target/ObsSM-0.2.jar:target/dependency/*" org.alma.obssm.Run /path/to/folder/model/ [listener port(default 8888)]
```

#Execute the new built-in GUI
```
java -cp "target/ObsSM-0.2.jar:target/dependency/*" org.alma.obssm.Run 
```


The SCXML model must be named model.xml and the transitions constraints must be named transitions.json and both of them have to be on the same folder.

#How to send logs to the interpreter


You can send line logs through the `logSenderV2.py` script.
If you want to use another port, host, python version or whatever, the scritp is very easy to modify (few lines).
You can build your own implementation on another language.

* To run the log sender:
```bash
python3 logSenderV2.py [folder with plain text date ordered files]
```
