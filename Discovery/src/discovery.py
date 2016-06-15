#!/usr/bin/env python2
import sys

from optparse import OptionParser
import logworld
from scxmlGen import createSCXML

parser = OptionParser()
parser.add_option("-f", "--file", dest="filename",
                  help="JSON Log translate document", metavar="JSON")
parser.add_option("-v", "--verbose", action="store_false", dest="verbose",
                  help="Show data from transitions")

(options, args) = parser.parse_args()

if options.filename is None:
    print("Option --file is required")

log = logworld.LogWorld(options.filename)

for line in sys.stdin:
    log.logDispatcher(line)


if options.verbose is None:
    print(createSCXML(log.transitions))
else:
    for x in log.transitions:
        print(x)
