package com.kgd.agents.carManufacturer.behaviors;

import com.kgd.agents.carManufacturer.CarManufacturerAgent;
import com.kgd.agents.driver.DriverAgent;
import com.kgd.agents.fuelStation.FuelCarControllerAgent;
import com.kgd.agents.navigator.RouteNavigatorAgent;
import com.kgd.agents.services.CarDataService;
import com.kgd.agents.services.HttpCarDataService;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import com.kgd.agents.walletController.WalletController;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.Arrays;

public class CreateNewCarAgentsBehaviour extends TickerBehaviour {
    private static int carNumber = 0;
    private final CarDataService carDataService = new HttpCarDataService();

    public CreateNewCarAgentsBehaviour(CarManufacturerAgent agent, long period) {
        super(agent, period);
    }

    @Override
    protected void onTick() {
        var requests = carDataService.getAll();

        for (var request: requests) {
            Object[] args = new Object[] {
                    Double.toString(request.origin().x()),
                    Double.toString(request.origin().y()),
                    request.destinationId(),
                    Double.toString(request.velocity())
            };
            Object[] routeNavArgs = Arrays.copyOf(args,3);
            String name = "Car_" + carNumber;

            // create a sub-container
            jade.core.Runtime runtime = jade.core.Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, name + "_container");
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            ContainerController container = runtime.createAgentContainer(profile);

            // create agents inside container
            try {
                // fuel controller has no conflicts with other agents, can be created first
                AgentController fuel = container.createNewAgent(name + "_fuel_controller", FuelCarControllerAgent.class.getName(), null);
                fuel.start();

                // cost controller has no conflicts with other agents either
                AgentController cost = container.createNewAgent(name + "_cost_controller", WalletController.class.getName(), null);
                cost.start();

                // no conflicts as well
                AgentController trafficLights = container.createNewAgent(
                        name + "_TL_controller", TrafficLightsCarControllerAgent.class.getName(),
                        new Object[]{Double.toString(request.velocity()), 7}
                );
                trafficLights.start();

                // navigator before driver (driver will try to query navigator for route on setup)
                AgentController nav = container.createNewAgent(name + "_route_navigator", RouteNavigatorAgent.class.getName(), routeNavArgs);
                nav.start();

                AgentController driver = container.createNewAgent(name, DriverAgent.class.getName(), args);
                driver.start();

                nextCar();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }

            carDataService.deleteById(request.id());
        }
    }

    private static void nextCar() { carNumber++; }
}
