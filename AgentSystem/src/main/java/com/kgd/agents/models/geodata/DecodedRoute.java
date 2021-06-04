package com.kgd.agents.models.geodata;

import java.util.ArrayList;

public class DecodedRoute {
    public GeoPoint origin;
    public String destinationId;
    public ArrayList<DecodedRouteSegment> segments = new ArrayList<>();

    public DecodedRoute(Route route) {
        origin = route.origin();
        destinationId = route.destinationId();

        for (var segment: route.segments()) {
            segments.add(new DecodedRouteSegment(segment));
        }
    }
}
