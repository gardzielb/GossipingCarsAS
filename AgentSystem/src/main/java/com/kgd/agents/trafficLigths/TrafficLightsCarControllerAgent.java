package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.DecodedRoute;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Route;
import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.services.EarthDistanceCalculator;
import com.kgd.agents.trafficLigths.controllerBehaviors.NotifyTrafficLightsBehavior;
import com.kgd.agents.trafficLigths.controllerBehaviors.UpdateTrafficLightsQueueBehavior;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayDeque;
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

        for (var segment : decodedRoute.segments) {
            for (int i = 0; i < segment.route.size(); i++) {
                var routePoint = segment.route.get(i);

                for (var tl : trafficLights) {
                    double distance = EarthDistanceCalculator.distance(
                            routePoint.y(), tl.location().y(), routePoint.x(), tl.location().x()
                    );
                    if (distance < HIT_RADIUS_KM) {
                        var notificationPoint = findNotificationPoint(segment.route, i);
                        trafficLightsQueue.add(new TrafficLightsData(tl, notificationPoint));
                        break;
                    }
                }
            }
        }

        prepareForNextTrafficLights();
    }

    public void prepareForNextTrafficLights() {
        var trafficLightsData = trafficLightsQueue.remove();

        var agentDescription = new DFAgentDescription();
        var serviceDescription = new ServiceDescription();
        serviceDescription.setName(trafficLightsData.trafficLights().id());
        serviceDescription.setType("trafficLights");
        agentDescription.addServices(serviceDescription);

        try {
            var tlAgents = DFService.search(this, agentDescription);
            currentTLInteractionBehavior = new NotifyTrafficLightsBehavior(
                    this, trafficLightsData.notificationPoint(), trafficLightsData.trafficLights(),
                    tlAgents[0].getName()
            );
            addBehaviour(currentTLInteractionBehavior);
        }
        catch (FIPAException e) {
            e.printStackTrace();
        }
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
