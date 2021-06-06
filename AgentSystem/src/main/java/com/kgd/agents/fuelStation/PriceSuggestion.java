package com.kgd.agents.fuelStation;

import com.kgd.agents.models.geodata.GeoPoint;
import jade.core.AID;

public record PriceSuggestion(AID stationAid, GeoPoint location, float price) {}
