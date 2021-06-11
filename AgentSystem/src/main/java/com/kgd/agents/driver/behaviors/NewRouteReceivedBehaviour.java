package com.kgd.agents.driver.behaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.driver.DriverAgent;
import com.kgd.agents.models.geodata.DecodedRoute;
import com.kgd.agents.models.geodata.Route;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class NewRouteReceivedBehaviour extends CyclicBehaviour {
    private final DriverAgent agent;

    public NewRouteReceivedBehaviour(DriverAgent driverAgent) {
        this.agent = driverAgent;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        var message = agent.receive(mt);

        if (message != null) {
            // decoding the route
            try {
                Route encodedRoute = (new ObjectMapper()).readValue(message.getContent(), Route.class);
                agent.route = new DecodedRoute(encodedRoute);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }

            agent.routeSegment = 0;
            agent.segmentFragment = 0;
            agent.percent = 0.0;
        }
        else {
            block();
        }

        agent.routeSegment = 0;
        agent.segmentFragment = 0;
        agent.percent = 0.0;

        // notify TrafficLightsController about route change
        var routeNotification = new ACLMessage(ACLMessage.INFORM);
        routeNotification.addReceiver(new AID(agent.getLocalName() + "_TL_controller", AID.ISLOCALNAME));
        routeNotification.setContent(message.getContent());
        agent.send(routeNotification);
    }
}
