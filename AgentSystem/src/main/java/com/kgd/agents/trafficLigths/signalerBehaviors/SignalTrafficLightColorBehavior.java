package com.kgd.agents.trafficLigths.signalerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.messages.TrafficLightExitData;
import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SignalTrafficLightColorBehavior extends CyclicBehaviour {

    private final TrafficLightSignalerAgent agent;
    private final GeoPoint exitPoint;
    private final String exitControllerName;
    private final ObjectMapper serializer = new ObjectMapper();

    public SignalTrafficLightColorBehavior(TrafficLightSignalerAgent agent, GeoPoint exitPoint,
                                           String exitControllerName) {
        this.agent = agent;
        this.exitPoint = exitPoint;
        this.exitControllerName = exitControllerName;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
        var isGreenQuery = agent.receive(msgTemplate);

        if (isGreenQuery != null) {
            System.out.println("Received query from " + isGreenQuery.getSender().getLocalName());

            var reply = isGreenQuery.createReply();
            if (agent.isGreen()) {
                reply.setPerformative(ACLMessage.AGREE);
                var exitData = new TrafficLightExitData(exitControllerName, exitPoint);
                try {
                    reply.setContent(serializer.writeValueAsString(exitData));
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            else {
                reply.setPerformative(ACLMessage.REFUSE);
            }

            agent.send(reply);
        }
        else {
            block();
        }
    }
}
