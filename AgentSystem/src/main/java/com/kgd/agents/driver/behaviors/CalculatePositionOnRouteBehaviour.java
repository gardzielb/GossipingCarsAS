package com.kgd.agents.driver.behaviors;

import com.kgd.agents.driver.DriverAgent;
import com.kgd.agents.models.DecodedRouteSegment;
import com.kgd.agents.models.EarthDistanceCalculator;
import com.kgd.agents.models.GeoPoint;
import jade.core.behaviours.CyclicBehaviour;

import java.time.Instant;

public class CalculatePositionOnRouteBehaviour extends CyclicBehaviour {
    protected DriverAgent agent;

    public CalculatePositionOnRouteBehaviour(DriverAgent agent){
        super();
        this.agent = agent;
    }

    @Override
    public void action() {
        double deltaTime = (Instant.now().toEpochMilli() - agent.time) / 1000.0; // seconds
        agent.time += deltaTime * 1000.0; // milliseconds

        // distance in kilometers
        double distance = deltaTime * (agent.getVelocity() / 3600.0);

        while (distance != 0.0) {
            // drive to the next point or as far as possible
            GeoPoint position = agent.getPosition();
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

                    // passing the final endpoint on route
                    if (agent.routeSegment == agent.route.segments.size()) {
                        System.out.println(agent.getLocalName()+" has reached their destination.");
                        agent.takeDown();
                        return;
                    }
                }
            } else {
                agent.percent += distance / EarthDistanceCalculator.distance(lastPoint.y(), nextPoint.y(), lastPoint.x(), nextPoint.x());
            }

            distance -= Math.min(distance, distanceLeft);
        }
    }
}
