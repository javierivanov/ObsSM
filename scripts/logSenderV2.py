#!/usr/bin/env python3

#Version 0.2
##
# Script to send Logs to SCXML Interpreter
# TODO: Improve the communication layer, more secure and standard.
#

import socket
import sys
import os
import time


# defaults vars
host = "localhost"
port = 8888
sock = socket.socket()
sock.connect((host, port))

def sendFile(file):
    f = open(sys.argv[1]+"/"+file)
    lines = f.readlines()
    for i in lines:
        time.sleep(0.001)
        print(i)
        sock.sendall(i.encode('utf-8'))

dirs= os.listdir(sys.argv[1])
for d in dirs:
    if str(d).startswith("acsStartContainer"):
        print("Reading: " + d)
        sendFile(d)
sock.sendall("EOF\n".encode("utf-8"))
sock.close()
