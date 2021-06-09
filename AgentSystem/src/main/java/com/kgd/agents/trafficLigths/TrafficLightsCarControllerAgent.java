package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.*;
import com.kgd.agents.services.EarthDistanceCalculator;
import com.kgd.agents.trafficLigths.controllerBehaviors.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TrafficLightsCarControllerAgent extends Agent {

    private final Queue<TrafficLightsData> trafficLightsQueue = new ArrayDeque<>();
    private Behaviour currentTLInteractionBehavior = null;

    private double notificationDistKm = 0.1;

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 2)
            throw new IllegalArgumentException("Specify car speed and required time for TL notification");

        double velocity = Double.parseDouble(args[0].toString());
        int notificationTime = Integer.parseInt(args[1].toString());
        notificationDistKm = velocity * ((double) notificationTime / 3600);

        addBehaviour(new UpdateTrafficLightsQueueBehavior(this));
    }

    public void updateLightsQueue(Route route, List<TrafficLights> trafficLights) {
        System.out.println("Received new route, updating TL queue");

        if (currentTLInteractionBehavior != null)
            removeBehaviour(currentTLInteractionBehavior);

        trafficLightsQueue.clear();

        var decodedRoute = new DecodedRoute(route);
        double HIT_RADIUS_KM = 0.1;

        var fullPolyline = decodedRoute.segments.stream().map(seg -> seg.route).reduce(
                new ArrayList<>(), (prevList, segRoute) -> {
                    prevList.addAll(segRoute);
                    return prevList;
                }
        );

        for (var tl : trafficLights) {
            for (int i = 0; i < fullPolyline.size(); i++) {
                var routePoint = fullPolyline.get(i);
                double distance = EarthDistanceCalculator.distance(
                        routePoint.y(), tl.location().y(), routePoint.x(), tl.location().x()
                );
                if (distance < HIT_RADIUS_KM) {
                    var notificationPoint = findNotificationPoint(fullPolyline, i);
                    trafficLightsQueue.add(new TrafficLightsData(tl, notificationPoint));
                    break;
                }
            }
        }

        prepareForNextTrafficLights();
    }

    public void prepareForNextTrafficLights() {
        if (trafficLightsQueue.isEmpty())
            return;

        System.out.println("Next TL ahead of me");

        var trafficLightsData = trafficLightsQueue.remove();
        System.out.println(trafficLightsData);

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
        var agentDescription = new DFAgentDescription();
        var serviceDescription = new ServiceDescription();
        serviceDescription.setName(trafficLights.id() + "_signaler");
        serviceDescription.setType("trafficLightsSignaler");
        agentDescription.addServices(serviceDescription);

        try {
            var tlAgents = DFService.search(this, agentDescription);
            var approachTlBehavior = new ApproachTrafficLightsBehavior(this, tlAgents[0].getName());
            currentTLInteractionBehavior = new DriveToPointBehavior(
                    this, trafficLights.location(), approachTlBehavior);
            addBehaviour(currentTLInteractionBehavior);
        }
        catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public void passBetweenTrafficLights(AID tlAgent, GeoPoint exitPoint) {
        var exitTlBehavior = new ExitTrafficLightsBehavior(this, tlAgent);
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
