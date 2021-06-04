package com.kgd.maps.services;

import com.kgd.maps.models.Route;
import com.kgd.maps.repositories.PlaceRepository;
import com.kgd.maps.repositories.RouteRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CachingRouteService implements RouteService {

    private final RouteRepository routeRepository;
    private final PlaceRepository placeRepository;
    private final RouteFinder routeFinder;

    public CachingRouteService(@Autowired RouteRepository routeRepository,
                               @Autowired PlaceRepository placeRepository,
                               @Autowired OsrmRouteFinder routeFinder) {
        this.routeRepository = routeRepository;
        this.placeRepository = placeRepository;
        this.routeFinder = routeFinder;
    }

    @Override
    public Route findRoute(Point origin, ObjectId destinationId, Point[] waypoints, String tag) {
        if (waypoints.length == 0) {
            var cachedRoutes = routeRepository.findByOriginNearAndDestinationIdEquals(
                    origin, new Distance(0.1, Metrics.KILOMETERS), destinationId
            );
            if (!cachedRoutes.isEmpty()) {
                System.out.println("Returning cached route");
                return cachedRoutes.get(0);
            }
        }

        var destination = placeRepository.findById(destinationId);
        if (destination.isEmpty())
            throw new IllegalArgumentException("Destination place does not exist");

        var routes = routeFinder.findRoute(origin, destination.get(), waypoints, tag);

        if (waypoints.length == 0)
            routeRepository.insert(routes); // cache new routes in DB

        return routes.get(0); // may be some kind of sorting in the future
    }

    @Override
    public List<Route> findAll() {
        return routeRepository.findAll();
    }
}
