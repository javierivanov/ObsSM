#!/usr/bin/env python3

import logworld
import sys
from scxmlGen import createSCXML

log = logworld.LogWorld(sys.argv[1])

f = open(sys.argv[2], "r")

[log.logDispatcher(x) for x in f.readlines()]

# [print(x) for x in log.transitions]

print(createSCXML(log.transitions))
