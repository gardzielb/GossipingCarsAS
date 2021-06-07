package com.kgd.agents.trafficLigths.managerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.Vec2;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveApproachNotificationsBehavior extends CyclicBehaviour {

    private final Agent managerAgent;
    private final ObjectMapper deserializer = new ObjectMapper();

    public ReceiveApproachNotificationsBehavior(Agent managerAgent) {
        this.managerAgent = managerAgent;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        var notification = managerAgent.blockingReceive(msgTemplate);

        if (notification != null) {
            var approachDirJson = notification.getContent();
            try {
                var approachDir = deserializer.readValue(approachDirJson, Vec2.class);
                System.out.println(notification.getSender().getLocalName() + " is coming from " + approachDir);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
