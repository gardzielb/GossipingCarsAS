package com.kgd.agents.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record Route(
        @JsonProperty("origin") GeoPoint origin,
        @JsonProperty("destinationId") String destinationId,
        @JsonProperty("segments") ArrayList<RouteSegment> segments
) {}
