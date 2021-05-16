package com.kgd.maps.services;

import com.kgd.maps.models.Route;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

@Service
public interface RouteService {
    Route findRoute(Point origin, Point destination);
}
