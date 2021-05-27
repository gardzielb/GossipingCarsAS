package com.kgd.agents.fuelStation;

import java.util.ArrayList;
import java.util.List;

public class OptimalFuelPriceCalculator {
    public static List<PriceSuggestion> calculateOptimalFuelPricesAsPriceSuggestion(List<FuelStationData> stations) {
        return calculateOptimalFuelPrices(stations).stream().map(
                station -> new PriceSuggestion(station.stationId(), (float) station.fuelPrice())
        ).toList();
    }

    public static List<FuelStationData> calculateOptimalFuelPrices(List<FuelStationData> stations) {
        List<FuelStationData> result = new ArrayList<>();

        var o_min = Double.MAX_VALUE;

        for (var station: stations) {
            var price = station.fuelPrice();
            var dist = station.routeDistance();

            var o = price * Math.pow(dist, 0.25);
            if (o < o_min) o_min = o;
        }

        for (var station: stations) {
            var price = o_min / Math.pow(station.routeDistance(), 0.25);
            result.add(new FuelStationData(station.stationId(), (float) Math.min(price, station.fuelPrice()), station.routeDistance()));
        }

        return result;
    }
}
