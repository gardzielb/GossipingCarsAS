package com.kgd.agents.models;

import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;

public class DecodedRouteSegment {
    public GeoPoint origin;
    public GeoPoint destination;
    public ArrayList<GeoPoint> route = new ArrayList<>();

    public DecodedRouteSegment(RouteSegment segment) {
        origin = segment.origin();
        destination = segment.destination();

        EncodedPolyline polyline = new EncodedPolyline(segment.encodedPolyline());
        var decodedPolyline = polyline.decodePath();

        for (var point: decodedPolyline) {
            route.add(new GeoPoint(point.lng, point.lat));
        }
    }
}
