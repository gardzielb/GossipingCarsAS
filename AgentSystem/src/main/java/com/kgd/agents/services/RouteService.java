package com.kgd.agents.services;

import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Route;

import java.io.IOException;
import java.net.URISyntaxException;

public interface RouteService {

    Route findRoute(GeoPoint origin, String destId) throws URISyntaxException, IOException, InterruptedException;

    Route findRoute(GeoPoint origin, String destId, GeoPoint[] waypoints) throws IOException, InterruptedException;

    Route findRoute(GeoPoint origin, String destId, String routeTag)
            throws URISyntaxException, IOException, InterruptedException;

    Route findRoute(GeoPoint origin, String destId, String routeTag, GeoPoint[] waypoints)
            throws IOException, InterruptedException;
}
