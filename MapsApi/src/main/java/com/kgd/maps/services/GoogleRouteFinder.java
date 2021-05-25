package com.kgd.maps.services;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.kgd.maps.models.Place;
import com.kgd.maps.models.Route;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleRouteFinder implements RouteFinder {

    private final GeoApiContext apiContext = GeoApiContextProvider.getApiContext();
    private final RouteParser routeParser = new RouteParser();

    @Override
    public List<Route> findRoute(Point origin, Place destination) {
        return findRoute(origin, destination, new Point[]{});
    }

    @Override
    public List<Route> findRoute(Point origin, Place destination, Point[] waypoints) {
        System.out.println("Asking google for route...");

        DirectionsApiRequest request = new DirectionsApiRequest(apiContext)
                .origin(new LatLng(origin.getY(), origin.getX()))
                .destination(new LatLng(destination.location().getY(), destination.location().getX()));

        if (waypoints.length > 0) {
            var latLngArray = new LatLng[waypoints.length];
            for (int i = 0; i < waypoints.length; i++) {
                latLngArray[i] = new LatLng(waypoints[i].getY(), waypoints[i].getX());
            }
            request.waypoints(latLngArray);
        }

        try {
            return Arrays.stream(request.await().routes)
                         .map(route -> routeParser.fromGoogleResponse(route, destination.id()))
                         .collect(Collectors.toList());
        }
        catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
