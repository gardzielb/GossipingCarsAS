package com.kgd.agents.navigator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Route;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.navigator.behaviors.HandleNewWaypointRequestBehavior;
import com.kgd.agents.navigator.behaviors.HandleRouteQueryBehavior;
import com.kgd.agents.services.HttpRouteService;
import com.kgd.agents.services.RouteService;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.net.URISyntaxException;

public class RouteNavigatorAgent extends Agent {

    private final RouteService routeService = new HttpRouteService();
    private Route currentRoute;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        if (args == null || args.length < 3)
            throw new IllegalStateException("Expected origin and destination as arguments");

        var origin = new GeoPoint(Double.parseDouble((String) args[0]), Double.parseDouble((String) args[1]));
        var destinationId = (String) args[2];

        try {
            currentRoute = routeService.findRoute(origin, destinationId);
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to find route");
        }

        addBehaviour(new HandleNewWaypointRequestBehavior(this));
        addBehaviour(new HandleRouteQueryBehavior(this));
    }

    public void addWaypoints(GeoPoint[] waypoints) {
        var name = getLocalName();
        var destinationName = name.substring(0, name.length() - "_route_navigator".length());
        var destinationAID = new AID(destinationName, AID.ISLOCALNAME);

        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(destinationAID);
        send(message);

        var reply = blockingReceive(MessageTemplate.MatchSender(destinationAID));

        try {
            var origin = (new ObjectMapper()).readValue(reply.getContent(), CarLocationData.class).position();
            currentRoute = routeService.findRoute(origin, currentRoute.destinationId(), waypoints);
            System.out.println("Successfully changed route to " + currentRoute);

            var routeNotification = reply.createReply();
            routeNotification.setPerformative(ACLMessage.INFORM);
            routeNotification.setContent((new ObjectMapper()).writeValueAsString(currentRoute));

            send(routeNotification);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }
}
