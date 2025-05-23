package com.abadou.utils;

public enum MessageType {
    DANGER,         // Signalement d'un obstacle ou d'un accident
    COORDINATION,   // Coordination de changement de voie ou d’intersection
    SIGNAL,         // Message d’un feu vers un véhicule (feu vert/rouge)
    STATUS,         // Mise à jour de position ou d’état
    COMMAND,        // Instructions du CoordinatorAgent
    RESPONSE        // Réponse à une commande
}
