package com.kgd.maps.api;

import com.kgd.maps.models.TrafficLights;
import com.kgd.maps.repositories.TrafficLightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lights")
public class TrafficLightsController {

	private final TrafficLightsRepository lightsRepository;

	public TrafficLightsController(@Autowired TrafficLightsRepository lightsRepository) {
		this.lightsRepository = lightsRepository;
	}

	@GetMapping("/all")
	public List<TrafficLights> getAll() {
		return lightsRepository.findAll();
	}

	@GetMapping("/find/{routeTag}")
	public List<TrafficLights> getByRouteTag(@PathVariable String routeTag) {
		return lightsRepository.findAllByRouteTag(routeTag);
	}

	@PostMapping("/add")
	public TrafficLights addTrafficLights(@RequestBody TrafficLights trafficLights) {
		return lightsRepository.insert(trafficLights);
	}
}
