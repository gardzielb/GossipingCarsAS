package com.kgd.maps.services;

import com.kgd.maps.models.Route;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteService {
    Route findRoute(Point origin, ObjectId destinationId, Point[] waypoints, String tag);

    List<Route> findAll();
}
