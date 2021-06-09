package com.kgd.agents.trafficLigths;

import com.kgd.agents.trafficLigths.managerBehaviors.ControlTrafficLightsColorBehavior;
import com.kgd.agents.trafficLigths.managerBehaviors.ReceiveCarNotificationsBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class TrafficLightsManagerAgent extends Agent {

    private String[] trafficLightIds;

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 2)
            throw new IllegalArgumentException("TrafficLightsManager should be passed traffic lights object!");

        trafficLightIds = new String[]{args[0].toString(), args[1].toString()};
        registerInDF();

        addBehaviour(new ReceiveCarNotificationsBehavior(this));
        addBehaviour(new ControlTrafficLightsColorBehavior(this, 3000, 5000, trafficLightIds));
    }

    private void registerInDF() {
        var agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        for (String lightId : trafficLightIds) {
            var serviceDescription = new ServiceDescription();
            serviceDescription.setType("trafficLightsManager");
            serviceDescription.setName(lightId + "_manager");
            agentDescription.addServices(serviceDescription);
        }

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
