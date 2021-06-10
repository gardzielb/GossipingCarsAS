package com.kgd.agents.fuelStation.controllerBehaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.fuelStation.FuelCarControllerAgent;
import com.kgd.agents.fuelStation.FuelStationData;
import com.kgd.agents.fuelStation.OptimalFuelPriceCalculator;
import com.kgd.agents.fuelStation.PriceSuggestion;
import com.kgd.agents.models.geodata.GeoPoint;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PriceNegotiationInitiatorBehavior extends ContractNetInitiator {

    private final FuelCarControllerAgent agent;
    private final List<PriceSuggestion> priceSuggestions = new ArrayList<>();
    private final HashMap<AID, GeoPoint> locations = new HashMap<>();
    private PriceSuggestion bestPrice;

    public PriceNegotiationInitiatorBehavior(FuelCarControllerAgent agent, List<FuelStationData> stationsData) {
        super(agent, new ACLMessage(ACLMessage.CFP));
        var priceSuggestions = OptimalFuelPriceCalculator
                .calculateOptimalFuelPricesAsPriceSuggestion(stationsData);
        this.agent = agent;
        this.priceSuggestions.addAll(priceSuggestions);
        this.bestPrice = priceSuggestions.get(0);
        priceSuggestions.forEach(
                station -> locations.put(station.stationAid(), station.location())
        );
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
            bestPrice = new PriceSuggestion(proposal.getSender(), null, Float.parseFloat(proposal.getContent()));
        }
        else {
            var exampleProposal = proposals.get(0);
            bestPrice = new PriceSuggestion(exampleProposal.getSender(), null, Float.parseFloat(exampleProposal.getContent()));

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
        agent.negotiatedPrice = bestPrice;

        String driverName = agent.getLocalName();
        driverName = driverName.substring(0, driverName.length() - "_fuel_controller".length());

        // request a new waypoint
        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(new AID(driverName+"_route_navigator", AID.ISLOCALNAME));
        try {
            message.setContent((new ObjectMapper()).writeValueAsString(
                    new GeoPoint[] { locations.get(agent.negotiatedPrice.stationAid()) }
            ));
        } catch (JsonProcessingException ignore) { }
        agent.send(message);

        agent.negotiatedPrice = bestPrice;
        agent.removeBehaviour(this);

        message = new ACLMessage(ACLMessage.PROPOSE);
        message.addReceiver(new AID(driverName, AID.ISLOCALNAME));
        message.setContent("start");
        agent.send(message);

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
