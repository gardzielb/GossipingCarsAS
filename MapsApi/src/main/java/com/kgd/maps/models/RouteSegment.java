package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.geo.Point;

public record RouteSegment(
        @JsonProperty("origin") Point origin,
        @JsonProperty("destination") Point destination,
        @JsonProperty("polyline") String encodedPolyline
) {}
