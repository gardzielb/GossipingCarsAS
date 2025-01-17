package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CarData(
        @JsonProperty("id") String id,
        @JsonProperty("origin") GeoPoint origin,
        @JsonProperty("destinationId") String destinationId,
        @JsonProperty("velocity") double velocity,
        @JsonProperty("routeTag") String routeTag,
        @JsonProperty("dumb") boolean dumb,
        @JsonProperty("simulationSpeed") double simulationSpeed
) {}
