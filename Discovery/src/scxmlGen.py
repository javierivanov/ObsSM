from xml.etree import ElementTree as et



def transitionToState(transition):
    l = str(transition).split('.')
    aux = l[0] + l[1].capitalize()
    return aux + "State"

def transitionToTransition(transition):
    l = str(transition).split('.')
    return l[1]

def createSCXML(transitions):
    '''Returns a SCXML document using the transitions'''
    attr = {'initial': 'idle', 'version': '0.9', 'xmlns': "http://www.w3.org/2005/07/scxml"}
    scxml = et.Element("scxml", attr)
    # States
    states = {}
    idle = et.SubElement(scxml, 'state', attrib={'id': 'idle'})

    for i in transitions:
        states[transitionToState(i.state_from['eventName'])] = None
        for j in i.states_to:
            if j['eventType'] == 'final':
                states[transitionToState(j['eventName'])] = 'final'
            else:
                states[transitionToState(j['eventName'])] = None
    for i in states.keys():
        if states[i] == 'final':
            states[i] = et.SubElement(scxml, 'final', {'id': i})
        else:
            states[i] = et.SubElement(scxml, 'state', {'id': i})

    # idle transition
    for i in transitions:
        if i.state_from['eventType'] == 'initial':
            attr = {}
            attr['target'] = transitionToState(i.state_from["eventName"])
            attr['event'] = i.state_from["eventName"]
            aux = et.SubElement(idle, 'transition', attr)
            aux.text = ' '

    # transitions
    for i in transitions:
        n = transitionToState(i.state_from["eventName"])
        for j in i.states_to:
            attr = {}
            attr['target'] = transitionToState(j["eventName"])
            attr['event'] = transitionToTransition(j["eventName"])
            aux = et.SubElement(states[n], 'transition', attr)
            aux.text = ' '
    return str(et.tostring(scxml).decode('utf-8')).replace('/>', '/>\n').replace('><', '>\n<').replace('</state>', '</state>\n').replace('<transition', '\t<transition')
