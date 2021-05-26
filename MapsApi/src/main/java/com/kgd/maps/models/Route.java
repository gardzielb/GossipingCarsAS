package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "routes")
public record Route(
        @JsonIgnore ObjectId id,
        @JsonProperty("origin") Point origin,
        @JsonProperty("destinationId") ObjectId destinationId,
        @JsonProperty("segments") ArrayList<RouteSegment> segments,
        @JsonProperty("distance") double distance
) {}
