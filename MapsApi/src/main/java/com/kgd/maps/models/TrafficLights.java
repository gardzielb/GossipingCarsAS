package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trafficLights")
public record TrafficLights(
		@JsonProperty("id") ObjectId id,
		@JsonProperty("location") Point location,
		@JsonProperty("routeTags") String[] routeTags
) {}
