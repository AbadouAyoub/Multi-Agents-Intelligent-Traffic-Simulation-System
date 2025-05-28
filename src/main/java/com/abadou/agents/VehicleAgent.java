package com.abadou.agents;

import com.abadou.ui.TrafficApp;
import com.abadou.utils.Sensor;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map;

public class VehicleAgent extends Agent {

    private String directionInit = "NORD";
    private String direction = "NORD";
    private String intentionDirection = "TOUT_DROIT";
    private boolean virageEffectue = false;
    private int demiTourPhase = 0;
<<<<<<< HEAD

    private double x, y;
    private static final double VITESSE = 1.0;
    private static final double LONG_VHL = 30;
    private static final double STOP_MARGIN = 5;
=======
    private boolean obstacleDetected = false;
    private double obstacleX = -1;
    private double obstacleY = -1;
    private boolean stoppedAtObstacle = false;

    private double x, y;
    private static final double VITESSE = 1.0;
    private static final double SLOW_VITESSE = 0.5;
    private static final double LONG_VHL = 30;
    private static final double STOP_MARGIN = 5;
    private static final double SENSOR_RANGE = 50;
    private static final double SIMULATION_BOUNDARY = 800;
    private Sensor sensor = new Sensor(SENSOR_RANGE);
>>>>>>> 6370002 (ajout de scenarios)

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0)
            directionInit = args[0].toString().toUpperCase();
        if (args != null && args.length > 1)
            intentionDirection = args[1].toString().toUpperCase();

        direction = directionInit;

        switch (directionInit) {
            case "NORD" -> { x = 360; y = 0; }
<<<<<<< HEAD
            case "SUD"  -> { x = 440; y = 800; }
            case "EST"  -> { x = 800; y = 360; }
            case "OUEST"-> { x = 0;   y = 440; }
=======
            case "SUD" -> { x = 440; y = 800; }
            case "EST" -> { x = 800; y = 360; }
            case "OUEST" -> { x = 0; y = 440; }
>>>>>>> 6370002 (ajout de scenarios)
        }

        addBehaviour(new TickerBehaviour(this, 50) {
            @Override
            protected void onTick() {
<<<<<<< HEAD
                boolean feuVert = switch (direction) {
                    case "NORD" -> TrafficApp.isFeuVert("FeuN");
                    case "SUD"  -> TrafficApp.isFeuVert("FeuS");
                    case "EST"  -> TrafficApp.isFeuVert("FeuE");
                    case "OUEST"-> TrafficApp.isFeuVert("FeuO");
                    default     -> true;
                };

                boolean doitStop = switch (direction) {
                    case "NORD"  -> !feuVert && (y + LONG_VHL >= 340 - STOP_MARGIN) && (y + LONG_VHL <= 340 + STOP_MARGIN);
                    case "SUD"   -> !feuVert && (y <= 460 + STOP_MARGIN) && (y >= 460 - STOP_MARGIN);
                    case "EST"   -> !feuVert && (x <= 460 + STOP_MARGIN) && (x >= 460 - STOP_MARGIN);
                    case "OUEST" -> !feuVert && (x + LONG_VHL >= 340 - STOP_MARGIN) && (x + LONG_VHL <= 340 + STOP_MARGIN);
                    default      -> false;
                };

                if (!doitStop) {

                    // 🔁 DEMI-TOUR à 180° en 3 étapes
                    if (intentionDirection.equals("DEMI_TOUR")) {
                        if (demiTourPhase == 0 && directionInit.equals("NORD") && y >= 460) {
                            direction = "OUEST";
                            y = 440;
                            demiTourPhase = 1;
                        } else if (demiTourPhase == 1 && x >= 460) {
                            direction = "SUD";
                            x = 440;
                            demiTourPhase = 2;
                        }
                    }

                    // 🔄 Virage standard
                    else if (!virageEffectue && intentionDirection.equals("TOURNER_DROITE")) {
                        if (directionInit.equals("NORD") && y >= 380) {
                            direction = "EST"; y = 360; virageEffectue = true;
                        } else if (directionInit.equals("SUD") && y <= 420) {
                            direction = "OUEST"; y = 440; virageEffectue = true;
                        } else if (directionInit.equals("EST") && x <= 420) {
                            direction = "SUD"; x = 440; virageEffectue = true;
                        } else if (directionInit.equals("OUEST") && x >= 380) {
                            direction = "NORD"; x = 360; virageEffectue = true;
                        }
                    } else if (!virageEffectue && intentionDirection.equals("TOURNER_GAUCHE")) {
                        if (directionInit.equals("NORD") && y >= 460) {
                            direction = "OUEST"; y = 440; virageEffectue = true;
                        } else if (directionInit.equals("SUD") && y <= 340) {
                            direction = "EST"; y = 360; virageEffectue = true;
                        } else if (directionInit.equals("EST") && x <= 340) {
                            direction = "NORD"; x = 360; virageEffectue = true;
                        } else if (directionInit.equals("OUEST") && x >= 460) {
                            direction = "SUD"; x = 440; virageEffectue = true;
                        }
                    }

                    switch (direction) {
                        case "NORD"  -> y += VITESSE;
                        case "SUD"   -> y -= VITESSE;
                        case "EST"   -> x -= VITESSE;
                        case "OUEST" -> x += VITESSE;
                    }
                }

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("UI", AID.ISLOCALNAME));
                msg.setContent("POSITION:" + getLocalName() + ":" + x + ":" + y + ":" + direction);
                send(msg);
=======
                // Handle obstacle messages
                ACLMessage msg = receive();
                if (msg != null && msg.getContent().startsWith("OBSTACLE:")) {
                    System.out.println(getLocalName() + ": Message OBSTACLE reçu à y=" + y);
                    String[] parts = msg.getContent().split(":");
                    if (parts.length == 3) {
                        obstacleX = Double.parseDouble(parts[1]);
                        obstacleY = Double.parseDouble(parts[2]);
                        obstacleDetected = true;
                        decideDirectionChange();
                    }
                }

                // Exit simulation check
                if (hasExited()) {
                    System.out.println(getLocalName() + ": Véhicule a quitté la simulation à x=" + x + ", y=" + y + ", direction=" + direction);
                    TrafficApp.removeVehicle(getLocalName());
                    doDelete();
                    return;
                }

                // Detect static obstacle
                double staticObstacleX = TrafficApp.getObstacleX();
                double staticObstacleY = TrafficApp.getObstacleY();
                if (staticObstacleX != -1 && staticObstacleY != -1 && !obstacleDetected) {
                    if (sensor.isInRange(x, y, staticObstacleX, staticObstacleY)) {
                        boolean isAhead = switch (direction) {
                            case "NORD" -> staticObstacleY > y;
                            case "SUD" -> staticObstacleY < y;
                            case "EST" -> staticObstacleX < x;
                            case "OUEST" -> staticObstacleX > x;
                            default -> false;
                        };
                        if (isAhead) {
                            obstacleDetected = true;
                            obstacleX = staticObstacleX;
                            obstacleY = staticObstacleY;
                            sendObstacleMessage();
                        }
                    }
                }

                // Slow down or stop for obstacle
                double currentVitesse = VITESSE;
                if (obstacleDetected && !stoppedAtObstacle) {
                    double distToObstacle = switch (direction) {
                        case "NORD" -> obstacleY - (y + LONG_VHL);
                        case "SUD" -> y - obstacleY;
                        case "EST" -> x - obstacleX;
                        case "OUEST" -> obstacleX - (x + LONG_VHL);
                        default -> Double.MAX_VALUE;
                    };
                    if (distToObstacle <= 10) {
                        stoppedAtObstacle = true;
                        System.out.println(getLocalName() + ": Arrêté à cause de l'obstacle à distance=" + distToObstacle);
                    } else if (distToObstacle <= 30) {
                        currentVitesse = SLOW_VITESSE;
                        System.out.println(getLocalName() + ": Ralentit à cause de l'obstacle à distance=" + distToObstacle);
                    }
                }

                // Traffic light check
                boolean feuVert = switch (direction) {
                    case "NORD" -> TrafficApp.isFeuVert("FeuN");
                    case "SUD" -> TrafficApp.isFeuVert("FeuS");
                    case "EST" -> TrafficApp.isFeuVert("FeuE");
                    case "OUEST" -> TrafficApp.isFeuVert("FeuO");
                    default -> true;
                };

                boolean doitStop = switch (direction) {
                    case "NORD" -> !feuVert && (y + LONG_VHL >= 340 - STOP_MARGIN) && (y + LONG_VHL <= 340 + STOP_MARGIN);
                    case "SUD" -> !feuVert && (y <= 460 + STOP_MARGIN) && (y >= 460 - STOP_MARGIN);
                    case "EST" -> !feuVert && (x <= 460 + STOP_MARGIN) && (x >= 460 - STOP_MARGIN);
                    case "OUEST" -> !feuVert && (x + LONG_VHL >= 340 - STOP_MARGIN) && (x + LONG_VHL <= 340 + STOP_MARGIN);
                    default -> false;
                };

                // Follow vehicle ahead
                boolean followVehicle = false;
                double followX = x, followY = y;
                for (Map.Entry<String, double[]> entry : TrafficApp.getVehiclePositions().entrySet()) {
                    if (!entry.getKey().equals(getLocalName())) {
                        double[] pos = entry.getValue();
                        String otherDir = TrafficApp.getVehicleDirections().getOrDefault(entry.getKey(), "");
                        if (otherDir.equals(direction)) {
                            if (sensor.isInRange(x, y, pos[0], pos[1])) {
                                boolean isAhead = switch (direction) {
                                    case "NORD" -> pos[1] > y;
                                    case "SUD" -> pos[1] < y;
                                    case "EST" -> pos[0] < x;
                                    case "OUEST" -> pos[0] > x;
                                    default -> false;
                                };
                                if (isAhead) {
                                    double dist = Math.sqrt(Math.pow(x - pos[0], 2) + Math.pow(y - pos[1], 2));
                                    if (dist < LONG_VHL + STOP_MARGIN) {
                                        followVehicle = true;
                                        followX = pos[0];
                                        followY = pos[1];
                                        System.out.println(getLocalName() + ": Suit un véhicule devant à distance=" + dist + ", pos=(" + followX + ", " + followY + ")");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                // Log conditions to diagnose stopping
                if (doitStop || followVehicle || stoppedAtObstacle) {
                    System.out.println(getLocalName() + ": Mouvement stoppé - doitStop=" + doitStop + ", followVehicle=" + followVehicle + ", stoppedAtObstacle=" + stoppedAtObstacle + ", x=" + x + ", y=" + y);
                }

                // Move or stay stopped
                if (!doitStop && !followVehicle && !stoppedAtObstacle) {
                    // Handle DEMI_TOUR
                    if (intentionDirection.equals("DEMI_TOUR")) {
                        if (demiTourPhase == 0 && directionInit.equals("NORD") && y >= 460) {
                            direction = "OUEST";
                            y = 440;
                            demiTourPhase = 1;
                        } else if (demiTourPhase == 1 && x >= 460) {
                            direction = "SUD";
                            x = 440;
                            demiTourPhase = 2;
                        }
                    }
                    // Handle turns (adjusted to y=355)
                    else if (!virageEffectue && intentionDirection.equals("TOURNER_DROITE")) {
                        switch (directionInit) {
                            case "NORD" -> {
                                if (y >= 380) {
                                    direction = "EST"; y = 360; virageEffectue = true;
                                }
                            }
                            case "SUD" -> {
                                if (y <= 420) {
                                    direction = "OUEST"; y = 440; virageEffectue = true;
                                }
                            }
                            case "EST" -> {
                                if (x <= 420) {
                                    direction = "SUD"; x = 440; virageEffectue = true;
                                }
                            }
                            case "OUEST" -> {
                                if (x >= 380) {
                                    direction = "NORD"; x = 360; virageEffectue = true;
                                }
                            }
                        }
                    } else if (!virageEffectue && intentionDirection.equals("TOURNER_GAUCHE")) {
                        System.out.println(getLocalName() + ": Vérification TOURNER_GAUCHE à y=" + y + ", virageEffectue=" + virageEffectue);
                        switch (directionInit) {
                            case "NORD" -> {
                                if (y >= 430) { // Changé de 460 à 355
                                    direction = "OUEST";
                                    y = 440; // Position ajustée après virage
                                    virageEffectue = true;
                                    System.out.println(getLocalName() + ": Tourner à gauche effectué à y=355.");
                                    // Réinitialiser l'état de l'obstacle après le virage
                                    obstacleDetected = false;
                                    obstacleX = -1;
                                    obstacleY = -1;
                                    stoppedAtObstacle = false;
                                    System.out.println(getLocalName() + ": État de l'obstacle réinitialisé après le virage.");
                                }
                            }
                            case "SUD" -> {
                                if (y <= 340) {
                                    direction = "EST"; y = 360; virageEffectue = true;
                                }
                            }
                            case "EST" -> {
                                if (x <= 340) {
                                    direction = "NORD"; x = 360; virageEffectue = true;
                                }
                            }
                            case "OUEST" -> {
                                if (x >= 460) {
                                    direction = "SUD"; x = 440; virageEffectue = true;
                                }
                            }
                        }
                    }

                    // Move based on direction
                    switch (direction) {
                        case "NORD" -> y += currentVitesse;
                        case "SUD" -> y -= currentVitesse;
                        case "EST" -> x -= currentVitesse;
                        case "OUEST" -> {
                            x += currentVitesse;
                            System.out.println(getLocalName() + ": Continue mouvement vers OUEST, x=" + x + ", y=" + y);
                        }
                    }
                } else if (followVehicle) {
                    switch (direction) {
                        case "NORD" -> y = followY - (LONG_VHL + STOP_MARGIN);
                        case "SUD" -> y = followY + (LONG_VHL + STOP_MARGIN);
                        case "EST" -> x = followX + (LONG_VHL + STOP_MARGIN);
                        case "OUEST" -> x = followX - (LONG_VHL + STOP_MARGIN);
                    }
                }

                TrafficApp.updateVehicle(getLocalName(), x, y, direction);
            }

            private void decideDirectionChange() {
                System.out.println(getLocalName() + ": decideDirectionChange appelé à y=" + y + ", intentionDirection=" + intentionDirection);
                if (obstacleDetected && direction.equals("NORD")) {
                    intentionDirection = "TOURNER_GAUCHE";
                    System.out.println(getLocalName() + ": Obstacle détecté, décision de tourner à gauche.");
                }
                System.out.println(getLocalName() + ": intentionDirection après décision = " + intentionDirection);
            }

            private void sendObstacleMessage() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                for (Map.Entry<String, double[]> entry : TrafficApp.getVehiclePositions().entrySet()) {
                    String receiverName = entry.getKey();
                    if (!receiverName.equals(getLocalName()) && receiverName.startsWith("Vehicle")) {
                        msg.addReceiver(new AID(receiverName, AID.ISLOCALNAME));
                    }
                }
                msg.setContent("OBSTACLE:" + obstacleX + ":" + obstacleY);
                send(msg);
                System.out.println(getLocalName() + ": Obstacle détecté à (" + obstacleX + ", " + obstacleY + "), message envoyé.");
            }

            private boolean hasExited() {
                return switch (direction) {
                    case "NORD" -> y > SIMULATION_BOUNDARY;
                    case "SUD" -> y < 0;
                    case "EST" -> x < 0;
                    case "OUEST" -> x > SIMULATION_BOUNDARY;
                    default -> false;
                };
>>>>>>> 6370002 (ajout de scenarios)
            }
        });
    }
}