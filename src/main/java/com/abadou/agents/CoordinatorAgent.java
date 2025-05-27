package com.abadou.agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

import com.abadou.ui.TrafficApp;

public class CoordinatorAgent extends Agent {

    private EtatCycle etatCycle = EtatCycle.VERT_NS;
    private long lastTransition = 0;

    private enum EtatCycle {
        VERT_NS, ORANGE_NS, VERT_EO, ORANGE_EO
    }

    @Override
    protected void setup() {
        System.out.println("Coordinator démarré.");

        List<String> nordSud = List.of("FeuN", "FeuS");
        List<String> estOuest = List.of("FeuE", "FeuO");

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                long now = System.currentTimeMillis();
                long elapsed = now - lastTransition;

                switch (etatCycle) {
                    case VERT_NS:
                        if (elapsed >= 5000) {
                            nordSud.forEach(f -> sendMessage(f, "setEtat:ORANGE"));
                            lastTransition = now;
                            etatCycle = EtatCycle.ORANGE_NS;
                        }
                        break;

                    case ORANGE_NS:
                        if (elapsed >= 2000) {
                            nordSud.forEach(f -> sendMessage(f, "setEtat:ROUGE"));

                            if (TrafficApp.isIntersectionVide()) {
                                estOuest.forEach(f -> sendMessage(f, "setEtat:VERT"));
                                lastTransition = now;
                                etatCycle = EtatCycle.VERT_EO;
                            } // sinon, on reste bloqué en ORANGE_NS
                        }
                        break;

                    case VERT_EO:
                        if (elapsed >= 5000) {
                            estOuest.forEach(f -> sendMessage(f, "setEtat:ORANGE"));
                            lastTransition = now;
                            etatCycle = EtatCycle.ORANGE_EO;
                        }
                        break;

                    case ORANGE_EO:
                        if (elapsed >= 2000) {
                            estOuest.forEach(f -> sendMessage(f, "setEtat:ROUGE"));

                            if (TrafficApp.isIntersectionVide()) {
                                nordSud.forEach(f -> sendMessage(f, "setEtat:VERT"));
                                lastTransition = now;
                                etatCycle = EtatCycle.VERT_NS;
                            } // sinon, on reste bloqué en ORANGE_EO
                        }
                        break;
                }
            }

            @Override
            public boolean done() {
                return false;
            }

            private void sendMessage(String feu, String content) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(feu, AID.ISLOCALNAME));
                msg.setContent(content);
                send(msg);
            }
        });
    }
}
