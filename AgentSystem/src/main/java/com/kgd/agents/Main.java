package com.kgd.agents;

import com.google.maps.model.PlaceType;
import com.kgd.agents.models.geodata.Vec2;
import com.kgd.agents.services.HttpPlaceService;
import com.kgd.agents.services.HttpTrafficLightsService;
import com.kgd.agents.services.PlaceService;
import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import com.kgd.agents.trafficLigths.TrafficLightsManagerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args)
            throws StaleProxyException, URISyntaxException, IOException, InterruptedException {
        jade.Boot.main(new String[]{"-gui", "factory:com.kgd.agents.carManufacturer.CarManufacturerAgent"});
        Runtime runtime = Runtime.instance();

        createTrafficLights(runtime);

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

    private static void createTrafficLights(Runtime jadeRuntime)
            throws IOException, InterruptedException, StaleProxyException {
        var profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, "TrafficLights");
        var tlContainer = jadeRuntime.createAgentContainer(profile);

        var tlService = new HttpTrafficLightsService();

        for (var tl : tlService.findAll()) {
            var managerAgent = tlContainer.createNewAgent(
                    tl.id() + "_manager", TrafficLightsManagerAgent.class.getName(), new Object[]{tl}
            );
            managerAgent.start();

            var isGreenMap = new HashMap<Vec2, Boolean>();
            for (Vec2 dir : tl.approachDirections()) {
                isGreenMap.put(dir, false);
            }

            var signalerAgent = tlContainer.createNewAgent(
                    tl.id() + "_signaler", TrafficLightSignalerAgent.class.getName(), new Object[]{tl, isGreenMap}
            );
            signalerAgent.start();
        }
    }
}
