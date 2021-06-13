package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.models.messages.TrafficLightNotification;
import com.kgd.agents.trafficLigths.NotificationType;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class NotifyTrafficLightsBehavior extends OneShotBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID tlAgentId;
    private final TrafficLights trafficLights;
    private final ObjectMapper serializer = new ObjectMapper();

    public NotifyTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, TrafficLights trafficLights,
                                       AID tlAgentId) {
        this.controllerAgent = controllerAgent;
        this.tlAgentId = tlAgentId;
        this.trafficLights = trafficLights;
    }

    @Override
    public void action() {
        var approachNotification = new ACLMessage(ACLMessage.INFORM);
        approachNotification.addReceiver(tlAgentId);
        var notificationContent = new TrafficLightNotification(NotificationType.EXPECTED_APPROACH, trafficLights.id());

        try {
            approachNotification.setContent(serializer.writeValueAsString(notificationContent));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        controllerAgent.send(approachNotification);
        controllerAgent.approachTrafficLights(trafficLights);
    }
}
