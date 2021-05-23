package com.kgd.agents.driver.behaviors;

import com.kgd.agents.driver.DriverAgent;
import com.kgd.agents.models.DecodedRouteSegment;
import com.kgd.agents.models.GeoPoint;
import jade.core.behaviours.CyclicBehaviour;

import java.time.Instant;

public class CalculatePositionOnRouteBehaviour extends CyclicBehaviour {
    protected DriverAgent agent;
    protected double accumulatedDeltaTime = 0.0;

    public CalculatePositionOnRouteBehaviour(DriverAgent agent){
        super();
        this.agent = agent;
    }

    @Override
    public void action() {
        double deltaTime = (Instant.now().toEpochMilli() - agent.time) / 1000.0; // seconds
        agent.time += deltaTime * 1000.0; // milliseconds
        accumulatedDeltaTime += deltaTime;

        // distance in kilometers
        double distance = deltaTime * (agent.getVelocity() / 3600.0);

        while (distance != 0.0) {
            // walk the path
            GeoPoint position = agent.getPosition();
            DecodedRouteSegment segment = agent.route.segments.get(agent.routeFragment);
            GeoPoint lastPoint = segment.route.get(agent.fragmentSection);
            GeoPoint nextPoint = segment.route.get(agent.fragmentSection + 1);

            double distanceLeft = CalculatePositionOnRouteBehaviour.distance(position.y(), nextPoint.y(), position.x(), nextPoint.x());

            // passing a node on route
            if (distance > distanceLeft) {
                agent.percent = 0.0;
                agent.fragmentSection++;

                // passing a waypoint on route
                if (agent.fragmentSection == segment.route.size() - 1) {
                    agent.routeFragment++;
                    agent.fragmentSection = 0;
                    System.out.println("Passing a BEEG waypoint!");

                    // passing the final endpoint on route
                    if (agent.routeFragment == agent.route.segments.size()) {
                        System.out.println("Arrived at the destination! Shutting down.");
                        agent.doDelete();
                    }
                }
            } else {
                agent.percent += distance / CalculatePositionOnRouteBehaviour.distance(lastPoint.y(), nextPoint.y(), lastPoint.x(), nextPoint.x());
            }

            distance -= Math.min(distance, distanceLeft);
        }

        // save to database every second
        if (accumulatedDeltaTime >= 10.0) {
            // save position to database
            GeoPoint position = agent.getPosition();

            System.out.printf("I'm currently at: %f, %f%n", position.x(), position.y());

            accumulatedDeltaTime -= 10.0;
        }
    }

    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
