package com.kgd.gossipingcars.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.PlaceType;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "places" )
public record Place(
		@JsonProperty( "id" ) ObjectId id,
		@JsonProperty( "name" ) String name,
		@JsonProperty( "type" ) PlaceType type,
		@JsonProperty( "location" ) Point location,
		@JsonProperty( "address" ) String address,
		@JsonProperty( "rating" ) double rating
) {}
