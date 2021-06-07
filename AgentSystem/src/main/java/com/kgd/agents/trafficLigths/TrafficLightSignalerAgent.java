package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.models.geodata.Vec2;
import com.kgd.agents.trafficLigths.managerBehaviors.ChangeLightColorBehavior;
import com.kgd.agents.trafficLigths.managerBehaviors.SignalTrafficLightColorBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Map;

public class TrafficLightSignalerAgent extends Agent {

    private Map<Vec2, Boolean> isGreenMap;
    private TrafficLights trafficLights;

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 2)
            throw new RuntimeException("Traffic lights object is needed by Signaler agent");

        trafficLights = (TrafficLights) args[0];
        registerInDF();

        isGreenMap = (Map<Vec2, Boolean>) args[1];

        addBehaviour(new SignalTrafficLightColorBehavior(this));
        addBehaviour(new ChangeLightColorBehavior(this));
    }

    public boolean isGreen(Vec2 direction) {
        return isGreenMap.get(direction);
    }

    public void changeLight(Vec2 direction) {
        boolean isGreen = isGreenMap.get(direction);
        isGreenMap.replace(direction, !isGreen);
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
