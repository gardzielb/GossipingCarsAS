package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stats(
        @JsonProperty("id") String id,
        @JsonProperty("distance") Double distance,
        @JsonProperty("cost") Double cost,
        @JsonProperty("time") Long time,
        @JsonProperty("arrived") Boolean arrived,
        @JsonProperty("dumb") Boolean dumb
) {}
