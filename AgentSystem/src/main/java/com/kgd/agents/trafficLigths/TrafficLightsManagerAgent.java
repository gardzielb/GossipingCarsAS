package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.services.LoggerFactory;
import com.kgd.agents.trafficLigths.managerBehaviors.LightColorFsm;
import com.kgd.agents.trafficLigths.managerBehaviors.ReceiveCarNotificationsBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.logging.log4j.Logger;

public class TrafficLightsManagerAgent extends Agent {

    public TrafficLightsManagerAgent() {}

    private static final Logger logger = LoggerFactory.getLogger("TL Manager");

    private LightColorFsm lightColorController;

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 3)
            throw new IllegalArgumentException("TrafficLightsManager should be passed traffic lights object!");

        var tl1 = (TrafficLights) args[0];
        var tl2 = (TrafficLights) args[1];
        long defaultColorChangeTimeout = (int) args[2] * 1000;

        registerInDF(tl1.id(), tl2.id());

        lightColorController = new LightColorFsm(this, tl1, tl2, defaultColorChangeTimeout);

        addBehaviour(new ReceiveCarNotificationsBehavior(this));
    }

    public void handleExpectedApproach(String lightsId) {
        logger.debug("Car is coming to lights {}", lightsId);
        lightColorController.handleCarApproaching(lightsId);
    }

    public void handlePassThrough(String lightsId) {
        logger.debug("Car coming from lights {} is passing through", lightsId);
        lightColorController.handleCarPassing(lightsId);
    }

    public void handleExit(String lightsId) {
        logger.debug("Car coming from lights {} is exiting", lightsId);
        lightColorController.handleCarExiting(lightsId);
    }

    private void registerInDF(String... trafficLightIds) {
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
            logger.info("Registered TL service");
        }
        catch (FIPAException e) {
            logger.error("Failed to register TL service");
            e.printStackTrace();
        }
    }
}
