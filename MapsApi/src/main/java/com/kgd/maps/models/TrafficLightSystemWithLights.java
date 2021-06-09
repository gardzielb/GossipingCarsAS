package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

public record TrafficLightSystemWithLights(
        @JsonProperty("id") ObjectId id,
        @JsonProperty("physicalLights") List<TrafficLights> physicalLights
) {}
