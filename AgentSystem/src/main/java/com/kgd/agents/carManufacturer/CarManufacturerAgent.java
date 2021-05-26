package com.kgd.agents.carManufacturer;

import com.kgd.agents.carManufacturer.behaviors.CreateNewCarAgentsBehaviour;
import jade.core.Agent;

public class CarManufacturerAgent extends Agent {
    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new CreateNewCarAgentsBehaviour(this, 1000));
    }
}
