package com.kgd.agents.navigator.behaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.navigator.RouteNavigatorAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

public class HandleNewWaypointRequestBehavior extends CyclicBehaviour {

    private final RouteNavigatorAgent agent;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HandleNewWaypointRequestBehavior(RouteNavigatorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        var message = agent.receive(template);

        if (message != null) {
            try {
                var waypoints = objectMapper.readValue(message.getContent(), GeoPoint[].class);
                addWaypoints(waypoints);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void addWaypoints(GeoPoint[] waypoints) {
        var name = agent.getLocalName();
        var destinationName = name.substring(0, name.length() - "_route_navigator".length());
        var destinationAID = new AID(destinationName, AID.ISLOCALNAME);

        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(destinationAID);
        agent.send(message);

        var reply = agent.receive(MessageTemplate.MatchSender(destinationAID));
        if (reply != null) {
            try {
                agent.addWaypoints(reply, waypoints);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
