package com.kgd.agents.fuelStation;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetResponder;
import jade.proto.SSIteratedContractNetResponder;
import jade.proto.SSResponderDispatcher;

import java.util.Locale;
import java.util.Random;

public class NegotiatePriceBehaviour extends SSResponderDispatcher {

    private final float currentPrice;
    private final float minimumPricePercentageAfterDiscount = 0.85f;
    private final Random random;

    public NegotiatePriceBehaviour(Agent a, float currentPrice) {
        super(a, ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET));
        this.currentPrice = currentPrice;
        this.random = new Random();
    }

    @Override
    protected Behaviour createResponder(ACLMessage initiationMsg) {
        return new SSIteratedContractNetResponder(myAgent, initiationMsg) {
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) {
                var reply = cfp.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                var discountPrice = minimumPricePercentageAfterDiscount * currentPrice + random.nextFloat() * (currentPrice - minimumPricePercentageAfterDiscount * currentPrice);
                reply.setContent(String.format(Locale.ROOT, "%.2f", discountPrice));
                return reply;
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                return super.handleAcceptProposal(cfp, propose, accept);
            }

            @Override
            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                super.handleRejectProposal(cfp, propose, reject);
            }
        };
    }
}
