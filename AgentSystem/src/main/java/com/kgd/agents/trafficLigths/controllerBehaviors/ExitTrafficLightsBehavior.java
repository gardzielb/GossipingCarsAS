package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.messages.TrafficLightNotification;
import com.kgd.agents.trafficLigths.NotificationType;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ExitTrafficLightsBehavior extends OneShotBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID trafficLightsAgent;
    private final ObjectMapper serializer = new ObjectMapper();
    private final String trafficLightId;

    public ExitTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, AID trafficLightsAgent,
                                     String trafficLightId) {
        this.controllerAgent = controllerAgent;
        this.trafficLightsAgent = trafficLightsAgent;
        this.trafficLightId = trafficLightId;
    }

    @Override
    public void action() {
        var exitNotification = new ACLMessage(ACLMessage.INFORM);
        exitNotification.addReceiver(trafficLightsAgent);
        var notificationContent = new TrafficLightNotification(NotificationType.EXIT, trafficLightId);

        try {
            exitNotification.setContent(serializer.writeValueAsString(notificationContent));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        controllerAgent.send(exitNotification);
        controllerAgent.prepareForNextTrafficLights();
    }
}
