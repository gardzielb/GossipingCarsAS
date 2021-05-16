package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "routes")
public record Route(
        @JsonProperty("id") ObjectId id,
        @JsonProperty("origin") Point origin,
        @JsonProperty("destination") Point destination,
        @JsonProperty("segments") ArrayList<RouteSegment> segments
) {}
