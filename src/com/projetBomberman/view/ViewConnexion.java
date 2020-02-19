package com.projetBomberman.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.projetBomberman.controller.ControllerBombermanGame;
import com.projetProgReseau.client.Client;
import com.projetProgReseau.metier.ConnexionForm;


public class ViewConnexion extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
    
	public ViewConnexion(ControllerBombermanGame controllerBombGame) {
		this.createView(controllerBombGame);
	}
	
	private void createView(ControllerBombermanGame controllerBombGame) {
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
			
			ConnexionForm form = new ConnexionForm(fieldPseudo.getText(), fieldPassword.getText());
			
			if(form.verifConnexion()) {
				System.out.println("Connexion acceptée");
				Client c = new Client(controllerBombGame, fieldPseudo.getText()+","+fieldPseudo.getText());
				Thread t = new Thread(c);
				t.start();
				setVisible(false);
			} else {
				labelErreur.setText("<html>Nom d'utilisateur ou mot de passe introuvable<br/>Reessayer ou creer un compte</html>");
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
