package com.projet.server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.projet.common.model.Message;
import com.projet.common.utils.JSONUtils;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private SocketServer server; 
    private BufferedReader in;
    private PrintWriter out;
    private String clientName = "Inconnu";

    public ClientHandler(Socket socket, SocketServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
        	server.getGui().addLog("Tentative de connexion depuis : " + clientSocket.getInetAddress());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String jsonInput;
            while ((jsonInput = in.readLine()) != null) {
                Message messageRecu = JSONUtils.deserialize(jsonInput);
                
                // 1. SI C'EST UN MESSAGE DE CONNEXION (L'identification invisible)
                if (messageRecu.getType() == Message.MessageType.CONNECT) {
                    
                    // On enregistre le vrai nom du client
                    this.clientName = messageRecu.getExpediteur(); 
                    
                    // On affiche sur la belle console du serveur
                    server.getGui().addLog(clientName + " est maintenant en ligne.");
                   
                    // C'est ça qui déclenche la méthode écrite dans SocketServer
                    server.broadcastUserList(); 
                    
                } 
                // 2. SI C'EST UN MESSAGE DE CHAT NORMAL
                else if (messageRecu.getType() == Message.MessageType.CHAT) {
                    server.getGui().addLog("[" + messageRecu.getExpediteur() + " -> " + messageRecu.getDestinataire() + "] : " + messageRecu.getContenu());
                    server.broadcast(messageRecu);
                }
            }

        } catch (IOException e) {
            // La déconnexion est gérée dans le block 'finally'
        } finally {
            closeConnection();
        }
    }

    /**
     * Nettoie les ressources et prévient le serveur du départ du client
     */
    private void closeConnection() {
        try {
            // 1. On retire le client de la liste du serveur
            server.removeClient(this, clientName);
            
            // 2. On prévient l'interface du serveur
            server.getGui().addLog(clientName + " a quitté le chat.");

            // 3. On demande au serveur de renvoyer la liste actualisée aux autres
            server.broadcastUserList(); 

            // 4. Fermeture propre du socket
            if (clientSocket != null) clientSocket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Envoie un message JSON à ce client spécifique
     */
    public void sendMessage(Message message) {
        String json = JSONUtils.serialize(message);
        out.println(json);
    }
    
    public String getClientName() {
        return this.clientName;
    }
}