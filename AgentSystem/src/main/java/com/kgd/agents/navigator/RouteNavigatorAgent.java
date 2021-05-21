package com.kgd.agents.navigator;

import com.kgd.agents.models.GeoPoint;
import com.kgd.agents.models.Route;
import com.kgd.agents.navigator.behaviors.HandleNewWaypointRequestBehavior;
import com.kgd.agents.navigator.behaviors.HandleRouteQueryBehavior;
import com.kgd.agents.services.HttpRouteService;
import com.kgd.agents.services.RouteService;
import jade.core.Agent;

import java.io.IOException;
import java.net.URISyntaxException;

public class RouteNavigatorAgent extends Agent {

    private final RouteService routeService = new HttpRouteService();
    private Route currentRoute;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        if (args == null || args.length < 3)
            throw new IllegalStateException("Expected origin and destination as arguments");

        var origin = new GeoPoint(Double.parseDouble((String) args[0]), Double.parseDouble((String) args[1]));
        var destinationId = (String) args[2];

        try {
            currentRoute = routeService.findRoute(origin, destinationId);
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to find route");
        }

        addBehaviour(new HandleNewWaypointRequestBehavior(this));
        addBehaviour(new HandleRouteQueryBehavior(this));
    }

    public void addWaypoints(GeoPoint[] waypoints) {
        try {
            currentRoute = routeService.findRoute(currentRoute.origin(), currentRoute.destinationId(), waypoints);
            System.out.println("Successfully changed route to " + currentRoute);
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }
}
