#!/usr/bin/env python3
import socket
#from urllib import request as urllib
import urllib2 as urllib
import sys
import os
import ntplib
import datetime
import time
import gzip
from tempfile import NamedTemporaryFile

base_url = 'http://computing-logs.aiv.alma.cl/AOS/SYSTEM/'
x = ntplib.NTPClient()
dt = datetime.datetime.utcfromtimestamp(x.request('ntp.shoa.cl').tx_time)

# defaults vars
host = "localhost"
port = 8888
sock = socket.socket()
sock.connect((host, port))


def datetimeToStr(da):
    out = ""
    out += str(da.year)
    out += "-"
    if da.month < 10:
        out += "0"
    out += str(da.month)
    out += "-"
    if da.day < 10:
        out += "0"
    out += str(da.day)
    return out


def get_files_per_day(day):
    print(base_url + day + "/")
    urlpath = urllib.urlopen(str(base_url + day + "/"))
    response = urlpath.readlines()
    list_per_day = list()
    for i in response:
        aux = i.decode('utf-8')
        if ".xml.gz" in aux:
            url_ = aux[aux.find('<a href="./log') + 11:2 + aux.find('gz">')]
            list_per_day.append(url_)
    return list_per_day


def data_process(day, file):
    urlpath = urllib.urlopen(base_url + day + "/" + file)
    tmp = urlpath.read()
    f = NamedTemporaryFile(delete=False)
    f.write(tmp)
    f.flush()
    f.close()
    tmp = gzip.open(f.name, "rb")
    os.unlink(f.name)
    for i in tmp.readlines():
        sock.sendall(i)

last_record = []
while True:
    print("Getting: " + datetimeToStr(dt))
    d = get_files_per_day(datetimeToStr(dt))
    for i in d:
        if i not in last_record:
            last_record.append(i)
            data_process(datetimeToStr(dt), i)
            print(i)
    time.sleep(10)
