package com.projet.client.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.projet.common.model.Message;
import com.projet.common.utils.JSONUtils;

public class ChatClient {

    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    /**
     * Tente de se connecter au serveur
     */
    public void connect() throws IOException {
        System.out.println("Tentative de connexion au serveur...");
        socket = new Socket(serverAddress, serverPort);
        
        // On prépare les tuyaux
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        System.out.println("Connecté !");
    }

    /**
     * Envoie un objet Message au serveur (transformé en JSON)
     */
    public void sendMessage(Message message) {
        if (out != null) {
            String json = JSONUtils.serialize(message);
            out.println(json); // Envoi dans le tuyau
            System.out.println("Envoi : " + json); // Log pour nous aider
        }
    }

    /**
     * Cette méthode écoute ce que le serveur raconte (on l'utilisera dans un Thread plus tard)
     */
    public Message receiveMessage() throws IOException {
        String json = in.readLine();
        if (json != null) {
            return JSONUtils.deserialize(json);
        }
        return null;
    }
    
    // Fermeture propre
    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}