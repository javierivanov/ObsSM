'''
This file contains all the logic to discovers transitions.

'''

import json
import re


def getJSONString(transitions):
    out = []
    for t in transitions:
        for i in range(0, len(t.states_to)):
            val = float(t.values[i]) / float(t.counter)
            out.append((t.state_from["eventName"], t.states_to[i]["eventName"], val))
    return json.dumps(out)


class Transition:
    '''Defines a transition between one state to many states.'''
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
        out = "=========================START=TRANSITION=========================="
        out += "\nstate_from:\t" + str(self.state_from["eventName"]) + "\n"
        out += "states_to:\t" + str([x["eventName"] for x in self.states_to]) + "\n"
        out += "T:\t\t" + str([float(x) / float(self.counter) for x in self.values]) + "\n"
        out += "counter:\t" + str(self.counter) + "\n"
        out += "=========================END=TRANSITION=========================="
        return out


class Agent:
    '''Defines an Agent who has a state and keyName'''
    def __init__(self, keyName, state):
        self.keyName = keyName
        self.state = state

    def __str__(self):
        return self.keyName


class LogWorld:
    '''Defines the logic of logs parsing'''
    def __init__(self, statesFile):
        f = open(statesFile, "r")
        self.states = json.loads("".join(f.readlines()))
        self.agents = []
        self.transitions = []

    def isTerminal(self, state):
        return state["eventType"] == "final"

    def getStartState(self):
        return [x for x in self.states if x["eventType"] == "initial"][0]

    def getPossibleStates(self, state):
        v = "eventName"
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

    def parseLogLine(self, line):
        for ss in self.states:
            if len(ss["search_list"]) > 0:
                for i in ss["search_list"]:
                    m = re.search(i, line)
                    if m is not None:
                        return ss
            if len([x for x in ss["and_list"] if str(x) in str(line)]) == len(ss["and_list"]):
                if len(ss["and_list"]) > 0:
                    return ss
            if len([x for x in ss["or_list"] if str(x) in str(line)]) >= 1:
                return ss
        return None

    def logDispatcher(self, line):
        '''
        Decides if a line correspond with an Agent, if is not add a new Agent
        then execute parseLogLine who return the name of the action and then fire the transition.

        If the parseLogLine returns None, it means that there is not a transition.
        '''
        for a in self.agents:
            kn = re.search(a.state["keyName"], line)
            if kn is not None:
                s = self.parseLogLine(line)
                if s is not None:
                    if s["eventType"] == "initial":
                        self.agents.remove(a)
                        break
                    self.fireTransition(a, s)
            if self.isTerminal(a.state):
                self.agents.remove(a)
                return
        a = Agent(None, self.getStartState())
        s = self.parseLogLine(line)
        if s is not None:
            if s["eventType"] == "initial":
                a.keyName = re.search(a.state["keyName"], line)
                if a.keyName is not None:
                    a.keyName = a.keyName.group(0)
                else:
                    print ("SOMETHING WRONG")
                    print (line)
                    return
                self.agents.append(a)
