package com.projet.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.projet.common.model.Message;
import com.projet.common.model.Message.MessageType;

public class SocketServer {
    private int port;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private ServerWindow gui; // L'interface graphique du serveur
    
    // Liste synchronisée pour éviter les erreurs si deux clients se connectent en même temps
    private List<ClientHandler> clients = new ArrayList<>();

    public SocketServer(int port) {
        this.port = port;
        // 1. On initialise l'interface graphique
        this.gui = new ServerWindow();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            gui.addLog("Serveur démarré sur le port " + port);

            // On lance l'écoute dans un Thread séparé pour que la fenêtre reste fluide
            new Thread(() -> {
                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        // On crée le handler (le serveur se passe lui-même via 'this')
                        ClientHandler handler = new ClientHandler(clientSocket, this);
                        
                        // On ajoute le client à la liste
                        synchronized (clients) {
                            clients.add(handler);
                        }

                        new Thread(handler).start();
                    } catch (IOException e) {
                        if (isRunning) gui.addLog("Erreur d'acceptation : " + e.getMessage());
                    }
                }
            }).start();

        } catch (IOException e) {
            gui.addLog("Impossible de démarrer le serveur : " + e.getMessage());
        }
    }

    /**
     * Gère la diffusion des messages (Public ou Privé)
     */
    public void broadcast(Message msg) {
        synchronized (clients) {
            if ("Tous".equals(msg.getDestinataire())) {
                // MESSAGE PUBLIC
                for (ClientHandler client : clients) {
                    client.sendMessage(msg);
                }
            } else {
                // MESSAGE PRIVÉ
                for (ClientHandler client : clients) {
                    // On n'envoie qu'au destinataire et à l'expéditeur
                    if (client.getClientName().equals(msg.getDestinataire()) || 
                        client.getClientName().equals(msg.getExpediteur())) {
                        client.sendMessage(msg);
                    }
                }
            }
        }
    }
    
    /**
     * Envoie la liste de tous les pseudos connectés à tous les clients
     */
    public void broadcastUserList() {
        StringBuilder sb = new StringBuilder();
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (!client.getClientName().equals("Inconnu")) {
                    sb.append(client.getClientName()).append(",");
                }
            }
        }
        
        Message listMsg = new Message("Système", sb.toString(), "Tous", MessageType.LIST_UPDATE);
        broadcast(listMsg);
    }

    /**
     * Retire un client et met à jour l'interface
     */
    public void removeClient(ClientHandler client, String name) {
        synchronized (clients) {
            clients.remove(client);
        }
        gui.addLog("[" + name + "] déconnecté. Clients restants : " + clients.size());
    }

    // Getter pour permettre aux handlers d'écrire dans les logs
    public ServerWindow getGui() {
        return gui;
    }

    public static void main(String[] args) {
        // Lancement du serveur sur le port 1234
        SocketServer server = new SocketServer(1234);
        server.start();
    }
}