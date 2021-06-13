package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.services.HttpTrafficLightsService;
import com.kgd.agents.services.TrafficLightsService;
import com.kgd.agents.trafficLigths.signalerBehaviors.HandleChangeLightColorRequestsBehavior;
import com.kgd.agents.trafficLigths.signalerBehaviors.SignalTrafficLightColorBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.io.IOException;

public class TrafficLightSignalerAgent extends Agent {

    private TrafficLights trafficLights;
    private final TrafficLightsService tlService = new HttpTrafficLightsService();

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 3)
            throw new RuntimeException("Traffic lights object is needed by Signaler agent");

        trafficLights = (TrafficLights) args[0];
        var exitPoint = (GeoPoint) args[1];
        var exitControllerName = args[2].toString();

        registerInDF();

        addBehaviour(new SignalTrafficLightColorBehavior(this, exitPoint, exitControllerName));
        addBehaviour(new HandleChangeLightColorRequestsBehavior(this));
    }

    public boolean isGreen() {
        return trafficLights.isGreen();
    }

    public void changeLight() {
        updateLights(!trafficLights.isGreen());
    }

    public void setLightGreen(boolean isGreen) {
        updateLights(isGreen);
    }

    private void updateLights(boolean isGreen) {
        try {
            var updatedTL = new TrafficLights(
                    trafficLights.id(), trafficLights.location(), trafficLights.routeTags(), isGreen
            );
            trafficLights = tlService.updateTrafficLights(updatedTL);
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void registerInDF() {
        var agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());
        var serviceDescription = new ServiceDescription();
        serviceDescription.setType("trafficLightsSignaler");
        serviceDescription.setName(trafficLights.id() + "_signaler");
        agentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, agentDescription);
            System.out.println("Registered TL signal service");
        }
        catch (FIPAException e) {
            System.out.println("Failed to register TL signal service");
            e.printStackTrace();
        }
    }
}
