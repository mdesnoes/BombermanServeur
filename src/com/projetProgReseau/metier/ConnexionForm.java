package com.projetProgReseau.metier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetProgReseau.entity.Utilisateur;

public class ConnexionForm {
	
	private static final MotDePasseEncryptor mdpEncryptor = MotDePasseEncryptor.getInstance();

	private String pseudo;
	private String password;
	
	public ConnexionForm(String pseudo, String password) {
		this.pseudo = pseudo;
		this.password = password;		
	}
	
	private Utilisateur getUtilisateurUsingREST() {
		Utilisateur u = null;
		
		if(!this.pseudo.isEmpty()) {
			// Requete GET sur l'API REST du projet JEE
			BufferedReader in = null;
			try {
				URL url = new URL("http://localhost:8080/SiteWebBomberman/api/utilisateur/" + this.pseudo);
				in = new BufferedReader(new InputStreamReader(url.openStream()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// On convertit le resultat de la requete ( au format JSON ) en Utilisateur ( Objet JAVA )
			ObjectMapper mapper = new ObjectMapper();
			try {
				u = mapper.readValue(in.readLine(), Utilisateur.class);
			} catch (Exception e) {
				System.out.println("Nom d'utilisateur non trouvé");
			}
		}
				
		return u;
	}
	
	// Renvoie vrai si le nom d'utilisateur et le mot de passe sont trouvé dans la base de donnée
	public boolean verifConnexion() {
		Utilisateur u = getUtilisateurUsingREST();
		
		if(u != null) {
			String passwordDecrypte = mdpEncryptor.decrypter(u.getPassword());
			return u.getPseudo().equals(this.pseudo) && passwordDecrypte.equals(this.password);
		}
		return false;
	}

}
