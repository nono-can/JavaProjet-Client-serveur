package com.projet.server.core;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerWindow extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea logArea;
    private JLabel statusLabel;
    private SimpleDateFormat timeFormat;

    public ServerWindow() {
        // Configuration de base de la fenêtre
        setTitle("Console de Contrôle - Serveur de Chat");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran

        // Initialisation du format d'heure pour les logs
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        // --- Création de la zone de logs ---
        logArea = new JTextArea();
        logArea.setEditable(false); // On ne peut pas écrire dedans à la main
        logArea.setBackground(new Color(20, 20, 20)); // Fond presque noir
        logArea.setForeground(new Color(50, 255, 50)); // Texte vert fluo (Style Matrix)
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setMargin(new Insets(10, 10, 10, 10)); // Marges intérieures
        
        // Ajout d'un scroll automatique
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // --- Barre de statut en bas ---
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setPreferredSize(new Dimension(this.getWidth(), 30));
        statusPanel.setBackground(new Color(45, 45, 45));
        
        statusLabel = new JLabel("  Etat du serveur : En attente de démarrage...");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        // Rendre la fenêtre visible
        setVisible(true);
    }

    /**
     * Ajoute un message dans la console du serveur avec l'heure actuelle.
     * Cette méthode est "Thread-Safe" (utilisable depuis n'importe quel ClientHandler).
     */
    public void addLog(String message) {
        String timestamp = timeFormat.format(new Date());
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + timestamp + "] " + message + "\n");
            
            // Scroll automatique vers le bas à chaque nouveau log
            logArea.setCaretPosition(logArea.getDocument().getLength());
            
            // Mise à jour rapide du statut si c'est une connexion/déconnexion
            if (message.contains("démarré")) {
                statusLabel.setText("  Etat du serveur : EN LIGNE");
                statusLabel.setForeground(new Color(100, 255, 100));
            }
        });
    }
}