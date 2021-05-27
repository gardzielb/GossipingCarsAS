package com.kgd.maps.api;

import com.google.maps.model.PlaceType;
import com.kgd.maps.models.Place;
import com.kgd.maps.repositories.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/place")
public class PlaceController {

    private final PlaceRepository repository;

    public PlaceController(@Autowired PlaceRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/all")
    public List<Place> getAllPlaces(@RequestParam(required = false) String type) {
        if (type != null) {
            return repository.findByType(type.toUpperCase(Locale.ROOT));
        } else {
            return repository.findAll();
        }
    }

    @GetMapping("/nearby")
    public List<Place> getNearbyPlaces(@RequestParam double lat, @RequestParam double lng, @RequestParam double kms) {
        return repository.findByLocationNear(new Point(lng, lat), new Distance(kms, Metrics.KILOMETERS));
    }

    @PostMapping("/add")
    public Place addPlace(@RequestBody Place place) {
        return repository.insert(place);
    }
}
