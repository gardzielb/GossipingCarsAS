package com.kgd.mapsapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "routes")
public record Route(
        @JsonProperty("id") ObjectId id,
        @JsonProperty("origin") Point origin,
        @JsonProperty("destination") Point destination,
        @JsonProperty("segments") RouteSegment[] segments
) {
}
