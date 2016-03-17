from xml.etree import ElementTree as et


verbToPast = {}
verbToPast["start"] = "started"
verbToPast["stop"] = "stoped"
verbToPast["creation"] = "created"
verbToPast["destruction"] = "destroyed"
verbToPast["release"] = "released"


def transitionToState(transition):
    l = str(transition).split('.')
    aux = ""
    for i in l:
        if i in verbToPast.keys():
            aux += str(verbToPast[i]).capitalize()
        else:
            aux += str(i).capitalize()
    return aux + "State"


def createSCXML(transitions):
    attr = {'initial': 'idle', 'version': '0.9', 'xmlns': "http://  www.w3.org/2005/07/scxml"}
    scxml = et.Element("scxml", attr)
    # States
    states = {}
    idle = et.SubElement(scxml, 'state', attrib={'id': 'idle'})

    for i in transitions:
        states[transitionToState(i.state_from['stateName'])] = None
        for j in i.states_to:
            if j['stateType'] == 'final':
                states[transitionToState(j['stateName'])] = 'final'
            else:
                states[transitionToState(j['stateName'])] = None
    for i in states.keys():
        if states[i] == 'final':
            states[i] = et.SubElement(scxml, 'final', {'id': i})
        else:
            states[i] = et.SubElement(scxml, 'state', {'id': i})

    # idle transition
    for i in transitions:
        if i.state_from['stateType'] == 'initial':
            attr = {}
            attr['target'] = transitionToState(i.state_from["stateName"])
            attr['event'] = i.state_from["stateName"]
            aux = et.SubElement(idle, 'transition', attr)
            aux.text = ' '

    # transitions
    for i in transitions:
        n = transitionToState(i.state_from["stateName"])
        for j in i.states_to:
            attr = {}
            attr['target'] = transitionToState(j["stateName"])
            attr['event'] = j["stateName"]
            aux = et.SubElement(states[n], 'transition', attr)
            aux.text = ' '
    return str(et.tostring(scxml).decode('utf-8')).replace('/>', '/>\n').replace('><', '>\n<').replace('</state>', '</state>\n').replace('<transition', '\t<transition')
