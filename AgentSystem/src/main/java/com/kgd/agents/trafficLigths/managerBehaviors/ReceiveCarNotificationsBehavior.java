package com.kgd.agents.trafficLigths.managerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.messages.TrafficLightNotification;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveCarNotificationsBehavior extends CyclicBehaviour {

    private final Agent managerAgent;
    private final ObjectMapper deserializer = new ObjectMapper();

    public ReceiveCarNotificationsBehavior(Agent managerAgent) {
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
                    case EXPECTED_APPROACH -> handleExpectedApproach(
                            notification.getSender().getLocalName(), notificationContent.data()
                    );
                    case PASS_THROUGH -> handlePassThrough(notificationContent.data());
                    case EXIT -> handleExit(notificationContent.data());
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

    private void handleExpectedApproach(String carId, String lightsId) {
        System.out.println("Car " + carId + " is coming to lights " + lightsId);
    }

    private void handlePassThrough(String carId) {
        System.out.println("Car " + carId + " is passing through");
    }

    private void handleExit(String carId) {
        System.out.println("Car " + carId + " is exiting");
    }
}
