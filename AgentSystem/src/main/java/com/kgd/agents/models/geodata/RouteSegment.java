package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RouteSegment(
        @JsonProperty("origin") GeoPoint origin,
        @JsonProperty("destination") GeoPoint destination,
        @JsonProperty("polyline") String encodedPolyline
) {}
