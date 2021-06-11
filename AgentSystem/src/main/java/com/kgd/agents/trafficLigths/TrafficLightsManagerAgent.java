package com.kgd.agents.trafficLigths;

import com.kgd.agents.trafficLigths.managerBehaviors.LightColorController;
import com.kgd.agents.trafficLigths.managerBehaviors.ReceiveCarNotificationsBehavior;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class TrafficLightsManagerAgent extends Agent {

    static class PassingCarsData {
        public int approachingCount = 0;
        public int passingCount = 0;
    }

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
        System.out.println("Car is coming to lights " + lightsId);
        var carsData = carsData(lightsId);
        carsData.approachingCount++;

        String otherLightsId = otherLightsId(lightsId);
        var otherSideData = carsData(otherLightsId);

        if (otherSideData.passingCount == 0 && otherSideData.approachingCount == 0) {
            System.out.println("No one is driving from the other side, green light for him");
            lightColorController.cancelLightsColorChange();
            lightColorController.changeLightsColor(otherLightsId, false);
            lightColorController.changeLightsColor(lightsId, true);
        }
        else if (!lightColorController.isColorChangeScheduled()) {
            System.out.println("Scheduling back light changes");
            long timeout = computeLightsTimeout(lightsId);
            lightColorController.scheduleLightsColorChange(lightsId, otherLightsId, timeout);
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
        System.out.println("Car coming from lights " + lightsId + " is passing through");
        var carsData = carsData(lightsId);
        carsData.approachingCount--;
        carsData.passingCount++;
    }

    public void handleExit(String lightsId) {
        System.out.println("Car coming from lights " + lightsId + " is exiting");
        var carsData = carsData(lightsId);
        carsData.passingCount--;

        if (carsData.passingCount == 0 && !lightColorController.isColorChangeScheduled()) {
            lightColorController.changeLightsColor(lightsId, true);
            lightColorController.scheduleLightsColorChange(
                    lightsId, otherLightsId(lightsId), computeLightsTimeout(lightsId)
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
            System.out.println("Registered TL service");
        }
        catch (FIPAException e) {
            System.out.println("Failed to register TL service");
            e.printStackTrace();
        }
    }
}
