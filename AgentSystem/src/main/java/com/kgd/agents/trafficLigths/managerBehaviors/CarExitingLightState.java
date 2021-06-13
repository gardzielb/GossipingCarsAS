package com.kgd.agents.trafficLigths.managerBehaviors;

import jade.core.Agent;

public class CarExitingLightState extends LightState {

    private final String carEnterLightsId;

    protected CarExitingLightState(LightColorFsm fsm, Agent managerAgent, String carEnterLightsId) {
        super(fsm, managerAgent);
        this.carEnterLightsId = carEnterLightsId;
    }

    @Override
    public void handleCarApproaching(String lightsId) {}

    @Override
    public void handleCarExiting(String lightsId) {
        if (!lightsId.equals(carEnterLightsId))
            throw new IllegalStateException();

        var lightsCarData = fsm.getCarsData(lightsId);
        if (lightsCarData.passingCount == 0) {
            var otherLightsId = fsm.getOtherLightsId(lightsId);
            long timeout = fsm.computeLightsTimeout(otherLightsId);
            changeLightsColor(otherLightsId, true);
            var nextState = new GreenRedLightState(fsm, managerAgent, otherLightsId, lightsId, timeout);
            fsm.setState(nextState);
        }
    }
}
