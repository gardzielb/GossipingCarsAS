package com.kgd.maps.services;

import com.kgd.maps.models.Route;
import com.kgd.maps.repositories.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;

public class CachingRouteService implements RouteService {

    private final RouteRepository routeRepository;

    public CachingRouteService(@Autowired RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Route findRoute(Point origin, Point destination) {
        var cachedRoutes = routeRepository.findByOriginEqualsAndDestinationEquals(origin, destination);
        if (!cachedRoutes.isEmpty())
            return cachedRoutes.get(0);

        var routeFinder = new RouteFinder(GeoApiContextProvider.getApiContext());
        var routes = routeFinder.findRoute(origin, destination);
        routeRepository.insert(routes); // cache new routes in DB

        return routes.get(0); // may be some kind of sorting in the future
    }
}
