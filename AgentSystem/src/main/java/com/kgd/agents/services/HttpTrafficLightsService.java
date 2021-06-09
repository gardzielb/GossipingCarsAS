package com.kgd.agents.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.TrafficLightSystem;
import com.kgd.agents.models.geodata.TrafficLights;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpTrafficLightsService implements TrafficLightsService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String urlBase = System.getenv("MAPS_API_URL");

    @Override
    public List<TrafficLights> findAllByRouteTag(String tag) throws IOException, InterruptedException {
        var urlBuilder = new StringBuilder(urlBase).append("/lights/find/").append(tag);

        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .GET().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TrafficLights.class)
            );
        }
        catch (URISyntaxException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
            return null;
        }
    }

    @Override
    public List<TrafficLightSystem> findAllSystems() throws IOException, InterruptedException {
        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBase + "/lights/systems/all"))
                    .GET().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TrafficLightSystem.class)
            );
        }
        catch (URISyntaxException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
            return null;
        }
    }
}
