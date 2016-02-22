#!/usr/bin/env python3

#Version 0.1.1
##
# Script to send Logs to SCXML Interpreter
#

import socket
import sys
import os
import time



def sendLogLine(line):
    sock = socket.socket()
    sock.connect(("localhost", 8888))
    sock.sendall(line.encode('utf-8'))
    sock.close()

def sendFile(file):
    f = open(sys.argv[1]+"/"+file)
    lines = f.readlines()
    for i in lines:
        sendLogLine(i)

"""
Selecting the folder to read container logs.
"""

if os.path.isdir(sys.argv[1]):
    dirs= os.listdir(sys.argv[1])
    for d in dirs:
        if str(d).startswith("acsStartContainer"):
            print("Reading: " + d)
            sendFile(d)
    sendLogLine("EOF\n")
