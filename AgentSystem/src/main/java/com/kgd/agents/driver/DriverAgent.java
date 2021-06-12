package com.kgd.agents.driver;

import com.kgd.agents.Main;
import com.kgd.agents.driver.behaviors.*;
import com.kgd.agents.models.geodata.DecodedRoute;
import com.kgd.agents.models.geodata.DecodedRouteSegment;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Stats;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.services.AgentLocationService;
import com.kgd.agents.services.HttpAgentLocationService;
import com.kgd.agents.services.HttpStatsService;
import com.kgd.agents.services.StatsService;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

public class DriverAgent extends Agent {

    public double time;
    public int routeSegment = 0;
    public int segmentFragment = 0;
    public double percent = 0.0;

    public double fullDistance = 0.0;
    public Behaviour calcPositionBehaviour;

    public boolean arrived = false;


    private double originX;
    private double originY;

    // velocity [km/h]
    protected double velocity = 0.0;
    protected double simulationSpeed = 1.0;
    private String destinationId;

    public DecodedRoute route = null;

    private AgentLocationService agentLocationService;
    private StatsService statsService;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        if (args == null || args.length < 4)
            throw new IllegalStateException("Expected origin, destination and car velocity [km/h] as arguments");

        originX = Double.parseDouble((String) args[0]);
        originY = Double.parseDouble((String) args[1]);
        destinationId = (String) args[2];
        velocity = Double.parseDouble((String) args[3]);
        velocity *= Main.getSimulationSpeed();


        agentLocationService = new HttpAgentLocationService();
        statsService = new HttpStatsService();

        addBehaviour(new RequestPositionBehaviour(this));
        addBehaviour(new NewRouteReceivedBehaviour(this));

        // querying the RouteNavigatorAgent for route
        var message = new ACLMessage(ACLMessage.QUERY_REF);
        message.addReceiver(new AID(getLocalName() + "_route_navigator", AID.ISLOCALNAME));

        send(message);

        calcPositionBehaviour = new CalculatePositionOnRouteBehaviour(this);
        addBehaviour(calcPositionBehaviour);
        addBehaviour(new UpdatePositionInDatabaseBehaviour(this, 3 * 1000));
        addBehaviour(new StartStopBehavior(this));
    }

    @Override
    public void takeDown() {
        agentLocationService.deleteAgentLocationByAID(getAID().toString());
        statsService.upsert(new Stats(null, getLocalName(), fullDistance, null, null, arrived));

        Thread t = new Thread(() -> {
            try {
                getContainerController().kill();
            } catch (ControllerException ignore) { }
        });
        t.start();
    }

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
}
