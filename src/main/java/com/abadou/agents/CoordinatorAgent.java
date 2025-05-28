package com.abadou.agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import com.abadou.ui.TrafficApp;
import com.abadou.utils.Sensor;

import java.util.List;
import java.util.Map;

import com.abadou.ui.TrafficApp;

public class CoordinatorAgent extends Agent {

    private EtatCycle etatCycle = EtatCycle.VERT_NS;
    private long lastTransition = 0;
    private boolean intelligentMode = false;
    private String prioritizedDirection = null; // "NS" or "EO"
    private static final double PROXIMITY_RANGE = 100.0; // Sensor range for detecting approaching vehicles
    private Sensor proximitySensor = new Sensor(PROXIMITY_RANGE);

    private enum EtatCycle {
        VERT_NS, ORANGE_NS, VERT_EO, ORANGE_EO, INTELLIGENT
    }

    @Override
    protected void setup() {
        System.out.println("Coordinator démarré.");

        List<String> nordSud = List.of("FeuN", "FeuS");
        List<String> estOuest = List.of("FeuE", "FeuO");

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                // Check for messages, but ignore OBSTACLE messages
                ACLMessage msg = receive();
                if (msg != null && !msg.getContent().startsWith("OBSTACLE:")) {
                    System.out.println("Coordinator reçu message: " + msg.getContent());
                }

                long now = System.currentTimeMillis();
                long elapsed = now - lastTransition;
                Map<String, Integer> vehicleCounts = TrafficApp.getVehicleCountsByDirection();

                // Always progress the cycle, even if vehicles are stopped
                if (!intelligentMode) {
                    int nsCount = vehicleCounts.getOrDefault("NORD", 0) + vehicleCounts.getOrDefault("SUD", 0);
                    int eoCount = vehicleCounts.getOrDefault("EST", 0) + vehicleCounts.getOrDefault("OUEST", 0);

                    boolean nsApproaching = isDirectionApproaching("NORD", "SUD");
                    boolean eoApproaching = isDirectionApproaching("EST", "OUEST");

                    if (nsCount >= 2 * eoCount && nsCount > 0 && nsApproaching) {
                        intelligentMode = true;
                        prioritizedDirection = "NS";
                        etatCycle = EtatCycle.INTELLIGENT;
                        nordSud.forEach(f -> sendMessage(f, "setEtat:VERT"));
                        estOuest.forEach(f -> sendMessage(f, "setEtat:ROUGE"));
                        lastTransition = now;
                        System.out.println("Mode Intelligent activé: Priorité Nord-Sud (" + nsCount + " vs " + eoCount + ")");
                        return;
                    } else if (eoCount >= 2 * nsCount && eoCount > 0 && eoApproaching) {
                        intelligentMode = true;
                        prioritizedDirection = "EO";
                        etatCycle = EtatCycle.INTELLIGENT;
                        estOuest.forEach(f -> sendMessage(f, "setEtat:VERT"));
                        nordSud.forEach(f -> sendMessage(f, "setEtat:ROUGE"));
                        lastTransition = now;
                        System.out.println("Mode Intelligent activé: Priorité Est-Ouest (" + eoCount + " vs " + nsCount + ")");
                        return;
                    }
                }

                switch (etatCycle) {
                    case VERT_NS:
                        if (elapsed >= 5000) {
                            nordSud.forEach(f -> sendMessage(f, "setEtat:ORANGE"));
                            lastTransition = now;
                            etatCycle = EtatCycle.ORANGE_NS;
                            System.out.println("Transition: VERT_NS → ORANGE_NS");
                        }
                        break;

                    case ORANGE_NS:
                        if (elapsed >= 5000) {
                            nordSud.forEach(f -> sendMessage(f, "setEtat:ROUGE"));
<<<<<<< HEAD

                            if (TrafficApp.isIntersectionVide()) {
                                estOuest.forEach(f -> sendMessage(f, "setEtat:VERT"));
                                lastTransition = now;
                                etatCycle = EtatCycle.VERT_EO;
                            } // sinon, on reste bloqué en ORANGE_NS
=======
                            estOuest.forEach(f -> sendMessage(f, "setEtat:VERT"));
                            lastTransition = now;
                            etatCycle = EtatCycle.VERT_EO;
                            System.out.println("Transition: ORANGE_NS → VERT_EO");
>>>>>>> 6370002 (ajout de scenarios)
                        }
                        break;

                    case VERT_EO:
                        if (elapsed >= 5000) {
                            estOuest.forEach(f -> sendMessage(f, "setEtat:ORANGE"));
                            lastTransition = now;
                            etatCycle = EtatCycle.ORANGE_EO;
                            System.out.println("Transition: VERT_EO → ORANGE_EO");
                        }
                        break;

                    case ORANGE_EO:
                        if (elapsed >= 5000) {
                            estOuest.forEach(f -> sendMessage(f, "setEtat:ROUGE"));
<<<<<<< HEAD

                            if (TrafficApp.isIntersectionVide()) {
                                nordSud.forEach(f -> sendMessage(f, "setEtat:VERT"));
                                lastTransition = now;
                                etatCycle = EtatCycle.VERT_NS;
                            } // sinon, on reste bloqué en ORANGE_EO
=======
                            nordSud.forEach(f -> sendMessage(f, "setEtat:VERT"));
                            lastTransition = now;
                            etatCycle = EtatCycle.VERT_NS;
                            System.out.println("Transition: ORANGE_EO → VERT_NS");
                        }
                        break;

                    case INTELLIGENT:
                        int nsCount = vehicleCounts.getOrDefault("NORD", 0) + vehicleCounts.getOrDefault("SUD", 0);
                        int eoCount = vehicleCounts.getOrDefault("EST", 0) + vehicleCounts.getOrDefault("OUEST", 0);

                        boolean isPrioritizedCleared = false;
                        if (prioritizedDirection.equals("NS")) {
                            isPrioritizedCleared = nsCount == 0 || isDirectionPassed("NORD", "SUD");
                        } else if (prioritizedDirection.equals("EO")) {
                            isPrioritizedCleared = eoCount == 0 || isDirectionPassed("EST", "OUEST");
                        }

                        if (isPrioritizedCleared) {
                            intelligentMode = false;
                            prioritizedDirection = null;
                            nordSud.forEach(f -> sendMessage(f, "setEtat:VERT"));
                            estOuest.forEach(f -> sendMessage(f, "setEtat:ROUGE"));
                            etatCycle = EtatCycle.VERT_NS;
                            lastTransition = now;
                            System.out.println("Mode Intelligent désactivé: Retour à VERT_NS");
>>>>>>> 6370002 (ajout de scenarios)
                        }
                        break;
                }
            }

