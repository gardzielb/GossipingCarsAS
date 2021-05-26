package com.kgd.agents.fuelStation;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class FuelCarControllerAgent extends Agent {
    @Override
    protected void setup() {
        super.setup();
        float price = Float.parseFloat(getArguments()[0].toString());
        var behavior = new PriceNegotiationInitiatorBehavior(
                this, new ACLMessage(ACLMessage.CFP),
                List.of(
                        new PriceSuggestion(new AID("60aebaedf0f4754b2c50cb1a", AID.ISLOCALNAME), price),
                        new PriceSuggestion(new AID("60aebaedf0f4754b2c50cb1c", AID.ISLOCALNAME), price)
                )
        );
        addBehaviour(behavior);
    }
}
