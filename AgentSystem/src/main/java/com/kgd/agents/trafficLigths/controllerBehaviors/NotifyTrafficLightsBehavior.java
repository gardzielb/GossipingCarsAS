package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class NotifyTrafficLightsBehavior extends OneShotBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID tlAgentId;
    private final TrafficLights trafficLights;

    public NotifyTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, TrafficLights trafficLights,
                                       AID tlAgentId) {
        this.controllerAgent = controllerAgent;
        this.tlAgentId = tlAgentId;
        this.trafficLights = trafficLights;
    }

    @Override
    public void action() {
        System.out.println("Notifying Traffic Lights");
        var notification = new ACLMessage(ACLMessage.INFORM);
        notification.addReceiver(tlAgentId);
        notification.setContent(trafficLights.id());
        controllerAgent.send(notification);
        controllerAgent.approachTrafficLights(trafficLights);
    }
}
