package com.kgd.agents.trafficLigths.managerBehaviors;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveApproachNotificationsBehavior extends CyclicBehaviour {

    private final Agent managerAgent;

    public ReceiveApproachNotificationsBehavior(Agent managerAgent) {
        this.managerAgent = managerAgent;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        var notification = managerAgent.receive(msgTemplate);

        if (notification != null) {
            var approachedLightsId = notification.getContent();
            System.out.println(notification.getSender().getLocalName() + " is coming to lights " + approachedLightsId);
        }
        else {
            block();
        }
    }
}
