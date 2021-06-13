package com.kgd.agents.trafficLigths.managerBehaviors;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;

public class GreenRedLightState extends LightState {

    private final String greenLightsId;
    private final String redLightsId;
    private Behaviour colorChangeBehavior;

    private boolean isInfinite = false;

    public GreenRedLightState(LightColorFsm fsm, Agent managerAgent, String greenLightsId, String redLightsId,
                              long timeout) {
        super(fsm, managerAgent);
        this.greenLightsId = greenLightsId;
        this.redLightsId = redLightsId;
        scheduleLightsColorChange(timeout);
    }

    @Override
    public void handleCarApproaching(String lightsId) {
        var otherLightsId = fsm.getOtherLightsId(lightsId);
        var lightsCarData = fsm.getCarsData(otherLightsId);

        if (lightsCarData.approachingCount == 0 && lightsCarData.passingCount == 0) {
            managerAgent.removeBehaviour(colorChangeBehavior);
            isInfinite = true;

            if (lightsId.equals(redLightsId)) {
                changeLightsColor(greenLightsId, false);
                changeLightsColor(redLightsId, true);
            }
        }
        else if (lightsId.equals(redLightsId) && isInfinite) {
            long timeout = fsm.computeLightsTimeout(otherLightsId);
            scheduleLightsColorChange(timeout);
        }
    }

    @Override
    public void handleCarExiting(String lightsId) {}

    private void scheduleLightsColorChange(long timeout) {
        isInfinite = false;
        colorChangeBehavior = new WakerBehaviour(managerAgent, timeout) {
            @Override
            protected void onWake() {
                changeLightsColor(greenLightsId, false);
                var greenLightsData = fsm.getCarsData(greenLightsId);

                if (greenLightsData.passingCount == 0) {
                    changeLightsColor(redLightsId, true);
                    long nextTimeout = fsm.computeLightsTimeout(redLightsId);
                    var nextState = new GreenRedLightState(fsm, managerAgent, redLightsId, greenLightsId, nextTimeout);
                    fsm.setState(nextState);
                }
                else {
                    var nextState = new CarExitingLightState(fsm, managerAgent, greenLightsId);
                    fsm.setState(nextState);
                }
            }
        };
        managerAgent.addBehaviour(colorChangeBehavior);
    }
}
