package com.kgd.agents.driver.behaviors;

import com.kgd.agents.driver.DriverAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.Instant;

public class StartStopBehavior extends CyclicBehaviour {
    private final DriverAgent agent;

    public StartStopBehavior(DriverAgent driverAgent) {
        agent = driverAgent;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        var message = agent.receive(mt);

        if (message != null) {
            switch (message.getContent()) {
                case "start" -> {
                    agent.time = Instant.now().toEpochMilli();
                    agent.addBehaviour(agent.calcPositionBehaviour);
                }
                case "stop" -> agent.removeBehaviour(agent.calcPositionBehaviour);
            }
        }
        else {
            block();
        }
    }
}
