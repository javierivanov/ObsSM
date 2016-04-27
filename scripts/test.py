import json
import sys


f = open("file.json", "r")

data = json.load(f)

lista = data["hits"]["hits"]

files = list()
for i in lista:
    if not i["_source"]["File"] in files:
        files.append(i["_source"]["File"])
if len(sys.argv) == 1:
    for i in files:
        print(i)
print("==============================================================================================================================")
if len(sys.argv) == 1:
    sys.exit(0)
for i in lista:
    for j in sys.argv:
        if j in i["_source"]["File"]:
            if "CONTROL/Array003" in i["_source"]["SourceObject"]:
                print(i["_source"]["TimeStamp"] + "\t" + i["_source"]["File"] + "\t" + i["_source"]["Routine"] + "\t\t\t" + i["_source"]["text"])
                print("----------------------------------------------------------------------------------------------------------------------------")
