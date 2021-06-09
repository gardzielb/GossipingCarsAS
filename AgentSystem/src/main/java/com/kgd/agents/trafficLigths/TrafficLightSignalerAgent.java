package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.trafficLigths.signalerBehaviors.ChangeLightColorBehavior;
import com.kgd.agents.trafficLigths.signalerBehaviors.SignalTrafficLightColorBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class TrafficLightSignalerAgent extends Agent {

    private boolean isGreen = false;
    private TrafficLights trafficLights;

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 4)
            throw new RuntimeException("Traffic lights object is needed by Signaler agent");

        trafficLights = (TrafficLights) args[0];
        isGreen = (boolean) args[1];
        var exitPoint = (GeoPoint) args[2];
        var exitControllerName = args[3].toString();

        registerInDF();

        addBehaviour(new SignalTrafficLightColorBehavior(this, exitPoint, exitControllerName));
        addBehaviour(new ChangeLightColorBehavior(this));
    }

    public boolean isGreen() {
        return isGreen;
    }

    public void changeLight() {
        isGreen = !isGreen;
//        System.out.println(getLocalName() + ": " + (isGreen ? "GREEN" : "RED"));
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
