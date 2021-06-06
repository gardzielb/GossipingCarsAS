package com.kgd.agents.trafficLigths.behaviors;

import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.utility.CarLocationQuery;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class NotifyTrafficLightsBehavior extends SimpleBehaviour {

    private final Agent agent;

    public NotifyTrafficLightsBehavior(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {

    }

    @Override
    public boolean done() {
        return false;
    }

    private CarLocationData receiveCarLocation() {
        var locationQuery = new CarLocationQuery();
        String agentName = agent.getLocalName();
        String carName = agentName.substring(0, agentName.length() - "_route_navigator".length());
        return locationQuery.blockingReceiveLocation(agent, carName);
    }
}
