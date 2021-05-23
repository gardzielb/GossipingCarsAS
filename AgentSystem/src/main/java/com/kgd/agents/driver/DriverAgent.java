package com.kgd.agents.driver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.driver.behaviors.CalculatePositionOnRouteBehaviour;
import com.kgd.agents.models.DecodedRoute;
import com.kgd.agents.models.DecodedRouteSegment;
import com.kgd.agents.models.GeoPoint;
import com.kgd.agents.models.Route;
import com.kgd.agents.navigator.RouteNavigatorAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.time.Instant;
import java.util.Arrays;

public class DriverAgent extends Agent {

    public double time;
    public int routeFragment = 0;
    public int fragmentSection = 0;
    public double percent = 0.0;

    // velocity [km/h]
    protected double velocity = 0.0;

    public DecodedRoute route = null;

    @Override
    protected void setup() {
        super.setup();

        time = Instant.now().toEpochMilli();

        Object[] args = getArguments();
        if (args == null || args.length < 3)
            throw new IllegalStateException("Expected origin, destination and car velocity [km/h] as arguments");

        Object[] routeManagerArgs = Arrays.copyOf(args, 3);
        velocity = Double.parseDouble((String) args[3]);

        // creating a route navigator
        AgentContainer c = getContainerController();
        try {
            AgentController a = c.createNewAgent(getLocalName() + "_route_navigator", RouteNavigatorAgent.class.getName(), routeManagerArgs);
            a.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        // querying the RouteNavigatorAgent for route
        var message = new ACLMessage(ACLMessage.QUERY_REF);
        message.addReceiver(new AID(getLocalName() + "_route_navigator", AID.ISLOCALNAME));
        send(message);

        var template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        message = blockingReceive(template);

        // decoding the route
        try {
            Route encodedRoute = (new ObjectMapper()).readValue(message.getContent(), Route.class);
            route = new DecodedRoute(encodedRoute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // TODO: add behaviours for calculating position and handling new routes
        addBehaviour(new CalculatePositionOnRouteBehaviour(this));
    }

    // get
    public double getVelocity() {
        return velocity;
    }

    public GeoPoint getPosition() {
        DecodedRouteSegment segment = route.segments.get(routeFragment);
        GeoPoint lastPoint = segment.route.get(fragmentSection);
        GeoPoint nextPoint = segment.route.get(fragmentSection + 1);
        GeoPoint position = lastPoint;

        if (percent > 0.0) {
            double dx = nextPoint.x() - lastPoint.x();
            double dy = nextPoint.y() - lastPoint.y();

            position = new GeoPoint(position.x() + percent * dx, position.y() + percent * dy);
        }

        return position;
    }
}
