package com.kgd.agents.driver.behaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.driver.DriverAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestPositionBehaviour extends CyclicBehaviour {
    private final DriverAgent agent;

    public RequestPositionBehaviour(DriverAgent driverAgent) {
        agent = driverAgent;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        var message = agent.receive(mt);

        if (message == null) return;

        var location = agent.getPosition();
        var reply = message.createReply();

        try {
            reply.setContent((new ObjectMapper()).writeValueAsString(location));
        } catch (JsonProcessingException e) {
            reply.setContent(null);
        }

        agent.send(reply);
    }
}
