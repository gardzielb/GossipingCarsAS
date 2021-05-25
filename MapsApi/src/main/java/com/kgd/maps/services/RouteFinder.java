package com.kgd.maps.services;

import com.kgd.maps.models.Place;
import com.kgd.maps.models.Route;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteFinder {
    List<Route> findRoute(Point origin, Place destination);

    List<Route> findRoute(Point origin, Place destination, Point[] waypoints);
}