            private boolean isDirectionApproaching(String dir1, String dir2) {
                for (Map.Entry<String, double[]> entry : TrafficApp.getVehiclePositions().entrySet()) {
                    String vehicleName = entry.getKey();
                    double[] pos = entry.getValue();
                    double x = pos[0];
                    double y = pos[1];
                    String direction = TrafficApp.getVehicleDirections().getOrDefault(vehicleName, "");

                    if (direction.equals(dir1) || direction.equals(dir2)) {
                        switch (direction) {
                            case "NORD":
                                if (y + 30 >= 340 - PROXIMITY_RANGE && y + 30 <= 340) return true;
                                break;
                            case "SUD":
                                if (y <= 460 + PROXIMITY_RANGE && y >= 460) return true;
                                break;
                            case "EST":
                                if (x <= 460 + PROXIMITY_RANGE && x >= 460) return true;
                                break;
                            case "OUEST":
                                if (x + 30 >= 340 - PROXIMITY_RANGE && x + 30 <= 340) return true;
                                break;
                        }
                    }
                }
                return false;
            }

            private boolean isDirectionPassed(String dir1, String dir2) {
                for (Map.Entry<String, double[]> entry : TrafficApp.getVehiclePositions().entrySet()) {
                    String vehicleName = entry.getKey();
                    double[] pos = entry.getValue();
                    double x = pos[0];
                    double y = pos[1];
                    String direction = TrafficApp.getVehicleDirections().getOrDefault(vehicleName, "");

                    if (direction.equals(dir1) || direction.equals(dir2)) {
                        switch (direction) {
                            case "NORD":
                                if (y + 30 <= 460) return false;
                                break;
                            case "SUD":
                                if (y >= 340) return false;
                                break;
                            case "EST":
                                if (x >= 340) return false;
                                break;
                            case "OUEST":
                                if (x + 30 <= 460) return false;
                                break;
                        }
                    }
                }
                return true;
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