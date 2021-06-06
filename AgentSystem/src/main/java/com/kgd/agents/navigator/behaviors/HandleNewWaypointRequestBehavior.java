package com.kgd.agents.navigator.behaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.navigator.RouteNavigatorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
                agent.addWaypoints(waypoints);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
