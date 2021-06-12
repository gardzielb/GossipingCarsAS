package com.kgd.agents.trafficLigths;

import com.kgd.agents.services.LoggerFactory;
import com.kgd.agents.trafficLigths.managerBehaviors.LightColorController;
import com.kgd.agents.trafficLigths.managerBehaviors.ReceiveCarNotificationsBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.apache.logging.log4j.Logger;

public class TrafficLightsManagerAgent extends Agent {

    static class PassingCarsData {
        public int approachingCount = 0;
        public int passingCount = 0;
    }

    private static final Logger logger = LoggerFactory.getLogger("TL Manager");

    private final LightColorController lightColorController = new LightColorController(this);

    private long defaultColorChangeTimeout;
    private String LEFT_LIGHTS, RIGHT_LIGHTS;
    private final PassingCarsData leftTlData = new PassingCarsData();
    private final PassingCarsData rightTlData = new PassingCarsData();

    @Override
    protected void setup() {
        super.setup();

        var args = getArguments();
        if (args == null || args.length < 3)
            throw new IllegalArgumentException("TrafficLightsManager should be passed traffic lights object!");

        LEFT_LIGHTS = args[0].toString();
        RIGHT_LIGHTS = args[1].toString();
        defaultColorChangeTimeout = (int) args[2] * 1000;

        registerInDF(args[0].toString(), args[1].toString());

        lightColorController.scheduleLightsColorChange(LEFT_LIGHTS, RIGHT_LIGHTS, defaultColorChangeTimeout);

        addBehaviour(new ReceiveCarNotificationsBehavior(this));
    }

    public void handleExpectedApproach(String lightsId) {
//        logger.debug("Car is coming to lights {}", lightsId);
        var carsData = carsData(lightsId);
        carsData.approachingCount++;

        String otherLightsId = otherLightsId(lightsId);
        var otherSideData = carsData(otherLightsId);

        if (otherSideData.passingCount == 0 && otherSideData.approachingCount == 0) {
//            logger.debug("No one is driving from the other side, green light for him");
            lightColorController.cancelLightsColorChange();
            lightColorController.changeLightsColor(otherLightsId, false);
            lightColorController.changeLightsColor(lightsId, true);
        }
        else if (!lightColorController.isColorChangeScheduled()) {
//            logger.debug("Scheduling back light changes");
            long timeout = computeLightsTimeout(otherLightsId);
            lightColorController.scheduleLightsColorChange(otherLightsId, lightsId, timeout);
        }
    }

    public long computeLightsTimeout(String lightsId) {
        var lightsCarData = carsData(lightsId);
        var otherLightsCarData = otherSideCarsData(lightsId);
        if (otherLightsCarData.approachingCount == 0)
            return defaultColorChangeTimeout;

        float timeoutRatio = (float) lightsCarData.approachingCount / (float) otherLightsCarData.approachingCount;
        return (long) (defaultColorChangeTimeout * timeoutRatio);
    }

    public boolean areCarsPassingFromLights(String lightsId) {
        return carsData(lightsId).passingCount > 0;
    }

    public void handlePassThrough(String lightsId) {
//        logger.debug("Car coming from lights {} is passing through", lightsId);
        var carsData = carsData(lightsId);
        if (carsData.approachingCount > 0)
            carsData.approachingCount--;
        carsData.passingCount++;
    }

    public void handleExit(String lightsId) {
//        logger.debug("Car coming from lights {} is exiting", lightsId);
        var carsData = carsData(lightsId);
        carsData.passingCount--;

//        String changeScheduleInfo = lightColorController.isColorChangeScheduled() ? "scheduled" : "not scheduled";
//        String lightColor = lightColorController.isLightGreen(lightsId) ? "green" : "red";
//        logger.debug(
//                "There are currently {} cars passing from that direction, light change is {} and light {} is {}",
//                carsData.passingCount, changeScheduleInfo, lightsId, lightColor
//        );

        if (carsData.passingCount == 0 &&
                !lightColorController.isColorChangeScheduled() &&
                !lightColorController.isLightGreen(lightsId)) {

            var otherLightsId = otherLightsId(lightsId);
//            logger.debug(
//                    "Turning lights {} green, because {} are red already", otherLightsId, lightsId
//            );

            lightColorController.changeLightsColor(otherLightsId, true);
            lightColorController.scheduleLightsColorChange(
                    otherLightsId, lightsId, computeLightsTimeout(otherLightsId)
            );
        }
    }

    private PassingCarsData carsData(String lightsId) {
        return lightsId.equals(LEFT_LIGHTS) ? leftTlData : rightTlData;
    }

    private PassingCarsData otherSideCarsData(String lightsId) {
        return lightsId.equals(RIGHT_LIGHTS) ? leftTlData : rightTlData;
    }

    private String otherLightsId(String lightsId) {
        return lightsId.equals(LEFT_LIGHTS) ? RIGHT_LIGHTS : LEFT_LIGHTS;
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
