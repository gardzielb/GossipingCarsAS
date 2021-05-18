package com.kgd.agents.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RouteRequest(
        @JsonProperty("origin") GeoPoint origin,
        @JsonProperty("destinationId") String destinationId,
        @JsonProperty("waypoints") GeoPoint[] waypoints
) {}
