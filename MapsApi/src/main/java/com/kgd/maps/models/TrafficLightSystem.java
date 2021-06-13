package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trafficLightSystems")
public record TrafficLightSystem(
        @JsonProperty("id") ObjectId id,
        @JsonProperty("physicalLights") ObjectId[] physicalLights
) {}
