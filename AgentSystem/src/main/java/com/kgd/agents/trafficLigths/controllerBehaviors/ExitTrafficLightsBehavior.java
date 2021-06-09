package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ExitTrafficLightsBehavior extends OneShotBehaviour {

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID trafficLightsAgent;

    public ExitTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, AID trafficLightsAgent) {
        this.controllerAgent = controllerAgent;
        this.trafficLightsAgent = trafficLightsAgent;
    }

    @Override
    public void action() {
        var exitNotification = new ACLMessage(ACLMessage.INFORM);
        exitNotification.addReceiver(trafficLightsAgent);
        controllerAgent.send(exitNotification);
        controllerAgent.prepareForNextTrafficLights();
    }
}
