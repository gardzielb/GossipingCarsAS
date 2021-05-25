package com.kgd.maps.services;

import com.google.maps.model.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RouteParserTests {
    @Test
    public void fromGoogleResponseShouldReturnEquivalentRoute() {
        var polyline = List.of(
                new LatLng(21, 37), new LatLng(69, 69),
                new LatLng(27, 31), new LatLng(69, 69),
                new LatLng(51, 21), new LatLng(53, 22)
        );

        var polyline1 = new EncodedPolyline(polyline.subList(0, 2));
        var polyline2 = new EncodedPolyline(polyline.subList(2, 4));
        var polyline3 = new EncodedPolyline(polyline.subList(4, 6));

        var step1 = new DirectionsStep();
        step1.polyline = polyline1;

        var step2 = new DirectionsStep();
        step2.polyline = polyline2;

        var step3 = new DirectionsStep();
        step3.polyline = polyline3;

        var leg1 = new DirectionsLeg();
        leg1.steps = new DirectionsStep[]{step1, step2};
        leg1.startLocation = polyline.get(0);
        leg1.endLocation = polyline.get(3);
        leg1.distance = new Distance();
        leg1.distance.inMeters = 1000;

        var leg2 = new DirectionsLeg();
        leg2.steps = new DirectionsStep[]{step3};
        leg2.startLocation = polyline.get(4);
        leg2.endLocation = polyline.get(5);
        leg2.distance = new Distance();
        leg2.distance.inMeters = 5000;

        var googleRoute = new DirectionsRoute();
        googleRoute.legs = new DirectionsLeg[]{leg1, leg2};

        var routeParser = new RouteParser();
        var destId = ObjectId.get();
        var route = routeParser.fromGoogleResponse(googleRoute, destId);

        Assertions.assertEquals(googleRoute.legs.length, route.segments().size());

        Assertions.assertEquals(googleRoute.legs[0].startLocation.lat, route.origin().getY());
        Assertions.assertEquals(googleRoute.legs[0].startLocation.lng, route.origin().getX());
        Assertions.assertEquals(destId, route.destinationId());
        Assertions.assertEquals(leg1.distance.inMeters + leg2.distance.inMeters, route.distance());

        var seg1 = new EncodedPolyline(route.segments().get(0).encodedPolyline());
        Assertions.assertEquals(new EncodedPolyline(polyline.subList(0, 4)).getEncodedPath(), seg1.getEncodedPath());

        var seg2 = new EncodedPolyline(route.segments().get(1).encodedPolyline());
        Assertions.assertEquals(polyline3.getEncodedPath(), seg2.getEncodedPath());
    }
}
