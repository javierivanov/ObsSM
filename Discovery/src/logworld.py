import json
import re


def getJSONString(transitions):
    out = []
    for t in transitions:
        for i in range(0, len(t.states_to)):
            val = float(t.values[i]) / float(t.counter)
            out.append((t.state_from["stateName"], t.states_to[i]["stateName"], val))
    return json.dumps(out)


class Transition:
    def __init__(self, state_from):
        self.state_from = state_from
        self.states_to = []
        self.values = []
        self.counter = 0

    def appendTransition(self, state_to):
        if state_to in self.states_to:
            index = self.states_to.index(state_to)
            self.values[index] += 1
        else:
            self.states_to.append(state_to)
            self.values.append(1)
        self.counter += 1

    def getTransition(self, state_to):
        if state_to in self.states_to:
            index = self.state_to.index(state_to)
            return (self.values[index], self.counter)
        return None

    def __str__(self):
        out = "========================START=TRANSITION========================"
        out += "\nstate_from:\t" + str(self.state_from["stateName"]) + "\n"
        out += "states_to:\t" + str([x["stateName"] for x in self.states_to]) + "\n"
        out += "T:\t\t" + str([float(x) / float(self.counter) for x in self.values]) + "\n"
        out += "========================END=TRANSITION========================="
        return out


class Agent:

    def __init__(self, keyName, state):
        self.keyName = keyName
        self.state = state

    def __str__(self):
        return self.keyName


class LogWorld:

    def __init__(self, statesFile):
        f = open(statesFile, "r")
        self.states = json.loads("".join(f.readlines()))
        self.agents = []
        self.transitions = []

    def isTerminal(self, state):
        return state["stateType"] == "final"

    def getStartState(self):
        return [x for x in self.states if x["stateType"] == "initial"][0]

    def getPossibleStates(self, state):
        v = "stateName"
        return [x for x in self.states if state[v] is not x[v]]

    def fireTransition(self, agent, state_to):
        existTransition = False
        for i in self.transitions:
            if i.state_from == agent.state:
                i.appendTransition(state_to)
                existTransition = True
                break
        if not existTransition:
            t = Transition(agent.state)
            t.appendTransition(state_to)
            self.transitions.append(t)
        agent.state = state_to

    def getT(self, state_from, state_to):
        for i in self.transitions:
            if i.state_from == state_from:
                return i.getTransition(state_to)
        return None

    def parseLogLine(self, agent, line):
        states = self.getPossibleStates(agent.state)
        for ss in states:
            if len([x for x in ss["and_list"] if x in line]) == len(ss["and_list"]):
                if len(ss["and_list"]) > 0:
                    self.fireTransition(agent, ss)
                    return
            if len([x for x in ss["or_list"] if x in line]) >= 1:
                self.fireTransition(agent, ss)

    def logDispatcher(self, line):
        s = self.getStartState()
        kn = re.search(s['keyName'], line)
        if kn is not None:
            kn = kn.group(0)
        else:
            return
        for agent in self.agents:
            if agent.keyName == kn:
                if self.isTerminal(agent.state):
                    self.agents.remove(agent)
                else:
                    self.parseLogLine(agent, line)
                    return
        agent = Agent(kn, s)
        self.agents.append(agent)
