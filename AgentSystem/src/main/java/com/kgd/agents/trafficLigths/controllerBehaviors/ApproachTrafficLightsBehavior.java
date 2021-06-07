package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.Vec2;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ApproachTrafficLightsBehavior extends OneShotBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID tlSignalerId;
    private final Vec2 approachDirection;
    private final ObjectMapper serializer = new ObjectMapper();

    public ApproachTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, AID tlSignalerId,
                                         Vec2 approachDirection) {
        this.controllerAgent = controllerAgent;
        this.tlSignalerId = tlSignalerId;
        this.approachDirection = approachDirection;
    }

    @Override
    public void action() {
        System.out.println("Asking light signaler if passage possible");

        var canPassQuery = new ACLMessage(ACLMessage.QUERY_IF);
        canPassQuery.addReceiver(tlSignalerId);
        try {
            canPassQuery.setContent(serializer.writeValueAsString(approachDirection));
            controllerAgent.send(canPassQuery);

            var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
            var canPassResponse = controllerAgent.blockingReceive(msgTemplate);

            if (canPassResponse != null) {
                System.out.println("Signaler allowed me to pass");
                controllerAgent.prepareForNextTrafficLights();
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
