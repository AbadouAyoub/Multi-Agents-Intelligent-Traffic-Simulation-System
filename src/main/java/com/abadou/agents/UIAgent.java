package com.abadou.agents;

import com.abadou.ui.TrafficApp;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class UIAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " prêt à recevoir les mises à jour UI...");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String[] parts = msg.getContent().split(":");

                    if (parts[0].equals("POSITION") && parts.length == 5) {
                        String name = parts[1];
                        double x = Double.parseDouble(parts[2]);
                        double y = Double.parseDouble(parts[3]);
                        String dir = parts[4];
                        TrafficApp.updateVehicle(name, x, y, dir);
                    }
                } else {
                    block();
                }
            }
        });
    }
}