package com.kgd.agents.trafficLigths.signalerBehaviors;

import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SignalTrafficLightColorBehavior extends CyclicBehaviour {

    private final TrafficLightSignalerAgent agent;
    private final String exitControllerName;

    public SignalTrafficLightColorBehavior(TrafficLightSignalerAgent agent) {
        this.agent = agent;
        this.exitControllerName = "";
    }

    public SignalTrafficLightColorBehavior(TrafficLightSignalerAgent agent, String exitControllerName) {
        this.agent = agent;
        this.exitControllerName = exitControllerName;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
        var isGreenQuery = agent.receive(msgTemplate);

        if (isGreenQuery != null) {
            System.out.println("Received query from " + isGreenQuery.getSender().getLocalName());

            var reply = isGreenQuery.createReply();
            if (agent.isGreen()) {
                reply.setPerformative(ACLMessage.AGREE);
                reply.setContent(exitControllerName);
            }
            else {
                reply.setPerformative(ACLMessage.REFUSE);
            }

            agent.send(reply);
        }
        else {
            block();
        }
    }
}
