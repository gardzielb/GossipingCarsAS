package com.kgd.agents.trafficLigths.managerBehaviors;

import com.kgd.agents.services.LoggerFactory;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.apache.logging.log4j.Logger;

public abstract class LightState {

    protected static final Logger logger = LoggerFactory.getLogger("TL manager FSM");

    protected final LightColorFsm fsm;
    protected final Agent managerAgent;

    protected LightState(LightColorFsm fsm, Agent managerAgent) {
        this.managerAgent = managerAgent;
        this.fsm = fsm;
    }

    public abstract void handleCarApproaching(String lightsId);

    public abstract void handleCarExiting(String lightsId);

    protected void changeLightsColor(String lightsId, boolean toGreen) {
        logger.debug("Changing {} color to {}", lightsId, toGreen ? "green" : "red");
        var changeColorRequest = new ACLMessage(ACLMessage.REQUEST);
        changeColorRequest.addReceiver(new AID(lightsId, AID.ISLOCALNAME));
        changeColorRequest.setContent(Boolean.toString(toGreen));
        managerAgent.send(changeColorRequest);

//        if (toGreen)
//            greenLight = lightsId;
//        else if (lightsId.equals(greenLight))
//            greenLight = null;
    }
}
