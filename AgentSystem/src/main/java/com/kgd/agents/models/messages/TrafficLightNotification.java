package com.kgd.agents.models.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgd.agents.trafficLigths.NotificationType;

public record TrafficLightNotification(
        @JsonProperty("type") NotificationType type,
        @JsonProperty("data") String data
) {}
