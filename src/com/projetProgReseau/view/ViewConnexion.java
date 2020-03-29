package com.projetProgReseau.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.net.Socket;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.projetProgReseau.metier.UtilisateurForm;
import com.projetProgReseau.serveur.Serveur;


public class ViewConnexion extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final String MSG_ERREUR = "<html>Nom d'utilisateur ou mot de passe introuvable<br/>Reessayer ou creer un compte</html>";
	
    Socket so;
    List<Socket> listeClients;
    
	public ViewConnexion(Socket so, List<Socket> listeClients) {
		this.so = so;
		this.listeClients = listeClients;
		this.createView();
	}
	
	private void createView() {
		this.setTitle("Veuillez vous connecter");
		this.setSize(new Dimension(500,250));
		Dimension windowSize = getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2;
		setLocation(dx,dy);
	
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new GridLayout(5,1));
		
		// Label de bienvenue
		JLabel labelBienvenue = new JLabel("Bienvenue sur Bomberman !", JLabel.CENTER);
		labelBienvenue.setFont(new Font("Arial", Font.BOLD, 26));
		panelPrincipal.add(labelBienvenue);
		
		// Lebl d'erreur de connexion
		JLabel labelErreur = new JLabel("", JLabel.CENTER);
		labelErreur.setFont(new Font("Arial", Font.BOLD, 15));
		labelErreur.setForeground(Color.RED);
		panelPrincipal.add(labelErreur);
		
		// Pseudo
		JPanel panelPseudo = new JPanel();
		panelPseudo.setLayout(new GridLayout(1,2));
		
		panelPseudo.add(new JLabel("Nom d'utilisateur : ", JLabel.CENTER));
		JTextField fieldPseudo = new JTextField();
		panelPseudo.add(fieldPseudo);
		
		panelPrincipal.add(panelPseudo);
		
		// Mot de passe
		JPanel panelPassword = new JPanel();
		panelPassword.setLayout(new GridLayout(1,2));
		
		panelPassword.add(new JLabel("Mot de passe : ", JLabel.CENTER));
		JPasswordField fieldPassword = new JPasswordField();
		panelPassword.add(fieldPassword);
		
		panelPrincipal.add(panelPassword);
		
		// Bouton connexion et annuler
		JPanel panelButton = new JPanel();
		JButton buttonConnexion = new JButton("Connexion");
		JButton buttonAnnuler = new JButton("Annuler");
		
		panelButton.add(buttonConnexion);															
		panelButton.add(buttonAnnuler);

		buttonConnexion.addActionListener(evenement -> {
			
			UtilisateurForm form = new UtilisateurForm();
			if(form.verifConnexion(fieldPseudo.getText(), fieldPassword.getText())) {
				System.out.println("Connexion acceptée");
				
				listeClients.add(so);
				System.out.println(listeClients);
				
				Serveur serv = new Serveur(so, listeClients, fieldPseudo.getText());
				Thread t = new Thread(serv);
				t.start();
				
				setVisible(false);
			} else {
				labelErreur.setText(MSG_ERREUR);
				System.out.println("Connexion refusée");
			}
		});
		
		buttonAnnuler.addActionListener(evenement -> {
			setVisible(false);
		});
		panelPrincipal.add(panelButton);
		
		setContentPane(panelPrincipal);
		setVisible(true);
 	}

}
