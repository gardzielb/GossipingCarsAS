package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.models.geodata.Vec2;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class NotifyTrafficLightsBehavior extends OneShotBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID tlAgentId;
    private final TrafficLights trafficLights;
    private final GeoPoint carLocation;

    public NotifyTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, GeoPoint carLocation,
                                       TrafficLights trafficLights, AID tlAgentId) {
        this.controllerAgent = controllerAgent;
        this.tlAgentId = tlAgentId;
        this.trafficLights = trafficLights;
        this.carLocation = carLocation;
    }

    @Override
    public void action() {
        System.out.println("Notifying Traffic Lights");

        var notification = new ACLMessage(ACLMessage.INFORM);
        notification.addReceiver(tlAgentId);

        var approachDir = determineApproachDirection(carLocation);
        var serializer = new ObjectMapper();
        try {
            notification.setContent(serializer.writeValueAsString(approachDir));
            controllerAgent.send(notification);
            controllerAgent.approachTrafficLights(trafficLights, approachDir);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private Vec2 determineApproachDirection(GeoPoint carLocation) {
        var carVec = new Vec2(
                trafficLights.location().x() - carLocation.x(), trafficLights.location().y() - carLocation.y()
        ).normalized();

        for (var dirVec : trafficLights.approachDirections()) {
            var dirVecNorm = dirVec.normalized();
            double dotProduct = dirVecNorm.dot(carVec);

            // vectors close to parallel
            if (dotProduct > Math.cos(Math.PI / 12)) {
                return dirVec;
            }
        }

        return trafficLights.approachDirections()[0];
    }
}
