package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrafficLights(
        @JsonProperty("id") String id,
        @JsonProperty("location") GeoPoint location,
        @JsonProperty("routeTags") String[] routeTags
) {}
