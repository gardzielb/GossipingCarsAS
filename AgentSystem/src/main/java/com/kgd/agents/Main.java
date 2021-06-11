package com.kgd.agents;

import com.google.maps.model.PlaceType;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.TrafficLights;
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

        for (var tlSystem : tlService.findAllSystems()) {
            String managerName = tlSystem.id() + "_manager";
            var managerAgent = tlContainer.createNewAgent(
                    managerName, TrafficLightsManagerAgent.class.getName(),
                    new Object[]{tlSystem.physicalLights()[0].id(), tlSystem.physicalLights()[1].id(), 7}
            );
            managerAgent.start();

            startTLSignaler(
                    tlContainer, tlSystem.physicalLights()[0], managerName, tlSystem.physicalLights()[1].location()
            );
            startTLSignaler(
                    tlContainer, tlSystem.physicalLights()[1], managerName, tlSystem.physicalLights()[0].location()
            );
        }
    }

    private static void startTLSignaler(AgentContainer container, TrafficLights lights, String managerName,
                                        GeoPoint exitPoint) throws StaleProxyException {
        var signaler = container.createNewAgent(
                lights.id(), TrafficLightSignalerAgent.class.getName(),
                new Object[]{lights, exitPoint, managerName}
        );
        signaler.start();

    }
}
