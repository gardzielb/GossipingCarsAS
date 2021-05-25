package com.kgd.agents.driver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.driver.behaviors.CalculatePositionOnRouteBehaviour;
import com.kgd.agents.driver.behaviors.UpdatePositionInDatabaseBehaviour;
import com.kgd.agents.models.DecodedRoute;
import com.kgd.agents.models.DecodedRouteSegment;
import com.kgd.agents.models.GeoPoint;
import com.kgd.agents.models.Route;
import com.kgd.agents.navigator.RouteNavigatorAgent;
import com.kgd.agents.services.AgentLocationService;
import com.kgd.agents.services.HttpAgentLocationService;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.*;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.*;

import java.time.Instant;
import java.util.Arrays;

public class DriverAgent extends Agent {

    public double time;
    public int routeSegment = 0;
    public int segmentFragment = 0;
    public double percent = 0.0;

    // velocity [km/h]
    protected double velocity = 0.0;

    public DecodedRoute route = null;

    private AgentLocationService agentLocationService;

    @Override
    protected void setup() {
        // create a sub-container
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, getLocalName()+"_container");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        ContainerController container = runtime.createAgentContainer(profile);

        ContainerID destination = new ContainerID();

        try {
            destination.setName(container.getContainerName());
            destination.setAddress(container.getPlatformName());
        } catch (ControllerException e) {
            e.printStackTrace();
        }

        doMove(destination);

        super.setup();

        Object[] args = getArguments();
        if (args == null || args.length < 3)
            throw new IllegalStateException("Expected origin, destination and car velocity [km/h] as arguments");

        Object[] routeManagerArgs = Arrays.copyOf(args, 3);
        velocity = Double.parseDouble((String) args[3]);

        // creating a route navigator
        try {
            AgentController a = container.createNewAgent(getLocalName() + "_route_navigator", RouteNavigatorAgent.class.getName(), routeManagerArgs);
            a.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterMove() {
        agentLocationService = new HttpAgentLocationService();
        // querying the RouteNavigatorAgent for route
        var message = new ACLMessage(ACLMessage.QUERY_REF);
        message.addReceiver(new AID(getLocalName() + "_route_navigator", AID.ISLOCALNAME));
        send(message);

        var template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        try {
            message = blockingReceive(template);
        } catch (Interrupted e) {
            e.printStackTrace();
            doDelete();
        }

        // decoding the route
        try {
            Route encodedRoute = (new ObjectMapper()).readValue(message.getContent(), Route.class);
            route = new DecodedRoute(encodedRoute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        time = Instant.now().toEpochMilli();

        // TODO: add behaviours for calculating position and handling new routes
        addBehaviour(new CalculatePositionOnRouteBehaviour(this));
        addBehaviour(new UpdatePositionInDatabaseBehaviour(this, 10*1000));
    }

    @Override
    public void takeDown() {
        // TODO: delete record with position from database
        agentLocationService.deleteAgentLocationByAID(getAID().toString());

        Thread t = new Thread(() -> {
            try {
                getContainerController().kill();
            } catch (ControllerException ignore) { }
        });
        t.start();
    }

    // get
    public double getVelocity() {
        return velocity;
    }

    public GeoPoint getPosition() {
        DecodedRouteSegment segment = route.segments.get(routeSegment);
        GeoPoint lastPoint = segment.route.get(segmentFragment);
        GeoPoint nextPoint = segment.route.get(segmentFragment + 1);
        GeoPoint position = lastPoint;

        if (percent > 0.0) {
            double dx = nextPoint.x() - lastPoint.x();
            double dy = nextPoint.y() - lastPoint.y();

            position = new GeoPoint(position.x() + percent * dx, position.y() + percent * dy);
        }

        return position;
    }

    private void sendRequest(Action action) {

        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setLanguage(new SLCodec().getName());
        request.setOntology(MobilityOntology.getInstance().getName());
        try {
            getContentManager().fillContent(request, action);
            request.addReceiver(action.getActor());
            send(request);
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }
}
