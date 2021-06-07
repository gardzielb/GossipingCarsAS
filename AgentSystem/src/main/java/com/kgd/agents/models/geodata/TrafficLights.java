package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrafficLights(
        @JsonProperty("id") String id,
        @JsonProperty("location") GeoPoint location,
        @JsonProperty("approachDirections") Vec2[] approachDirections,
        @JsonProperty("routeTags") String[] routeTags
) {}
