package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.trafficLigths.managerBehaviors.ReceiveApproachNotificationsBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class TrafficLightsManagerAgent extends Agent {

    private TrafficLights trafficLights;

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length == 0)
            throw new IllegalArgumentException("TrafficLightsManager should be passed traffic lights object!");

        trafficLights = (TrafficLights) args[0];
        registerInDF();

        addBehaviour(new ReceiveApproachNotificationsBehavior(this));
    }

    private void registerInDF() {
        var agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());
        var serviceDescription = new ServiceDescription();
        serviceDescription.setType("trafficLightsManager");
        serviceDescription.setName(trafficLights.id() + "_manager");
        agentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, agentDescription);
            System.out.println("Registered TL service");
        }
        catch (FIPAException e) {
            System.out.println("Failed to register TL service");
            e.printStackTrace();
        }
    }
}
