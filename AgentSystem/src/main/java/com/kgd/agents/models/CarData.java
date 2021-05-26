package com.kgd.agents.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CarData(
        @JsonProperty("id") String id,
        @JsonProperty("origin") GeoPoint origin,
        @JsonProperty("destinationId") String destinationId,
        @JsonProperty("velocity") double velocity
) {}
