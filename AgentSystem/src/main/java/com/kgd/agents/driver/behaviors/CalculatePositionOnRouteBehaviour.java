package com.kgd.agents.driver.behaviors;

import com.kgd.agents.driver.DriverAgent;
import com.kgd.agents.models.geodata.DecodedRouteSegment;
import com.kgd.agents.services.EarthDistanceCalculator;
import com.kgd.agents.models.geodata.GeoPoint;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.time.Instant;

public class CalculatePositionOnRouteBehaviour extends TickerBehaviour {
    protected DriverAgent agent;
    public boolean done = false;

    public CalculatePositionOnRouteBehaviour(DriverAgent agent) {
        super(agent, 200);
        agent.time = Instant.now().toEpochMilli();
        this.agent = agent;
    }

    @Override
    public void onTick() {
        if (done || agent.route == null) return;

        double deltaTime = (Instant.now().toEpochMilli() - agent.time) / 1000.0; // seconds
        agent.time += deltaTime * 1000.0; // milliseconds

        // distance in kilometers
        double distance = deltaTime * (agent.getVelocity() / 3600.0);
        agent.fullDistance += distance;

        var message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID(agent.getLocalName() + "_fuel_controller", AID.ISLOCALNAME));
        message.setContent(Double.toString(distance));
        agent.send(message);

        while (distance != 0.0) {
            // drive to the next point or as far as possible
            GeoPoint position = agent.getLocationInfo().position();
            DecodedRouteSegment segment = agent.route.segments.get(agent.routeSegment);
            GeoPoint lastPoint = segment.route.get(agent.segmentFragment);
            GeoPoint nextPoint = segment.route.get(agent.segmentFragment + 1);

            double distanceLeft = EarthDistanceCalculator.distance(position.y(), nextPoint.y(), position.x(), nextPoint.x());

            // passing a node on route
            if (distance > distanceLeft) {
                agent.percent = 0.0;
                agent.segmentFragment++;

                // passing a waypoint on route
                if (agent.segmentFragment == segment.route.size() - 1) {
                    agent.routeSegment++;
                    agent.segmentFragment = 0;

                    if (agent.routeSegment == agent.route.segments.size()) {
                        // passing the final endpoint on route
                        System.out.println(agent.getLocalName()+" has reached their destination.");
                        done = true;
                        agent.takeDown();
                        return;
                    } else {
                        // passing a fuel station, notify FuelManager about filling the tank
                        ACLMessage fuelNotification = new ACLMessage(ACLMessage.REQUEST);
                        fuelNotification.addReceiver(new AID(agent.getLocalName() + "_fuel_controller", AID.ISLOCALNAME));
                        agent.send(fuelNotification);
                    }
                }
            } else {
                agent.percent += distance / EarthDistanceCalculator.distance(lastPoint.y(), nextPoint.y(), lastPoint.x(), nextPoint.x());
            }

            distance -= Math.min(distance, distanceLeft);
        }
    }
}
