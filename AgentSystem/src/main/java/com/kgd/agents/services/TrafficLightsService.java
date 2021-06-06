package com.kgd.agents.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kgd.agents.models.geodata.TrafficLights;

import java.io.IOException;
import java.util.List;

public interface TrafficLightsService {
    List<TrafficLights> findAllByRouteTag(String tag) throws IOException, InterruptedException;
}
