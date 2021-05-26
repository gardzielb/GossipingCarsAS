package com.kgd.agents.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.CarData;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class HttpCarDataService implements CarDataService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<CarData> getAll() {
        String urlBase = System.getenv("MAPS_API_URL");

        var urlBuilder = new StringBuilder(urlBase)
                .append("/car_requests/all");

        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .GET().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.stream(objectMapper.readValue(response.body(), CarData[].class)).toList();
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
            return null;
        }
    }

    @Override
    public void deleteAll() {
        String urlBase = System.getenv("MAPS_API_URL");

        var urlBuilder = new StringBuilder(urlBase)
                .append("/car_requests/all");

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
