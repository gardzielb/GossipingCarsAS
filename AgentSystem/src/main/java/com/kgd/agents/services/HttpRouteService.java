package com.kgd.agents.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Route;
import com.kgd.agents.models.messages.RouteRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HttpRouteService implements RouteService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Route findRoute(GeoPoint origin, String destId) throws IOException, InterruptedException {
        return findRoute(origin, destId, new GeoPoint[]{});
    }

    @Override
    public Route findRoute(GeoPoint origin, String destId, GeoPoint[] waypoints)
            throws IOException, InterruptedException {
        return findRoute(origin, destId, null, waypoints);
    }

    @Override
    public Route findRoute(GeoPoint origin, String destId, String routeTag)
            throws URISyntaxException, IOException, InterruptedException {
        return findRoute(origin, destId, routeTag, new GeoPoint[]{});
    }

    @Override
    public Route findRoute(GeoPoint origin, String destId, String routeTag, GeoPoint[] waypoints)
            throws IOException, InterruptedException {
        String urlBase = System.getenv("MAPS_API_URL");

        var routeRequest = new RouteRequest(origin, destId, waypoints, routeTag);
        String encodedRouteRequest = URLEncoder.encode(
                objectMapper.writeValueAsString(routeRequest), StandardCharsets.UTF_8
        );

        var urlBuilder = new StringBuilder(urlBase)
                .append("/route/find?routeRequest=")
                .append(encodedRouteRequest);

        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .GET().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), Route.class);
        }
        catch (URISyntaxException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
            return null;
        }
    }
}
