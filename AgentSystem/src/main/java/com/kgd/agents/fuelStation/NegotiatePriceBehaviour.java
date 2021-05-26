package com.kgd.agents.fuelStation;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetResponder;
import jade.proto.SSIteratedContractNetResponder;
import jade.proto.SSResponderDispatcher;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class NegotiatePriceBehaviour extends SSResponderDispatcher {

    private final double MIN_DISCOUNT = 0.80;
    private final double MAX_DISCOUNT = 0.90 + Double.MIN_VALUE;
    private final float DISCOUNT_STEP = 0.05f;

    private final float currentPrice;
    private final float minimumPrice;

    public NegotiatePriceBehaviour(Agent a, float currentPrice) {
        super(a, ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET));
        this.currentPrice = currentPrice;
        this.minimumPrice = (float) (ThreadLocalRandom.current().nextDouble(MIN_DISCOUNT, MAX_DISCOUNT) * currentPrice);
    }

    @Override
    protected Behaviour createResponder(ACLMessage initiationMsg) {
        return new SSIteratedContractNetResponder(myAgent, initiationMsg) {
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException {
                var reply = cfp.createReply();
                try
                {
                    var suggestedPrice = Float.parseFloat(cfp.getContent());
                    if(suggestedPrice - DISCOUNT_STEP >= minimumPrice) {
                        reply.setPerformative(ACLMessage.PROPOSE);
                        var discountPrice = suggestedPrice - DISCOUNT_STEP;
                        reply.setContent(String.format(Locale.ROOT, "%.2f", discountPrice));
                    }
                    else {
                        reply.setPerformative(ACLMessage.REFUSE);
                    }
                    return reply;
                } catch(NumberFormatException | NullPointerException ex) {
                    throw new NotUnderstoodException("Could not parse message content");
                }
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                var response = accept.createReply();
                response.setPerformative(ACLMessage.INFORM);
                response.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
                response.setContent(propose.getContent());
                return response;
            }

            @Override
            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                super.handleRejectProposal(cfp, propose, reject);
            }
        };
    }
}
