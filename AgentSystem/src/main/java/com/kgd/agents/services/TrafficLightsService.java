package com.kgd.agents.services;

import com.kgd.agents.models.geodata.TrafficLightSystem;
import com.kgd.agents.models.geodata.TrafficLights;

import java.io.IOException;
import java.util.List;

public interface TrafficLightsService {
    List<TrafficLights> findAllByRouteTag(String tag) throws IOException, InterruptedException;

    List<TrafficLightSystem> findAllSystems() throws IOException, InterruptedException;

    TrafficLights updateTrafficLights(TrafficLights trafficLights) throws IOException, InterruptedException;
}
