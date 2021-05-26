package com.kgd.agents.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CarLocationData(
        @JsonProperty("position") GeoPoint position,
        @JsonProperty("destinationId") String destinationId
) {}
