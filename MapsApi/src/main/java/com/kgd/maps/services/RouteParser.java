package com.kgd.maps.services;

import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.kgd.maps.models.Route;
import com.kgd.maps.models.RouteSegment;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;

import java.util.ArrayList;

public class RouteParser {

    public Route fromGoogleResponse(DirectionsRoute route, ObjectId destinationId) {
        var segments = new ArrayList<RouteSegment>();
        double distance = 0.0;

        for (var leg : route.legs) {
            var polyline = new ArrayList<LatLng>();
            for (var step : leg.steps) {
                polyline.addAll(step.polyline.decodePath());
            }
            var segment = new RouteSegment(
                    new Point(leg.startLocation.lng, leg.startLocation.lat),
                    new Point(leg.endLocation.lng, leg.endLocation.lat),
                    new EncodedPolyline(polyline).getEncodedPath()
            );
            segments.add(segment);
            distance += leg.distance.inMeters;
        }

        return new Route(ObjectId.get(), segments.get(0).origin(), destinationId, segments, distance);
    }
}
