package View;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Controller.ControllerBombermanGame;
import client.Client;


public class ViewConnexion extends JFrame {
	

	public ViewConnexion(ControllerBombermanGame controllerBombGame) {
		this.createView(controllerBombGame);
	}
	
	private void createView(ControllerBombermanGame controllerBombGame) {
		this.setTitle("Connexion");
		this.setSize(new Dimension(500,200));
		Dimension windowSize = getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2;
		setLocation(dx,dy);
	
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new GridLayout(4,1));
		
		// Lable de bienvenue
		JLabel labelBienvenue = new JLabel("Bienvenue sur le jeu Bomberman !", JLabel.CENTER);
		panelPrincipal.add(labelBienvenue);
		
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
			Client c = new Client(controllerBombGame, fieldPseudo.getText()+","+fieldPseudo.getText());
			Thread t = new Thread(c);
			t.start();
			setVisible(false);
		});
		
		buttonAnnuler.addActionListener(evenement -> {
			setVisible(false);
		});
		panelPrincipal.add(panelButton);
		
		setContentPane(panelPrincipal);
		setVisible(true);
 	}

}
