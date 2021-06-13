package com.kgd.agents.trafficLigths.managerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.messages.TrafficLightNotification;
import com.kgd.agents.trafficLigths.TrafficLightsManagerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveCarNotificationsBehavior extends CyclicBehaviour {

    private final TrafficLightsManagerAgent managerAgent;
    private final ObjectMapper deserializer = new ObjectMapper();

    public ReceiveCarNotificationsBehavior(TrafficLightsManagerAgent managerAgent) {
        this.managerAgent = managerAgent;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        var notification = managerAgent.receive(msgTemplate);

        if (notification != null) {
            try {
                var notificationContent = deserializer.readValue(
                        notification.getContent(), TrafficLightNotification.class
                );
                switch (notificationContent.type()) {
                    case EXPECTED_APPROACH -> managerAgent.handleExpectedApproach(notificationContent.data());
                    case PASS_THROUGH -> managerAgent.handlePassThrough(notificationContent.data());
                    case EXIT -> managerAgent.handleExit(notificationContent.data());
                }
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else {
            block();
        }
    }
}
