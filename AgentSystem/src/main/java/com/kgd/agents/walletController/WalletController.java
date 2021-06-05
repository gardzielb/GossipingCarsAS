package com.kgd.agents.walletController;

import com.kgd.agents.walletController.behaviors.CalculateExpensesBehaviour;
import jade.core.Agent;

public class WalletController extends Agent {
    public double cost = 0.0;

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new CalculateExpensesBehaviour(this));
    }

    @Override
    protected void takeDown() {
        // TODO: add saving cost stats to the database
        super.takeDown();
    }
}
