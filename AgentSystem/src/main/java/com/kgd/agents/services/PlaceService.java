package com.kgd.agents.services;

import com.google.maps.model.PlaceType;
import com.kgd.agents.models.Place;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface PlaceService {
    List<Place> findAllPlaces() throws URISyntaxException, IOException, InterruptedException;

    List<Place> findAllByType(PlaceType type) throws URISyntaxException, IOException, InterruptedException;
}
