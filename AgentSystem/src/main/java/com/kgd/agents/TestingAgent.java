package com.kgd.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Route;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class TestingAgent extends Agent {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        var waypoint = new GeoPoint(Double.parseDouble((String) args[0]), Double.parseDouble((String) args[1]));
        requestWaypoint(waypoint);

        var behavior = new SimpleBehaviour() {
            private boolean isDone = false;

            @Override
            public void action() {
                var message = receive();
                if (message != null) {
                    System.out.println(
                            "Got message '" + message.getContent() + "' from " + message.getSender().getLocalName()
                    );

                    switch (message.getPerformative()) {
                        case ACLMessage.AGREE:
                            System.out.println("My waypoint request was accepted");
                            break;
                        case ACLMessage.INFORM:
                            if (message.getContent().equals("DONE")) {
                                System.out.println("My waypoint was added to route");
                                askForRoute();
                            }
                            else {
                                try {
                                    var route = objectMapper.readValue(message.getContent(), Route.class);
                                    System.out.println("Current route begins in " + route.origin());
                                    System.out.println("Current route ends in place with id " + route.destinationId());
                                    System.out.println("Current route has " + route.segments().size() + " segments");
                                }
                                catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    isDone = true;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public boolean done() {
                return isDone;
            }
        };

        addBehaviour(behavior);
    }

    private void requestWaypoint(GeoPoint waypoint) {
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

    private void askForRoute() {
        var message = new ACLMessage(ACLMessage.QUERY_REF);
        message.addReceiver(new AID("RouteNavigator", AID.ISLOCALNAME));
        send(message);
    }
}
