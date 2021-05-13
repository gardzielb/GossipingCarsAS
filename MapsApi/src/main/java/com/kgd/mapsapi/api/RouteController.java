package com.kgd.mapsapi.api;

import com.kgd.mapsapi.models.Route;
import com.kgd.mapsapi.repositories.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteRepository repository;

    public RouteController(@Autowired RouteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/all")
    public List<Route> getAll() {
        return repository.findAll();
    }

    @GetMapping("/near")
    public List<Route> getNear() {
        return repository.findByOriginEqualsAndDestinationEquals(new Point(50, 21), new Point(51, 22));
    }

    @PostMapping("/add")
    public Route add(@RequestBody Route route) {
        return repository.insert(route);
    }
}
