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

def sendLogLine(line):
    sock = socket.socket()
    sock.connect((host, port))
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
def main():
    """
    Checking args
    """
    if len(sys.argv) == 4:
        host = sys.argv[2]
        port = sys.argv[3]
    elif len(sys.argv) != 1:
        print("Error, arguments does not match.")
        return
    if not os.path.isdir(sys.argv[1]):
        print("Error, we need a folder")
        return

    dirs= os.listdir(sys.argv[1])
    for d in dirs:
        if str(d).startswith("acsStartContainer"):
            print("Reading: " + d)
            sendFile(d)
    sendLogLine("EOF\n")

if __name__ == "__main__":
    main()
