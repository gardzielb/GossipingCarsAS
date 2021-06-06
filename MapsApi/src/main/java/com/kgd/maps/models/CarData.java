package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "car_data")
public record CarData(
        @JsonProperty("id") ObjectId id,
        @JsonProperty("origin") Point origin,
        @JsonProperty("destinationId") ObjectId destinationId,
        @JsonProperty("velocity") double velocity,
        @JsonProperty("dumb") boolean dumb
) {}
