package com.kgd.agents.models.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgd.agents.models.geodata.GeoPoint;

public record CarLocationData(
        @JsonProperty("position") GeoPoint position,
        @JsonProperty("destinationId") String destinationId
) {}
