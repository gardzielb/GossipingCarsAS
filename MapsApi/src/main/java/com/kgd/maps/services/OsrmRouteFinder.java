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

    @Override
    public List<Route> findRoute(Point origin, Place destination) {
        return findRoute(origin, destination, new Point[]{}, "");
    }

    @Override
    public List<Route> findRoute(Point origin, Place destination, Point[] waypoints, String tag) {
        try {
            var request = requestBuilder
                    .includeSteps()
                    .origin(origin).destination(destination.location())
                    .waypoints(waypoints).build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            var objectMapper = new ObjectMapper();
            registerRouteDeserializer(objectMapper, destination.id(), tag);
            return List.of(objectMapper.readValue(response.body(), Route.class));
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerRouteDeserializer(ObjectMapper objectMapper, ObjectId destId, String routeTag) {
        var module = new SimpleModule();
        module.addDeserializer(Route.class, new OsrmRouteDeserializer(destId, routeTag));
        objectMapper.registerModule(module);
    }
}
