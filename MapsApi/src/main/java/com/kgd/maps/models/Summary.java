package com.kgd.maps.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Summary(
        @JsonProperty() double avgCostDumb,
        @JsonProperty() double avgDistanceDumb,
        @JsonProperty() double avgTimeDumb,
        @JsonProperty() double avgCostSmart,
        @JsonProperty() double avgDistanceSmart,
        @JsonProperty() double avgTimeSmart
) { }
