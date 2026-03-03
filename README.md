Projet Java : Application de Chat Multi-utilisateurs (Sockets & JSON)
📝 Présentation du Projet
Ce projet consiste en la création d'une application de messagerie instantanée en réseau basée sur une architecture Client/Serveur. L'application permet à plusieurs utilisateurs de se connecter simultanément à un serveur central pour échanger des messages en temps réel, soit de manière publique, soit via des messages privés.

🏗️ Architecture du Code
Pour garantir la maintenabilité et la clarté du code, le projet est découpé en trois packages distincts, respectant une logique de séparation des responsabilités :

com.projet.common : Regroupe les ressources partagées.

Modèle Message : Objet pivot transportant l'expéditeur, le contenu, le destinataire et le type de message (CHAT, CONNECT, LIST_UPDATE).

Utilitaire JSONUtils : Gère la sérialisation et la désérialisation via la bibliothèque GSON.

com.projet.server : Cœur logique du service.

SocketServer : Gère l'ouverture du port (1234) et l'acceptation des nouvelles connexions.

ClientHandler : Implémente Runnable pour gérer chaque client dans un Thread dédié, permettant ainsi le multi-threading.

com.projet.client : Interface utilisateur et communication.

ChatClient : Gère la socket côté client.

ChatWindow (Swing) : Interface graphique avancée utilisant un JTextPane pour un affichage stylisé et une JList dynamique pour la gestion des utilisateurs connectés.

🚀 Technologies et Bibliothèques
Utilisation de Google GSON (gson.jar)
Le choix a été fait d'utiliser la bibliothèque GSON pour la communication entre le client et le serveur.

Pourquoi ? Plutôt que d'envoyer des chaînes de caractères brutes difficiles à parser, nous transformons l'objet Message en format JSON. Cela sécurise l'échange de données et facilite l'ajout futur de nouvelles fonctionnalités (comme l'envoi de fichiers ou d'emojis).

Installation : Le fichier gson-2.10.1.jar doit être ajouté au Build Path du projet (Propriétés du projet > Java Build Path > Libraries > Add External JARs).

Gestion du Multi-threading et de l'EDT
Serveur : Chaque client est isolé dans un thread pour éviter de bloquer l'écoute de nouvelles connexions.

Client : Utilisation systématique de SwingUtilities.invokeLater() pour mettre à jour l'interface graphique (GUI) à partir du thread d'écoute, respectant ainsi les contraintes de l'Event Dispatch Thread de Java Swing.

✨ Fonctionnalités implémentées
Identification immédiate : Dès la connexion, le client envoie un message technique de type CONNECT pour s'enregistrer auprès du serveur.

Liste d'utilisateurs dynamique : Le serveur diffuse (broadcast) la liste mise à jour des pseudos à chaque nouvelle connexion ou déconnexion.

Messages Privés : En sélectionnant un utilisateur dans la liste de gauche, le message est envoyé exclusivement à ce destinataire.

Interface Moderne : Thème sombre pour la liste des utilisateurs et gestion des styles (gras/italique) dans l'historique de chat.

🛠️ Installation et Lancement
Cloner le dépôt : git clone [URL_DU_REPO]

Configurer le JAR : Assurez-vous que le fichier gson.jar est bien référencé dans votre IDE.

Lancer le Serveur : Exécutez la classe com.projet.server.core.SocketServer.

Lancer les Clients : Exécutez la classe com.projet.client.AppLauncher pour ouvrir autant de fenêtres de chat que souhaité.
[!TIP]
Si vous souhaitez tester les messages privés, il suffit de cliquer sur un pseudo dans la liste de gauche avant d'envoyer votre message. Pour revenir en mode public, sélectionnez "Tous".


👨‍💻 Auteur
Ndiaye Cyril Arnaud / Ndione Marie Hélène Emma Suzanne  - Étudiants en Licence 3 Genie Logiciel.
