package com.projet.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cette classe est le contrat de communication entre le client et le serveur.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    // Types de messages possibles (pour savoir quoi faire à la réception)
    public enum MessageType {
        CONNECT, CHAT, LIST_UPDATE
    }

    private String expediteur;
    private String contenu;
    private String destinataire; // "Tous" ou le pseudo de quelqu'un
    private MessageType type;
    private String timestamp;
    
    // Constructeur par défaut (nécessaire pour la sérialisation JSON)
    public Message() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // Constructeur pratique pour envoyer un message rapide
    public Message(String expediteur, String contenu, String destinataire, MessageType type) {
        this();
        this.expediteur = expediteur;
        this.contenu = contenu;
        this.destinataire = destinataire;
        this.type = type;
    }

    // Getters et Setters (indispensables pour que GSON puisse lire les données)
    public String getExpediteur() { return expediteur; }
    public void setExpediteur(String expediteur) { this.expediteur = expediteur; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getDestinataire() { return destinataire; }

    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + expediteur + ": " + contenu;
    }
}