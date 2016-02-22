# ObsSM
ALMA Log's State Machine Parser

#Compiling Process

* Download sources [https://github.com/javierivanov/ObsSM/archive/obssm-0.1.zip](obssm-0.1)
* Unzip files

```
unzip obssm-0.1.zip
```

* Enter to the project folder

```
cd obssm-0.1/ObsSM/
```

* Compile using Maven

```
clean dependency:copy-dependencies package
```

#Execute the program
```
java -cp "target/ObsSM-0.1.jar:target/dependency/*" org.alma.obssm.Run /complete/path/to/folder/model/
```

The SCXML model must be named model.xml and the transitions constraints must be named transitions.json and both of them have to be on the same folder.
