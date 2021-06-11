package com.kgd.agents.walletController.behaviors;

import com.kgd.agents.walletController.WalletController;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CalculateExpensesBehaviour extends CyclicBehaviour {
    private final WalletController agent;
    public CalculateExpensesBehaviour(WalletController agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        var mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        var msg = agent.receive(mt);

        if (msg != null)
        {
            double additionalCost = Double.parseDouble(msg.getContent());
            agent.cost += additionalCost;
        }
        else
        {
            block();
        }
    }
}
