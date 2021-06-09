package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.messages.TrafficLightExitData;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

// TODO: this thing is messy, clean it up, man
public class ApproachTrafficLightsBehavior extends SimpleBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID tlSignalerId;
    private final ObjectMapper deserializer = new ObjectMapper();

    private boolean canPassTL = false;
    private boolean isCarStopped = false;

    public ApproachTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, AID tlSignalerId) {
        this.controllerAgent = controllerAgent;
        this.tlSignalerId = tlSignalerId;
    }

    @Override
    public void action() {
        System.out.println("Asking light signaler if passage possible");

        var canPassQuery = new ACLMessage(ACLMessage.QUERY_IF);
        canPassQuery.addReceiver(tlSignalerId);
        try {
            controllerAgent.send(canPassQuery);

            var msgTemplate = MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                    MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
            );
            var canPassResponse = controllerAgent.receive(msgTemplate);

            if (canPassResponse != null) {
                if (canPassResponse.getPerformative() == ACLMessage.AGREE) {
                    System.out.println("Signaler allowed me to pass");

                    if (canPassResponse.getContent().isBlank()) {
                        controllerAgent.prepareForNextTrafficLights();
                    }
                    else {
                        var exitData = deserializer.readValue(canPassResponse.getContent(), TrafficLightExitData.class);
                        controllerAgent.passBetweenTrafficLights(
                                new AID(exitData.agentName(), AID.ISLOCALNAME), exitData.exitPoint()
                        );
                    }

                    canPassTL = true;
                }
                else if (!isCarStopped) {
                    System.out.println("Stopping the car");
                    isCarStopped = true;
                }
            }
            else {
                block();
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean done() {
        return canPassTL;
    }
}
