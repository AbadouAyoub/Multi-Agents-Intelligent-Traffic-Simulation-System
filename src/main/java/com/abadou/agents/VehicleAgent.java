package com.abadou.agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class VehicleAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + " démarré.");

        addBehaviour(new TickerBehaviour(this, 2000) {
            protected void onTick() {
                System.out.println(getLocalName() + " navigue...");
                // Exemple : envoyer un message à un feu
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(getAID("TrafficLight1"));
                msg.setContent("Je m'approche du feu");
                send(msg);
            }
        });
    }
}