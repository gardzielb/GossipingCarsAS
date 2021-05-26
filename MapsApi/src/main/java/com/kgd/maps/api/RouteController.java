package com.kgd.maps.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kgd.maps.models.Route;
import com.kgd.maps.models.RouteRequest;
import com.kgd.maps.serialization.ObjectIdDeserializer;
import com.kgd.maps.serialization.PointDeserializer;
import com.kgd.maps.services.CachingRouteService;
import com.kgd.maps.services.RouteService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(@Autowired CachingRouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/all")
    public List<Route> getAllRoutes() {
        return routeService.findAll();
    }

    @GetMapping("/find")
    public ResponseEntity<Route> getRoute(@RequestParam("routeRequest") String encodedRouteRequest) {
        var routeRequestJson = URLDecoder.decode(encodedRouteRequest, StandardCharsets.UTF_8);

        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(Point.class, new PointDeserializer());
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        objectMapper.registerModule(module);

        try {
            var routeRequest = objectMapper.readValue(routeRequestJson, RouteRequest.class);
            var route = routeService.findRoute(
                    routeRequest.origin(), routeRequest.destinationId(), routeRequest.waypoints()
            );
            return ResponseEntity.ok(route);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
    }
}
