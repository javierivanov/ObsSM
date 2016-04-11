# import urllib.request as urllib
import urllib2 as urllib
import os
from datetime import timedelta
import gzip
from tempfile import NamedTemporaryFile
import logworld


class XMLLogProcessor:
    '''This class download, uncompress and process the logs files'''
    def __init__(self, dfrom, dto, jsonfile):
        self.base_url = 'http://computing-logs.aiv.alma.cl/AOS/SYSTEM/'
        self.dfrom, self.dto = dfrom, dto
        self.list_days = list()
        self.log = logworld.LogWorld(jsonfile)
        self.data_files_map = {}

        for s, e in self.perdelta(self.dfrom, self.dto, timedelta(days=1)):
            self.list_days.append(str(e))

    def perdelta(self, start, end, delta):
        curr = start
        while curr < end:
            yield curr, min(curr + delta, end)
            curr += delta

    def get_files_per_day(self, day):
        urlpath = urllib.urlopen(self.base_url + day)
        response = urlpath.readlines()
        list_per_day = list()
        for i in response:
            aux = i.decode('utf-8')
            if ".xml.gz" in aux:
                url_ = aux[aux.find('<a href="./log') + 11:2 + aux.find('gz">')]
                list_per_day.append(url_)
        return list_per_day

    def download_file(self, day, file_):
        url_ = self.base_url + day + '/' + file_
        response = urllib.urlopen(url_)
        temp_data = response.read()
        temp_file = NamedTemporaryFile(mode='w+b', delete=False)
        temp_file.write(temp_data)
        temp_file.flush()
        temp_file.close()
        self.data_files_map.update({file_: temp_file.name})

    def data_process(self, file_):
        temp_file_g = gzip.open(file_, "rb")
        lines = temp_file_g.readlines()
        for a in lines:
            i = str(a)
            self.log.logDispatcher(i)
        temp_file_g.close()
        os.unlink(file_)

    def get_transitions(self):
        for i in self.list_days:
            for h in self.get_files_per_day(i)[:7]:
                # print("Downloading file: " + h)
                self.download_file(i, h)
                # print("Processing data")
                self.data_process(self.data_files_map[h])
        return self.log.transitions
