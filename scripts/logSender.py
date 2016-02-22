#!/usr/bin/env python3


##
# Script to send Logs to SCXML Interpreter
#

import socket
import sys
import os
import time

"""
Selecting the folder to read container logs.
"""

if os.path.isdir(sys.argv[1]):
    








lines = f.readlines()

def sendLogLine(line):
    sock = socket.socket()
    sock.connect(("localhost", 8888))
    sock.sendall(line.encode('utf-8'))
    sock.close()

for i in lines:
    sendLogLine(i)

sendLogLine("EOF\n")
