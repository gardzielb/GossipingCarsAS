package com.kgd.agents.walletController.behaviors;

import com.kgd.agents.walletController.WalletController;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CalculateExpensesBehaviour extends TickerBehaviour {
    private final WalletController agent;
    public CalculateExpensesBehaviour(WalletController agent) {
        super(agent, 500);
        this.agent = agent;
    }

    @Override
    protected void onTick() {
        var mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        var msg = agent.receive(mt);

        if (msg == null) return;

        double additionalCost = Double.parseDouble(msg.getContent());
        agent.cost += additionalCost;
    }
}
