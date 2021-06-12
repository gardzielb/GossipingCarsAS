package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.messages.TrafficLightExitData;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ApproachTrafficLightsBehavior extends SimpleBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID tlSignalerId;
    private final ObjectMapper deserializer = new ObjectMapper();

    private boolean canPassTL = false;
    private boolean isAsking = true;
    private boolean isCarStopped = false;

    public ApproachTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, AID tlSignalerId) {
        this.controllerAgent = controllerAgent;
        this.tlSignalerId = tlSignalerId;
    }

    @Override
    public void action() {

        if (isAsking) {
            var canPassQuery = new ACLMessage(ACLMessage.QUERY_IF);
            canPassQuery.addReceiver(tlSignalerId);
            controllerAgent.send(canPassQuery);
            isAsking = false;
        }

        var msgTemplate = MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
        );
        var canPassResponse = controllerAgent.receive(msgTemplate);

        if (canPassResponse != null) {
            if (canPassResponse.getPerformative() == ACLMessage.AGREE) {
//                System.out.println("Signaler allowed me to pass");
                changeCarMovement("start");

                try {
                    var exitData = deserializer.readValue(canPassResponse.getContent(), TrafficLightExitData.class);
                    controllerAgent.passBetweenTrafficLights(
                            new AID(exitData.agentName(), AID.ISLOCALNAME), exitData.exitPoint(),
                            tlSignalerId.getLocalName()
                    );
                    canPassTL = true;
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            else if (!isCarStopped) {
//                System.out.println("Stopping the car");
                changeCarMovement("stop");
                isCarStopped = true;
            }

            isAsking = canPassResponse.getPerformative() == ACLMessage.REFUSE;
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return canPassTL;
    }

    private void changeCarMovement(String command) {
        String agentName = controllerAgent.getLocalName();
        String carName = agentName.substring(0, agentName.length() - "_TL_controller".length());
        var destinationAID = new AID(carName, AID.ISLOCALNAME);

        var stopRequest = new ACLMessage(ACLMessage.PROPOSE);
        stopRequest.addReceiver(destinationAID);
        stopRequest.setContent(command);

        controllerAgent.send(stopRequest);
    }
}
