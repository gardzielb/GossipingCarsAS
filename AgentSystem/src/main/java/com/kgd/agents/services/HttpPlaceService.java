package com.kgd.agents.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.PlaceType;
import com.kgd.agents.models.GeoPoint;
import com.kgd.agents.models.Place;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpPlaceService implements PlaceService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Place> findAllPlaces() throws IOException, InterruptedException {
        return findAllByType(null);
    }

    @Override
    public List<Place> findAllByType(PlaceType type) throws IOException, InterruptedException {
        String urlBase = System.getenv("MAPS_API_URL");

        var urlBuilder = new StringBuilder(urlBase)
                .append("/place/all");

        if (type != null) {
            urlBuilder.append("?type=").append(type.toUrlValue());
        }

        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .GET().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Place.class)
            );
        }
        catch (URISyntaxException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
            return null;
        }
    }

    @Override
    public List<Place> findNearbyByType(PlaceType type, GeoPoint location,
                                        double kmRadius) throws IOException, InterruptedException {
        String urlBase = System.getenv("MAPS_API_URL");
        var urlBuilder = new StringBuilder(urlBase)
                .append("/place/nearby")
                .append("?lat=").append(location.y()).append("&lng=").append(location.x())
                .append("&kms=").append(kmRadius);

        if (type != null) {
            urlBuilder.append("&type=").append(type.toUrlValue());
        }

        try {
            var request = HttpRequest
                    .newBuilder(new URI(urlBuilder.toString()))
                    .GET().build();
            var response = HttpClient
                    .newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Place.class)
            );
        }
        catch (URISyntaxException e) {
            System.out.println("Set 'MAPS_API_URL' env variable to valid URL");
            return null;
        }
    }
}
