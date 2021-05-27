package com.kgd.agents.driver;

import com.kgd.agents.driver.behaviors.CalculatePositionOnRouteBehaviour;
import com.kgd.agents.driver.behaviors.NewRouteReceivedBehaviour;
import com.kgd.agents.driver.behaviors.RequestPositionBehaviour;
import com.kgd.agents.driver.behaviors.UpdatePositionInDatabaseBehaviour;
import com.kgd.agents.models.CarLocationData;
import com.kgd.agents.models.DecodedRoute;
import com.kgd.agents.models.DecodedRouteSegment;
import com.kgd.agents.models.GeoPoint;
import com.kgd.agents.services.AgentLocationService;
import com.kgd.agents.services.HttpAgentLocationService;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

import java.time.Instant;

public class DriverAgent extends Agent {

    public double time;
    public int routeSegment = 0;
    public int segmentFragment = 0;
    public double percent = 0.0;

    private double originX;
    private double originY;

    // velocity [km/h]
    protected double velocity = 0.0;
    private String destinationId;

    public DecodedRoute route = null;

    private AgentLocationService agentLocationService;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        if (args == null || args.length < 3)
            throw new IllegalStateException("Expected origin, destination and car velocity [km/h] as arguments");

        originX = Double.parseDouble((String) args[0]);
        originY = Double.parseDouble((String) args[1]);
        destinationId = (String) args[2];
        velocity = Double.parseDouble((String) args[3]);

        agentLocationService = new HttpAgentLocationService();

        addBehaviour(new RequestPositionBehaviour(this));
        addBehaviour(new NewRouteReceivedBehaviour(this));

        // querying the RouteNavigatorAgent for route
        var message = new ACLMessage(ACLMessage.QUERY_REF);
        message.addReceiver(new AID(getLocalName() + "_route_navigator", AID.ISLOCALNAME));
        send(message);

        time = Instant.now().toEpochMilli();

        addBehaviour(new CalculatePositionOnRouteBehaviour(this));
        addBehaviour(new UpdatePositionInDatabaseBehaviour(this, 10 * 1000));
    }

    @Override
    public void takeDown() {
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

    public CarLocationData getLocationInfo() {
        if (route == null) {
            return new CarLocationData(new GeoPoint(originX, originY), destinationId);
        }

        DecodedRouteSegment segment = route.segments.get(routeSegment);
        GeoPoint lastPoint = segment.route.get(segmentFragment);
        GeoPoint nextPoint = segment.route.get(segmentFragment + 1);
        GeoPoint position = lastPoint;

        if (percent > 0.0) {
            double dx = nextPoint.x() - lastPoint.x();
            double dy = nextPoint.y() - lastPoint.y();

            position = new GeoPoint(position.x() + percent * dx, position.y() + percent * dy);
        }

        return new CarLocationData(position, destinationId);
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
