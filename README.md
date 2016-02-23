# ObsSM
ALMA Log's State Machine Parser

#Compiling Process

* Download sources https://github.com/javierivanov/ObsSM/archive/obssm-0.2.zip
* Unzip files

```
unzip obssm-0.2.zip
```

* Enter to the project folder

```
cd ObsSM-obssm-0.2/ObsSM/
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

The SCXML model must be named model.xml and the transitions constraints must be named transitions.json and both of them have to be on the same folder.

#How to send logs to the interpreter


You can send line logs through the `logSenderV2.py` script.
If you want to use another port, host, python version or whatever, the scritp is very easy to modify (few lines).
You can build your own implementation on another language.

* To run the log sender:
```bash
python3 logSenderV2.py [folder with plain text date ordered files]
```
