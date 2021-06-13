package com.kgd.agents.trafficLigths.signalerBehaviors;

import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleChangeLightColorRequestsBehavior extends CyclicBehaviour {

    private final TrafficLightSignalerAgent agent;

    public HandleChangeLightColorRequestsBehavior(TrafficLightSignalerAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        var changeColorRequest = agent.receive(msgTemplate);

        if (changeColorRequest != null) {
            String content = changeColorRequest.getContent();
            if (content.isBlank())
                agent.changeLight();
            else
                agent.setLightGreen(Boolean.parseBoolean(content));
        }
        else {
            block();
        }
    }
}
