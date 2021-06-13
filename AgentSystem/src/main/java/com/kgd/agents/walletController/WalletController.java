package com.kgd.agents.walletController;

import com.kgd.agents.models.geodata.Stats;
import com.kgd.agents.services.HttpStatsService;
import com.kgd.agents.services.StatsService;
import com.kgd.agents.walletController.behaviors.CalculateExpensesBehaviour;
import jade.core.Agent;

public class WalletController extends Agent {
    public double cost = 0.0;
    private StatsService statsService;
    private String uuid;

    @Override
    protected void setup() {
        super.setup();
        Object[] args = getArguments();
        uuid = (String) args[0];

        addBehaviour(new CalculateExpensesBehaviour(this));
        statsService = new HttpStatsService();
    }

    @Override
    protected void takeDown() {
        String name = getLocalName();
        var driverName = name.substring(0, name.length() - "_cost_controller".length());
        statsService.upsert(new Stats(uuid, null, cost, null, false, null));
        System.out.println("Dying nicely on takedown");
        super.takeDown();
    }
}
