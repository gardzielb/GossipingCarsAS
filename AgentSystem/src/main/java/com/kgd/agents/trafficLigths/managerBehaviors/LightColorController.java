//package com.kgd.agents.trafficLigths.managerBehaviors;
//
//import com.kgd.agents.services.LoggerFactory;
//import com.kgd.agents.trafficLigths.TrafficLightsManagerAgent;
//import jade.core.AID;
//import jade.core.behaviours.Behaviour;
//import jade.core.behaviours.WakerBehaviour;
//import jade.lang.acl.ACLMessage;
//import org.apache.logging.log4j.Logger;
//
//public class LightColorController {
//
//    private static final Logger logger = LoggerFactory.getLogger("TL Manager");
//
//    private final TrafficLightsManagerAgent agent;
//    private Behaviour colorChangeBehavior = null;
//    private boolean isScheduled = false;
//    private boolean isCarExiting = false;
//    private String greenLight;
//
//    public LightColorController(TrafficLightsManagerAgent agent) {
//        this.agent = agent;
//    }
//
//    public void scheduleLightsColorChange(String greenLightsId, String redLightsId, long timeout) {
//        logger.debug("Scheduling light color change. Lights {} will turn red in {}ms", greenLightsId, timeout);
//        greenLight = greenLightsId;
//        isScheduled = true;
//
//        colorChangeBehavior = new WakerBehaviour(agent, timeout) {
//            @Override
//            protected void onWake() {
//                changeLightsColor(greenLightsId, false);
//                isCarExiting = agent.areCarsPassingFromLights(greenLightsId);
//
//                if (!isCarExiting) {
////                    logger.debug("Passage is free, turning lights " + redLightsId + " green");
//                    changeLightsColor(redLightsId, true);
//                    long newTimeout = agent.computeLightsTimeout(redLightsId);
//                    scheduleLightsColorChange(redLightsId, greenLightsId, newTimeout);
//                }
//            }
//        };
//        agent.addBehaviour(colorChangeBehavior);
//    }
//
//    public void cancelLightsColorChange() {
//        logger.debug("Canceling scheduled lights color change");
//        isScheduled = false;
//        agent.removeBehaviour(colorChangeBehavior);
//    }
//
//    public boolean isColorChangeScheduled() {
//        return isScheduled;
//    }
//
//    public boolean isWaitingForCarExit() {
//        return isCarExiting;
//    }
//
//    public void changeLightsColor(String lightsId, boolean toGreen) {
//        logger.debug("Changing {} color to {}", lightsId, toGreen ? "green" : "red");
//        var changeColorRequest = new ACLMessage(ACLMessage.REQUEST);
//        changeColorRequest.addReceiver(new AID(lightsId, AID.ISLOCALNAME));
//        changeColorRequest.setContent(Boolean.toString(toGreen));
//        agent.send(changeColorRequest);
//
//        if (toGreen)
//            greenLight = lightsId;
//        else if (lightsId.equals(greenLight))
//            greenLight = null;
//    }
//
//    public boolean isLightGreen(String lightsId) {
//        return lightsId.equals(greenLight);
//    }
//}
