package com.abadou.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class TrafficLightAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + " feu en ligne.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(getLocalName() + " a reçu : " + msg.getContent() + " de " + msg.getSender().getLocalName());
                } else {
                    block();
                }
            }
        });
    }
}
