package com.kgd.agents.trafficLigths.managerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.Vec2;
import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SignalTrafficLightColorBehavior extends CyclicBehaviour {

    private final TrafficLightSignalerAgent agent;
    private final ObjectMapper deserializer = new ObjectMapper();

    public SignalTrafficLightColorBehavior(TrafficLightSignalerAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
        var isGreenQuery = agent.receive(msgTemplate);

        if (isGreenQuery != null) {
            System.out.println("Received query from " + isGreenQuery.getSender().getLocalName());

            var reply = isGreenQuery.createReply();

            var dirJson = isGreenQuery.getContent();
            try {
                var dirVec = deserializer.readValue(dirJson, Vec2.class);
                reply.setPerformative(ACLMessage.INFORM_IF);
                reply.setContent(agent.isGreen(dirVec) ? "true" : "false");
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                reply.setContent(e.getMessage());
            }

            agent.send(reply);
        }
        else {
            block();
        }
    }
}
