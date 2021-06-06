package com.kgd.agents.fuelStation;

import com.kgd.agents.models.geodata.GeoPoint;
import jade.core.AID;

public record FuelStationData(
        AID stationId,
        GeoPoint location,
        double fuelPrice,
        double routeDistance
) {}
