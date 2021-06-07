package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.services.EarthDistanceCalculator;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DriveToPointBehavior extends SimpleBehaviour {

    private final TrafficLightsCarControllerAgent agent;
    private final GeoPoint destPoint;
    private final Behaviour nextBehavior;
    private boolean hasArrived = false;

    public DriveToPointBehavior(TrafficLightsCarControllerAgent agent, GeoPoint destPoint, Behaviour nextBehavior) {
        this.agent = agent;
        this.destPoint = destPoint;
        this.nextBehavior = nextBehavior;
    }

    @Override
    public void action() {
        String agentName = agent.getLocalName();
        String carName = agentName.substring(0, agentName.length() - "_TL_controller".length());
        var destinationAID = new AID(carName, AID.ISLOCALNAME);

        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(destinationAID);
        agent.send(message);

        var reply = agent.receive(MessageTemplate.MatchSender(destinationAID));
        if (reply != null) {
            try {
                var carLocation = (new ObjectMapper()).readValue(reply.getContent(), CarLocationData.class).position();
                double dist = EarthDistanceCalculator.distance(
                        carLocation.y(), destPoint.y(), carLocation.x(), destPoint.x()
                );

                if (dist < 0.1) {
                    hasArrived = true;
                    agent.setTlInteractionBehavior(nextBehavior);
                }
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
        return hasArrived;
    }
}
