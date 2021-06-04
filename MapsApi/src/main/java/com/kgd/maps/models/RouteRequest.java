package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;

public record RouteRequest(
        @JsonProperty("origin") Point origin,
        @JsonProperty("destinationId") ObjectId destinationId,
        @JsonProperty("waypoints") Point[] waypoints,
		@JsonProperty("tag") String tag
) {}
