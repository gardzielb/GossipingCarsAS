package com.kgd.agents.trafficLigths.managerBehaviors;

import com.kgd.agents.trafficLigths.TrafficLightsManagerAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

public class LightColorController {

    private final TrafficLightsManagerAgent agent;
    private Behaviour colorChangeBehavior = null;
    private boolean isScheduled = false;

    public LightColorController(TrafficLightsManagerAgent agent) {
        this.agent = agent;
    }

    public void scheduleLightsColorChange(String greenLightsId, String redLightsId, long timeout) {
        isScheduled = true;

        colorChangeBehavior = new WakerBehaviour(agent, timeout) {
            @Override
            protected void onWake() {
                isScheduled = false;
                changeLightsColor(greenLightsId, false);

                boolean isPassageFree = !agent.areCarsPassingFromLights(greenLightsId);
                if (isPassageFree) {
                    changeLightsColor(redLightsId, true);
                    long newTimeout = agent.computeLightsTimeout(redLightsId);
                    scheduleLightsColorChange(redLightsId, greenLightsId, newTimeout);
                }
            }
        };
        agent.addBehaviour(colorChangeBehavior);
    }

    public void cancelLightsColorChange() {
        isScheduled = false;
        agent.removeBehaviour(colorChangeBehavior);
    }

    public boolean isColorChangeScheduled() {
        return isScheduled;
    }

    public void changeLightsColor(String lightsId, boolean toGreen) {
        var changeColorRequest = new ACLMessage(ACLMessage.REQUEST);
        changeColorRequest.addReceiver(new AID(lightsId, AID.ISLOCALNAME));
        changeColorRequest.setContent(Boolean.toString(toGreen));
        agent.send(changeColorRequest);
    }
}
