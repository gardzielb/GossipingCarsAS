package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stats")
public class Stats {
        @JsonProperty("id")
        public String id;
        @JsonProperty("distance")
        public Double distance;
        @JsonProperty("cost")
        public Double cost;
        @JsonProperty("time")
        public Double time;
        @JsonProperty("dumb")
        public Boolean dumb;
        @JsonProperty("arrived")
        public Boolean arrived;
}
