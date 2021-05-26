package com.kgd.agents.fuelStation;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.stream.Collectors;

public class PriceNegotiationInitiatorBehavior extends ContractNetInitiator {

    private final List<PriceSuggestion> priceSuggestions = new ArrayList<>();
    private PriceSuggestion bestPrice;

    public PriceNegotiationInitiatorBehavior(Agent agent, List<PriceSuggestion> priceSuggestions) {
        super(agent, new ACLMessage(ACLMessage.CFP));
        this.priceSuggestions.addAll(priceSuggestions);
        this.bestPrice = priceSuggestions.get(0);
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        var cfpVector = new Vector<ACLMessage>(priceSuggestions.size());
        priceSuggestions.forEach(suggestion -> cfpVector.add(
                createCfp(suggestion.stationAid(), String.format(Locale.ROOT, "%.2f", suggestion.price())))
        );
        return cfpVector;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        super.handleAllResponses(responses, acceptances);

        System.out.println(responses);
        List<ACLMessage> proposals = findPriceProposals(responses);

        if (proposals.isEmpty()) {
            System.out.println("No proposals");
        }
        else if (proposals.size() == 1) {
            var proposal = proposals.get(0);
            acceptances.add(createAcceptance(proposal));
            bestPrice = new PriceSuggestion(proposal.getSender(), Float.parseFloat(proposal.getContent()));
        }
        else {
            var nextRoundCfp = new Vector<ACLMessage>();
            proposals.forEach(
                    proposal -> nextRoundCfp.add(createCfp(proposal.getSender(), proposal.getContent()))
            );
            newIteration(nextRoundCfp);
        }
    }

    @Override
    public int onEnd() {
        System.out.println("Ending negotiation, the winner is " + bestPrice);
        return super.onEnd();
    }

    private List<ACLMessage> findPriceProposals(Vector<ACLMessage> responses) {
        return responses.stream()
                        .filter(r -> r.getPerformative() == ACLMessage.PROPOSE)
                        .collect(Collectors.toList());
    }

    private ACLMessage createAcceptance(ACLMessage proposal) {
        var acceptMsg = proposal.createReply();
        acceptMsg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        acceptMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
        return acceptMsg;
    }

    private ACLMessage createCfp(AID receiver, String content) {
        var cfp = new ACLMessage(ACLMessage.CFP);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
        cfp.addReceiver(receiver);
        cfp.setContent(content);
        return cfp;
    }
}
