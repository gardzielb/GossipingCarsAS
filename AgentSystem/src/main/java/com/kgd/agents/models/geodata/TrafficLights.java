package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrafficLights(
        @JsonProperty("id") String id,
        @JsonProperty("location") GeoPoint location,
        @JsonProperty("approachDirections") GeoPoint[] approachDirections,
        @JsonProperty("routeTags") String[] routeTags
) {}
