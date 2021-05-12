package com.kgd.mapsapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.EncodedPolyline;
import org.springframework.data.geo.Point;

public record RouteSegment(
        @JsonProperty("origin") Point origin,
        @JsonProperty("destination") Point destination,
        @JsonProperty("polyline") EncodedPolyline polyline
) {
}
