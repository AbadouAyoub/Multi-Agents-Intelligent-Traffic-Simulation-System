package com.abadou.agents;

import com.abadou.ui.TrafficApp;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class TrafficLightAgent extends Agent {
    private String etat = "ROUGE";

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " feu démarré.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getContent().startsWith("setEtat:")) {
                    etat = msg.getContent().split(":")[1];
                    TrafficApp.updateFeu(getLocalName(), etat);
                    System.out.println(getLocalName() + " → état = " + etat);
                } else {
                    block();
                }
            }
        });
    }
}