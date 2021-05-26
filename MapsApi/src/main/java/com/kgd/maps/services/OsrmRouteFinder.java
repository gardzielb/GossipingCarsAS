package com.kgd.maps.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kgd.maps.models.Place;
import com.kgd.maps.models.Route;
import com.kgd.maps.serialization.OsrmRouteDeserializer;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class OsrmRouteFinder implements RouteFinder {

    private final OsrmRequestBuilder requestBuilder = new OsrmRequestBuilder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Route> findRoute(Point origin, Place destination) {
        return findRoute(origin, destination, new Point[]{});
    }

    @Override
    public List<Route> findRoute(Point origin, Place destination, Point[] waypoints) {
        try {
            var request = requestBuilder
                    .includeSteps()
                    .origin(origin).destination(destination.location())
                    .waypoints(waypoints).build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

            registerRouteDeserializer(destination.id());
            return List.of(objectMapper.readValue(response.body(), Route.class));
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerRouteDeserializer(ObjectId destId) {
        var module = new SimpleModule();
        module.addDeserializer(Route.class, new OsrmRouteDeserializer(destId));
        objectMapper.registerModule(module);
    }
}