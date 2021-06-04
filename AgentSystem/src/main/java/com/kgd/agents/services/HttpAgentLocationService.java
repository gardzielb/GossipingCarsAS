package com.kgd.agents.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.AgentLocation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HttpAgentLocationService implements AgentLocationService{
    @Override
    public AgentLocation getAgentLocationByAID(String AID) throws IOException, InterruptedException {
        String urlBase = System.getenv("MAPS_API_URL");

        var urlBuilder = new StringBuilder(urlBase)
                .append("/agent_location/find?aid=")
                .append(URLEncoder.encode(AID, StandardCharsets.UTF_8));

        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .GET().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return (new ObjectMapper()).readValue(response.body(), AgentLocation.class);
        }
        catch (URISyntaxException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
            return null;
        }
    }

    @Override
    public AgentLocation addOrUpdateAgentLocation(AgentLocation location) {
        String urlBase = System.getenv("MAPS_API_URL");

        var urlBuilder = new StringBuilder(urlBase).append("/agent_location/add");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(location);

            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), AgentLocation.class);

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteAgentLocationByAID(String AID) {
        String urlBase = System.getenv("MAPS_API_URL");

        var urlBuilder = new StringBuilder(urlBase)
                .append("/agent_location/find?aid=")
                .append(URLEncoder.encode(AID, StandardCharsets.UTF_8));

        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .DELETE().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
        }
    }
}
