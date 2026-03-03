package com.projet.client;

import javax.swing.JOptionPane;
import com.projet.client.core.ChatClient;
import com.projet.client.view.ChatWindow; // Importation du nouveau package

public class AppLauncher {
    public static void main(String[] args) {
        try {
            String pseudo = JOptionPane.showInputDialog(null, "Choisissez un pseudo :", "Connexion", JOptionPane.PLAIN_MESSAGE);
            
            if (pseudo == null || pseudo.trim().isEmpty()) return;

            ChatClient client = new ChatClient("localhost", 1234);
            client.connect();

            // Lancement de la VueHeHeh
            ChatWindow window = new ChatWindow(pseudo, client);
            window.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur de connexion : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}