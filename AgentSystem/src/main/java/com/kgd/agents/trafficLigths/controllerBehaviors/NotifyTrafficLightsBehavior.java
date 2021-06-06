package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.services.EarthDistanceCalculator;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class NotifyTrafficLightsBehavior extends SimpleBehaviour {

    private final Agent controllerAgent;
    private final AID tlAgentId;
    private final TrafficLights trafficLights;
    private final GeoPoint notificationPoint;
    private boolean isTLNotified = false;

    public NotifyTrafficLightsBehavior(Agent controllerAgent, GeoPoint notificationPoint, TrafficLights trafficLights,
                                       AID tlAgentId) {
        this.controllerAgent = controllerAgent;
        this.tlAgentId = tlAgentId;
        this.trafficLights = trafficLights;
        this.notificationPoint = notificationPoint;
    }

    @Override
    public void action() {
        String agentName = controllerAgent.getLocalName();
        String carName = agentName.substring(0, agentName.length() - "_TL_controller".length());
        var destinationAID = new AID(carName, AID.ISLOCALNAME);

        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(destinationAID);
        controllerAgent.send(message);

        var reply = controllerAgent.receive(MessageTemplate.MatchSender(destinationAID));
        if (reply != null) {
            try {
                var carLocation = (new ObjectMapper()).readValue(reply.getContent(), CarLocationData.class);
                handleTLNotification(carLocation);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return isTLNotified;
    }

    private void handleTLNotification(CarLocationData carLocation) {
        System.out.println("Car location = " + carLocation.position() + ", notificationPoint = " + notificationPoint);
        var carPosition = carLocation.position();
        double dist = EarthDistanceCalculator.distance(
                carPosition.y(), notificationPoint.y(), carPosition.x(), notificationPoint.x()
        );

        if (dist < 0.1) {
            System.out.println("Notifying Traffic Lights");

            var notification = new ACLMessage(ACLMessage.INFORM);
            notification.addReceiver(tlAgentId);
            notification.setContent("");
            controllerAgent.send(notification);
            isTLNotified = true;
        }
    }
}
