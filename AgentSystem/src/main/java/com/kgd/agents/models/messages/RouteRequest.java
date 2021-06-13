package com.kgd.agents.models.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgd.agents.models.geodata.GeoPoint;

public record RouteRequest(
        @JsonProperty("origin") GeoPoint origin,
        @JsonProperty("destinationId") String destinationId,
        @JsonProperty("waypoints") GeoPoint[] waypoints,
        @JsonProperty("tag") String tag
) {}
