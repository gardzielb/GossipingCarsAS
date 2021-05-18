package com.kgd.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.GeoPoint;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class TestingAgent extends Agent {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        var waypoint = new GeoPoint(Double.parseDouble((String) args[0]), Double.parseDouble((String) args[1]));

        var behavior = new OneShotBehaviour() {
            @Override
            public void action() {
                var message = new ACLMessage(ACLMessage.REQUEST);
                message.addReceiver(new AID("RouteNavigator", AID.ISLOCALNAME));
                var waypoints = new GeoPoint[]{waypoint};

                try {
                    message.setContent(objectMapper.writeValueAsString(waypoints));
                    send(message);
                }
                catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        };

        addBehaviour(behavior);
    }
}
