#!/usr/bin/env python3
import urllib.request as urllib
import pickle
import os
from datetime import date, timedelta
import gzip
import threading
import time
from tempfile import NamedTemporaryFile

base_url = 'http://computing-logs.aiv.alma.cl/AOS/SYSTEM/'
dfrom, dto = date(2016, 1, 31), date(2016, 2, 1)
dfilters = ('Emergency', 'Alert', 'Critical', 'Error')
threads_size = 20
base_folder = "data/"
list_days = list()


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


def data_process(day, file_):
    url_ = base_url + day + '/' + file_
    if not os.path.isfile(base_folder + file_):
        response = urllib.urlopen(url_)
        temp_data = response.read()
        temp_file = open(base_folder + file_, "wb")
        temp_file.write(temp_data)
        temp_file.flush()
        temp_file.close()
    temp_file = gzip.open(base_folder + file_, "rb")
    lines = temp_file.readlines()
    results = list()
    for aux in lines:
        startwith = False
        for i in dfilters:
            if aux.startswith(b'<' + i.encode('utf-8')):
                startwith = True
        if startwith:
            results.append(aux)
    temp_file.close()
    os.remove(base_folder + file_)
    fOut = open(base_folder + file_ + ".bin", "wb")
    pickle.dump(results, fOut)
    fOut.flush()
    fOut.close()


def worker(id_):
    counter = id_
    while counter < len(list_days):
        print(list_days[counter])
        files_ = get_files_per_day(list_days[counter])
        for i in files_:
            data_process(list_days[counter], i)
        counter += threads_size


for i in range(0, threads_size):
    t = threading.Thread(target=worker, args=(i,))
    t.start()
