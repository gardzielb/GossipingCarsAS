package com.kgd.maps.api;

import com.kgd.maps.models.TrafficLightSystem;
import com.kgd.maps.models.TrafficLights;
import com.kgd.maps.repositories.TrafficLightSystemRepository;
import com.kgd.maps.repositories.TrafficLightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lights")
public class TrafficLightsController {

    private final TrafficLightsRepository lightsRepository;
    private final TrafficLightSystemRepository lightSystemRepository;

    public TrafficLightsController(@Autowired TrafficLightsRepository lightsRepository,
                                   @Autowired TrafficLightSystemRepository lightSystemRepository) {
        this.lightsRepository = lightsRepository;
        this.lightSystemRepository = lightSystemRepository;
    }

    @GetMapping("/all")
    public List<TrafficLights> getAll() {
        return lightsRepository.findAll();
    }

    @GetMapping("/find/{routeTag}")
    public List<TrafficLights> getByRouteTag(@PathVariable String routeTag) {
        return lightsRepository.findAllByRouteTagsContains(routeTag);
    }

    @GetMapping("/systems/all")
    public List<TrafficLightSystem> getAllSystems() {
        return lightSystemRepository.findAll();
    }

    @PostMapping("/add")
    public TrafficLights addTrafficLights(@RequestBody TrafficLights trafficLights) {
        return lightsRepository.insert(trafficLights);
    }

    @PostMapping("/systems/add")
    public TrafficLightSystem addTrafficLightSystem(@RequestBody TrafficLightSystem lightSystem) {
        return lightSystemRepository.insert(lightSystem);
    }
}
