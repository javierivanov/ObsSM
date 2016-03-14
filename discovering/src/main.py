import logworld
import sys


log = logworld.LogWorld("states.json")

f = open(sys.argv[1], "r")

[log.logDispatcher(x) for x in f.readlines()]

[print(x) for x in log.transitions]

f = open("out.json", "w")
f.write(logworld.getJSONString(log.transitions))
f.close()
