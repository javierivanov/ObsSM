#!/usr/bin/env python3
import urllib.request as urllib
import os
from datetime import date, timedelta
import gzip
from tempfile import NamedTemporaryFile
import logworld
import sys
from scxmlGen import createSCXML

base_url = 'http://computing-logs.aiv.alma.cl/AOS/SYSTEM/'
dfrom, dto = date(2016, 3, 17), date(2016, 3, 18)
base_folder = "data/"
list_days = list()
log = logworld.LogWorld(sys.argv[1])
data_files_map = {}


def perdelta(start, end, delta):
    curr = start
    while curr < end:
        yield curr, min(curr + delta, end)
        curr += delta

for s, e in perdelta(dfrom, dto, timedelta(days=1)):
    list_days.append(str(e))


def get_files_per_day(day):
    urlpath = urllib.urlopen(base_url + day)
    response = urlpath.readlines()
    list_per_day = list()
    for i in response:
        aux = i.decode('utf-8')
        if ".xml.gz" in aux:
            url_ = aux[aux.find('<a href="./log') + 11:2 + aux.find('gz">')]
            list_per_day.append(url_)
    return list_per_day


def download_file(day, file_):
    url_ = base_url + day + '/' + file_
    response = urllib.urlopen(url_)
    temp_data = response.read()
    temp_file = NamedTemporaryFile(mode='w+b', delete=False)
    temp_file.write(temp_data)
    temp_file.flush()
    temp_file.close()
    data_files_map.update({file_: temp_file.name})


def data_process(file_):
    temp_file_g = gzip.open(file_, "rb")
    lines = temp_file_g.readlines()
    for a in lines:
        i = str(a)
        log.logDispatcher(i)
    temp_file_g.close()
    os.unlink(file_)


for i in list_days:
    for h in get_files_per_day(i)[:10]:
        download_file(i, h)
        data_process(data_files_map[h])
        [print(x) for x in log.transitions]
        print()
        print()

# print(createSCXML(log.transitions))
