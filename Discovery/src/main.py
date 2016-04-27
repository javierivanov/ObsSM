#!/usr/bin/env python


from datetime import date
from xml_log_processor import XMLLogProcessor
import sys

from scxmlGen import createSCXML

date_from = date(2016, 3, 23)
date_to = date(2016, 3, 24)
jsonfile = "../../models/states.json"

if len(sys.argv) > 1:
    jsonfile = sys.argv[1]


print ("Reading File: " + jsonfile)
print ("Date from - to:")
print (date_from)
print (date_to)

processor = XMLLogProcessor(date_from, date_to, jsonfile)

transitions = processor.get_transitions()

for i in transitions:
    print(i)

print(createSCXML(transitions))
