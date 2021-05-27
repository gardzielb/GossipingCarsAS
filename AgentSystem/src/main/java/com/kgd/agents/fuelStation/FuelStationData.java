package com.kgd.agents.fuelStation;

import jade.core.AID;

public record FuelStationData(
        AID stationId,
        double fuelPrice,
        double routeDistance
) {}
