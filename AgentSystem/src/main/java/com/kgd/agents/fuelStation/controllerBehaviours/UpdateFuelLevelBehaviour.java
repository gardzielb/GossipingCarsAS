package com.kgd.agents.fuelStation.controllerBehaviours;

import com.kgd.agents.fuelStation.FuelCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class UpdateFuelLevelBehaviour extends TickerBehaviour {
    protected final FuelCarControllerAgent agent;

    public UpdateFuelLevelBehaviour(FuelCarControllerAgent agent) {
        super(agent, 500);
        this.agent = agent;
    }

    @Override
    public void onTick() {
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        var message = agent.receive(template);

        if (message != null) {
            double distance = Double.parseDouble(message.getContent());
            agent.currentCapacity -= Math.min(distance / 100 * agent.combustion, agent.currentCapacity);
        }

        if (agent.onRouteToStation) {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            message = agent.receive(mt);

            if (message == null) return;

            System.out.println("Mmmm, pyszne paliwo");
            double diff = agent.capacity - agent.currentCapacity;
            double cost = diff * agent.negotiatedPrice.price();

            // send cost to cost manager
            String name = agent.getLocalName();
            name = name.substring(0, name.length() - "_fuel_controller".length());

            message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(new AID(name + "_cost_controller", AID.ISLOCALNAME));
            message.setContent(Double.toString(cost));

            agent.send(message);

            agent.currentCapacity = agent.capacity;
            agent.onRouteToStation = false;
        } else if (agent.currentCapacity / agent.capacity < 0.1) {
                System.out.println("E, paliwo ci się kończy");
                agent.onRouteToStation = true;
                agent.addBehaviour(new PrepareForNegotiationsBehaviour(agent));
        }
    }
}

