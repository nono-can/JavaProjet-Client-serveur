package com.projet.client.view;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import com.projet.client.core.ChatClient;
import com.projet.common.model.Message;

public class ChatWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextPane chatPane; 
    private StyledDocument doc;
    private JTextField messageField;
    private JButton sendButton;
    private ChatClient client;
    private String pseudo;
    private JList<String> userList;
    private DefaultListModel<String> listModel;

    public ChatWindow(String pseudo, ChatClient client) {
        this.pseudo = pseudo;
        this.client = client;
        
        setTitle("Chat Étudiant - " + pseudo);
        setSize(600, 600); // Fenêtre un peu plus large pour laisser de la place à la liste
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        startListening();

        // Envoi du message d'identification (doit correspondre au CONNECT du ClientHandler)
        Message msgIdent = new Message(pseudo, "Connexion initiale", "Serveur", Message.MessageType.CONNECT);
        client.sendMessage(msgIdent);
    }

    private void initializeUI() {
        // --- 1. CONFIGURATION DU PANNEAU LATÉRAL (LISTE) ---
        listModel = new DefaultListModel<>();
        listModel.addElement("Tous"); 
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setSelectedIndex(0);
        
        userList.setBackground(new Color(40, 44, 52));
        userList.setForeground(Color.WHITE);
        userList.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane listScrollPane = new JScrollPane(userList);
        // On force la largeur de la colonne de gauche (150 pixels)
        listScrollPane.setPreferredSize(new Dimension(150, 0));
        listScrollPane.setMinimumSize(new Dimension(150, 0));
        listScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.DARK_GRAY));

        // --- 2. ZONE D'AFFICHAGE (CENTRE) ---
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        doc = chatPane.getStyledDocument();
        chatPane.setMargin(new Insets(10, 10, 10, 10));
        chatPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatPane.setBackground(new Color(250, 250, 250));

        JScrollPane chatScrollPane = new JScrollPane(chatPane);

        // --- 3. PANNEAU DE SAISIE (BAS) ---
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        messageField = new JTextField();
        sendButton = new JButton("Envoyer");
        
        southPanel.add(messageField, BorderLayout.CENTER);
        southPanel.add(sendButton, BorderLayout.EAST);

        // --- 4. ASSEMBLAGE FINAL ---
        this.setLayout(new BorderLayout());
        this.add(listScrollPane, BorderLayout.WEST);   // La liste à gauche
        this.add(chatScrollPane, BorderLayout.CENTER); // Le chat au milieu
        this.add(southPanel, BorderLayout.SOUTH);      // La saisie en bas

        // Événements
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }
    
    private void appendText(String text, Color color, boolean isBold, boolean isItalic) {
        Style style = chatPane.addStyle("MyStyle", null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, isBold);
        StyleConstants.setItalic(style, isItalic);

        try {
            doc.insertString(doc.getLength(), text, style);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String texte = messageField.getText().trim();
        if (!texte.isEmpty()) {
            String destinataire = userList.getSelectedValue(); 
            if (destinataire == null) destinataire = "Tous";

            Message msg = new Message(pseudo, texte, destinataire, Message.MessageType.CHAT);
            client.sendMessage(msg);
            messageField.setText("");
        }
    }

    private void startListening() {
        Thread listener = new Thread(() -> {
            try {
                while (true) {
                    Message msg = client.receiveMessage();
                    if (msg != null) {
                        SwingUtilities.invokeLater(() -> {
                            if (msg.getType() == Message.MessageType.LIST_UPDATE) {
                                listModel.clear();
                                listModel.addElement("Tous");
                                String contenu = msg.getContenu();
                                if (contenu != null && !contenu.isEmpty()) {
                                    String[] users = contenu.split(",");
                                    for (String user : users) {
                                        if (!user.trim().isEmpty() && !user.equals(this.pseudo)) {
                                            listModel.addElement(user.trim());
                                        }
                                    }
                                }
                                return;
                            }
                            
                            // Affichage des messages
                            appendText("[" + msg.getTimestamp() + "] ", new Color(100, 110, 130), false, false);
                            
                            if (msg.getExpediteur().equals("Système")) {
                                appendText(msg.getExpediteur() + " : " + msg.getContenu() + "\n", new Color(198, 120, 221), false, true);
                            } else if (msg.getExpediteur().equals(this.pseudo)) {
                                String prefix = msg.getDestinataire().equals("Tous") ? " (Moi) : " : " (Moi -> " + msg.getDestinataire() + ") : ";
                                appendText(msg.getExpediteur() + prefix, new Color(97, 175, 239), true, false);
                                appendText(msg.getContenu() + "\n", Color.BLACK, false, false);
                            } else {
                                String prefix = msg.getDestinataire().equals("Tous") ? " : " : " (Privé) : ";
                                appendText(msg.getExpediteur() + prefix, new Color(152, 195, 121), true, false);
                                appendText(msg.getContenu() + "\n", Color.BLACK, false, false);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> appendText("Connexion perdue.\n", Color.RED, true, false));
            }
        });
        listener.setDaemon(true);
        listener.start();
    }
}