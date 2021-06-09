package com.kgd.agents.trafficLigths.signalerBehaviors;

import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ChangeLightColorBehavior extends CyclicBehaviour {

    private final TrafficLightSignalerAgent agent;

    public ChangeLightColorBehavior(TrafficLightSignalerAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        var changeColorRequest = agent.receive(msgTemplate);

        if (changeColorRequest != null) {
            agent.changeLight();
            System.out.println(agent.getLocalName() + " changed light color");
        }
        else {
            block();
        }
    }
}
