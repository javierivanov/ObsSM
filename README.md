# ObsSM 1.0 version
ALMA Log's State Machine Parser



# ObsSM: Interpreter

* Easy build and run
```sh
curl https://raw.githubusercontent.com/javierivanov/ObsSM/master/auto_install.sh -o - |sh
```


## Compiling process, normal procedure

The interpreter works on java version >= 1.7

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
# ObsSM: advanced usage
You can also use a command line, for automatization or externals plugins.
## Command Line
```sh
./obssm.sh -h
```
Help message:
```
usage: obssm
 -c,--cmd                                              use a Command Line
                                                       Interface
    --date_from <time stamp from>                      TimeStamp from
    --date_to <time stamp to>                          TimeStamp to
    --elk_server <elk_server>                          Elastic Search
                                                       Server
 -h,--help                                             show this message
    --linereader <LineReaderJarFile:LineReaderClass>   Linereader
                                                       implementation
                                                       (Default:
                                                       ElasticSearch)
    --listener <ListenerJarFile:ListenerClass>         Transition listener
                                                       (Default:
                                                       DefaultEntryistener
                                                       )
    --log_translate <json file>                        use a given json
                                                       file to translate
                                                       log
    --query <query>                                    Query DSL
    --query_filter <json file>                         use a given query
                                                       filter (json) file
                                                       to search through
                                                       ElasticSearch
    --scxml <scxml file>                               use a given scxml
                                                       file to parse a SM
```
### Deafult Listener
If you want to see transitions just like command line output.

```sh
./obssm.sh -c --date_from="2016-05-22T21:16:22.037" --date_to="2016-05-23T21:16:22.037" --query="*"
```
Response:
```sh
May 27, 2016 5:29:47 PM org.alma.obssm.net.ElasticSearchImpl$1 run
INFO: Elastic Search start
ARRAY: Array001 EVENT: Array.creation TO: ArrayCreated FROM: MainIdle TS: 2016-05-22T21:28:11.266
ARRAY: Array002 EVENT: Array.creation TO: ArrayCreated FROM: MainIdle TS: 2016-05-22T21:43:22.335
ARRAY: Array003 EVENT: Array.creation TO: ArrayCreated FROM: MainIdle TS: 2016-05-22T21:49:46.369
ARRAY: Array002 EVENT: Interferometry.init TO: InterferometryInitializeStarted FROM: ArrayCreated TS: 2016-05-22T21:53:54.610
ARRAY: Array002 EVENT: PointingSubArray.callreference TO: InterferometrySettingUpBegun FROM: InterferometryInitializeStarted TS: 2016-05-22T21:53:54.680
ARRAY: Array002 EVENT: PointingSubArray.callreference TO: PointingSubArrayGettingReferenceCalled FROM: PointingSubArrayConstructorIdle TS: 2016-05-22T21:53:54.680
ARRAY: Array002 EVENT: PointingSubArray.antModecontrollercreated TO: PointingSubArrayControllersCreated FROM: PointingSubArrayGettingReferenceCalled TS: 2016-05-22T21:53:54.749
ARRAY: Array002 EVENT: LocalOscillator.callreference TO: LocalOscillatorGettingReferenceCalled FROM: LocalOscillatorConstructorIdle TS: 2016-05-22T21:53:55.105
ARRAY: Array002 EVENT: LocalOscillator.antModecontrollercreated TO: LocalOscillatorControllersCreated FROM: LocalOscillatorGettingReferenceCalled TS: 2016-05-22T21:53:59.963
```

### Custom Listener
Maybe you want to view transitions with a graphical interface. So it's possible to use a custom Listener.

You want to download the third party plugin here before!
```sh
wget https://raw.githubusercontent.com/javierivanov/ObsSM/master/ObsSM_plugins/GraphViewer.jar
```

```sh
./obssm.sh -c --date_from="2016-05-22T21:16:22.037" --date_to="2016-05-23T21:16:22.037" --query="*" --listener="GraphViewer.jar:org.alma.GraphViewer"
```
As you can see, I'm not a GUI designer, please create your own plugin for visualization.

### Custom models
Also you can use a custom SCXML Model, JSON log translate and JSON query base documents:


```sh
./obssm.sh -c --query_filter "/path/to/JSON" --scxml "/path/to/SCXML" --log_translate "/path/to/JSON"
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
