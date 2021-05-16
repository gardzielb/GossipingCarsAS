package com.kgd.maps.api;

import com.kgd.maps.models.Route;
import com.kgd.maps.repositories.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteRepository routeRepository;

    public RouteController(@Autowired RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @GetMapping("/all")
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @GetMapping("/find")
    public List<Route> getRoute(@RequestParam double originLat, @RequestParam double originLng,
                                @RequestParam double destLat, @RequestParam double destLng) {
        return routeRepository.findByOriginEqualsAndDestinationEquals(
                new Point(originLng, originLat), new Point(destLng, destLat)
        );
    }
}
