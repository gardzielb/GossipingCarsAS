package com.kgd.agents.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.AgentLocation;
import com.kgd.agents.models.geodata.Stats;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpStatsService implements StatsService {
    @Override
    public Stats upsert(Stats stats) {
        String urlBase = System.getenv("MAPS_API_URL");

        var urlBuilder = new StringBuilder(urlBase).append("/stats/upsert");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(stats);

            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), Stats.class);

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
