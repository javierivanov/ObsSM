#!/usr/bin/env python3
import socket
import sys
import time
f = open(sys.argv[1],"r")

lines = f.readlines()

def sendLogLine(line):
    sock = socket.socket()
    sock.connect(("localhost", 8888))
    sock.sendall(line.encode('utf-8'))
    sock.close()

for i in lines:
    sendLogLine(i)

sendLogLine("EOF\n")
