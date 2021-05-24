package com.kgd.maps.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "agentLocations")
public record AgentLocation(
        @JsonProperty("id") ObjectId id,
        @JsonProperty("AID") String AID,
        @JsonProperty("location") Point location
) {}