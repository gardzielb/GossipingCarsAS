package com.kgd.maps.services;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.kgd.maps.models.Route;
import org.springframework.data.geo.Point;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RouteFinder {

    private final GeoApiContext apiContext;
    private final RouteParser routeParser = new RouteParser();

    public RouteFinder(GeoApiContext apiContext) {
        this.apiContext = apiContext;
    }

    public List<Route> findRoute(Point origin, Point destination) {
        DirectionsApiRequest request = new DirectionsApiRequest(apiContext)
                .origin(new LatLng(origin.getY(), origin.getX()))
                .destination(new LatLng(origin.getY(), origin.getX()));

        try {
            return Arrays.stream(request.await().routes)
                         .map(routeParser::fromGoogleResponse).collect(Collectors.toList());
        }
        catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
