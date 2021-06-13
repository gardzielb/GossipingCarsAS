package com.kgd.agents.trafficLigths;

import com.kgd.agents.Main;
import com.kgd.agents.models.geodata.*;
import com.kgd.agents.services.EarthDistanceCalculator;
import com.kgd.agents.services.HttpStatsService;
import com.kgd.agents.services.LoggerFactory;
import com.kgd.agents.services.StatsService;
import com.kgd.agents.trafficLigths.controllerBehaviors.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TrafficLightsCarControllerAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger("TL Car Controller");

    private final StatsService statsService = new HttpStatsService();
    private final Queue<TrafficLightsData> trafficLightsQueue = new ArrayDeque<>();
    private Behaviour currentTLInteractionBehavior = null;

    private double notificationDistKm = 0.1;
    private boolean isDumb;
    private String uuid;

    private long tlWaitingMillis = 0;

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 2)
            throw new IllegalArgumentException("Specify car speed and required time for TL notification");

        double velocity = Double.parseDouble(args[0].toString());
        int notificationTime = Integer.parseInt(args[1].toString());

        isDumb = Boolean.parseBoolean(args[2].toString());
        uuid = (String) args[3];
        notificationDistKm = velocity * ((double) notificationTime / 3600);

        addBehaviour(new UpdateTrafficLightsQueueBehavior(this));
    }

    @Override
    protected void takeDown() {
        String name = getLocalName();
        var driverName = name.substring(0, name.length() - "_TL_controller".length());
        statsService.upsert(new Stats(uuid, null, null, (long) (tlWaitingMillis * Main.getSimulationSpeed()), null, null));
        System.out.println("Dying nicely on takedown");
        super.takeDown();
    }

    public void saveTLWaitingTime(long millis) {
        tlWaitingMillis += millis;
    }

    public void updateLightsQueue(Route route, List<TrafficLights> trafficLights) {
        if (currentTLInteractionBehavior != null)
            removeBehaviour(currentTLInteractionBehavior);

        trafficLightsQueue.clear();

        var decodedRoute = new DecodedRoute(route);
        double HIT_RADIUS_KM = 0.2;

        var fullPolyline = decodedRoute.segments.stream().map(seg -> seg.route).reduce(
                new ArrayList<>(), (prevList, segRoute) -> {
                    prevList.addAll(segRoute);
                    return prevList;
                }
        );

        for (int i = 0; i < fullPolyline.size(); i++) {
            var routePoint = fullPolyline.get(i);

            for (var tl : trafficLights) {
                double distance = EarthDistanceCalculator.distance(
                        routePoint.y(), tl.location().y(), routePoint.x(), tl.location().x()
                );
                if (distance < HIT_RADIUS_KM) {
                    var notificationPoint = findNotificationPoint(fullPolyline, i);
                    trafficLightsQueue.add(new TrafficLightsData(tl, notificationPoint));
                    trafficLights.remove(tl);
                    break;
                }
            }
        }

        prepareForNextTrafficLights();
    }

    public void prepareForNextTrafficLights() {
        if (trafficLightsQueue.isEmpty())
            return;

        var trafficLightsData = trafficLightsQueue.remove();
        logger.debug("Lights {} are ahead of me", trafficLightsData.trafficLights().id());

        if (isDumb) {
            approachTrafficLights(trafficLightsData.trafficLights());
            return;
        }

        var agentDescription = new DFAgentDescription();
        var serviceDescription = new ServiceDescription();
        serviceDescription.setName(trafficLightsData.trafficLights().id() + "_manager");
        serviceDescription.setType("trafficLightsManager");
        agentDescription.addServices(serviceDescription);

        try {
            var tlAgents = DFService.search(this, agentDescription);
            var notifyTlBehavior = new NotifyTrafficLightsBehavior(
                    this, trafficLightsData.trafficLights(), tlAgents[0].getName()
            );
            currentTLInteractionBehavior = new DriveToPointBehavior(
                    this, trafficLightsData.notificationPoint(), notifyTlBehavior
            );
            addBehaviour(currentTLInteractionBehavior);
        }
        catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public void setTlInteractionBehavior(Behaviour behavior) {
        currentTLInteractionBehavior = behavior;
        addBehaviour(currentTLInteractionBehavior);
    }

    public void approachTrafficLights(TrafficLights trafficLights) {
        logger.debug("Approaching lights {}", trafficLights.id());

        var agentDescription = new DFAgentDescription();
        var serviceDescription = new ServiceDescription();
        serviceDescription.setName(trafficLights.id() + "_signaler");
        serviceDescription.setType("trafficLightsSignaler");
        agentDescription.addServices(serviceDescription);

        try {
            var tlAgents = DFService.search(this, agentDescription);
            var approachTlBehavior = new ApproachTrafficLightsBehavior(this, tlAgents[0].getName());
            currentTLInteractionBehavior = new DriveToPointBehavior(
                    this, trafficLights.location(), approachTlBehavior
            );
            addBehaviour(currentTLInteractionBehavior);
        }
        catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public void passBetweenTrafficLights(AID tlAgent, GeoPoint exitPoint, String enterTlId) {
        logger.debug("Traffic lights {} let me pass", enterTlId);

        var exitTlBehavior = new ExitTrafficLightsBehavior(this, tlAgent, enterTlId);
        currentTLInteractionBehavior = new DriveToPointBehavior(this, exitPoint, exitTlBehavior);
        addBehaviour(currentTLInteractionBehavior);
    }

    private GeoPoint findNotificationPoint(List<GeoPoint> routePoints, int tlLocationIndex) {
        double distance = 0.0;
        int index = tlLocationIndex;

        do {
            if (index <= 0)
                return routePoints.get(0);

            var pt1 = routePoints.get(index);
            var pt2 = routePoints.get(index - 1);
            distance += EarthDistanceCalculator.distance(pt1.y(), pt2.y(), pt1.x(), pt2.x());
            index--;
        }
        while (distance < notificationDistKm);

        return routePoints.get(index);
    }
}

record TrafficLightsData(TrafficLights trafficLights, GeoPoint notificationPoint) {}
