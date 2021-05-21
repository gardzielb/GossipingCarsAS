package com.kgd.agents.services;

import com.kgd.agents.models.GeoPoint;
import com.kgd.agents.models.Route;

import java.io.IOException;
import java.net.URISyntaxException;

public interface RouteService {

    Route findRoute(GeoPoint origin, String destId) throws URISyntaxException, IOException, InterruptedException;

    Route findRoute(GeoPoint origin, String destId, GeoPoint[] waypoints) throws IOException, InterruptedException;
}
