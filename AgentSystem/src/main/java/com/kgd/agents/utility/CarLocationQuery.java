package com.kgd.agents.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.messages.CarLocationData;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CarLocationQuery {
    public CarLocationData blockingReceiveLocation(Agent agent, String carName) {
        var destinationAID = new AID(carName, AID.ISLOCALNAME);

        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(destinationAID);
        agent.send(message);

        var reply = agent.blockingReceive(MessageTemplate.MatchSender(destinationAID));

        try {
            return (new ObjectMapper()).readValue(reply.getContent(), CarLocationData.class);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
