package com.kgd.agents;

import com.google.maps.model.PlaceType;
import com.kgd.agents.carManufacturer.CarManufacturerAgent;
import com.kgd.agents.services.HttpPlaceService;
import com.kgd.agents.services.PlaceService;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args)
            throws StaleProxyException, URISyntaxException, IOException, InterruptedException {
        jade.Boot.main(new String[]{"-gui", "factory:com.kgd.agents.carManufacturer.CarManufacturerAgent"});
        Runtime runtime = Runtime.instance();

        PlaceService placeService = new HttpPlaceService();
        var stations = placeService.findAllByType(PlaceType.GAS_STATION);

        Profile p = new ProfileImpl();
        p.setParameter(Profile.CONTAINER_NAME, "FuelStations");
        AgentContainer container = runtime.createAgentContainer(p);

        for (var station : stations) {
            AgentController agent = container.createNewAgent(
                    station.id(),
                    "com.kgd.agents.fuelStation.FuelStationManagerAgent",
                    new Object[]{
                            station.location(),
                            station.id()
                    }
            );
            agent.start();
        }
    }
}
