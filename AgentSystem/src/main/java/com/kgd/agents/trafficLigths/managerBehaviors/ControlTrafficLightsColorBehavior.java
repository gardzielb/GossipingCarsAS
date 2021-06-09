package com.kgd.agents.trafficLigths.managerBehaviors;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

public class ControlTrafficLightsColorBehavior extends WakerBehaviour {

    private final long[] intervals = new long[2];
    private int currentIntervalIndex = 0;
    private final String[] signalerAgentNames;

    public ControlTrafficLightsColorBehavior(Agent agent, long timeout, long nextChangeTimeout,
                                             String[] signalerAgentNames) {
        super(agent, timeout);
        this.signalerAgentNames = signalerAgentNames.clone();
        intervals[0] = timeout;
        intervals[1] = nextChangeTimeout;
    }

    @Override
    protected void onWake() {
        var changeColorRequest = new ACLMessage(ACLMessage.REQUEST);
        for (String signalerName : signalerAgentNames) {
            changeColorRequest.addReceiver(new AID(signalerName, AID.ISLOCALNAME));
        }
        myAgent.send(changeColorRequest);

        currentIntervalIndex = (currentIntervalIndex + 1) % 2;
        reset(intervals[currentIntervalIndex]);
    }
}
