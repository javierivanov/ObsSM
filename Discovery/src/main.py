#!/usr/bin/env python3

import logworld
import sys
from scxmlGen import *

log = logworld.LogWorld("states.json")

f = open(sys.argv[1], "r")

[log.logDispatcher(x) for x in f.readlines()]

#[print(x) for x in log.transitions]

print(createSCXML(log.transitions))
