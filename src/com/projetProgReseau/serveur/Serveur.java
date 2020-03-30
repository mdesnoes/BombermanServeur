package com.projetProgReseau.serveur;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.projetProgReseau.entity.Partie;
import com.projetProgReseau.metier.PartieForm;
import com.projetProgReseau.view.ViewConnexion;

public class Serveur implements Runnable {
	
	private static final String MSG_DECO_CLIENT = "DECONNEXION";
	private static final String MSG_FIN_PARTIE = "FIN_PARTIE";
	private static final String SEP_FIN_PARTIE = ">";
	private static final String SEP_INFOS_PARTIE = ";";
	
	
	public Socket connexion;
	public List<Socket> listSockets = new ArrayList<Socket>();
	private BufferedReader entree;
	private DataOutputStream sortie;
	
	private String nomClient;
	
	
	public Serveur(Socket s, List<Socket> listSockets, String nom){
		this.connexion=s;
		this.listSockets = listSockets;
		this.nomClient = nom;

		try {
			this.entree = new BufferedReader(new InputStreamReader(this.connexion.getInputStream()));
			this.sortie = new DataOutputStream(this.connexion.getOutputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String ch;
		
		try {
			sortie.writeUTF(nomClient);
			System.out.println("[SERVEUR] Connexion de " + nomClient);
			notifyAllClient("[SERVEUR] Connexion de " + nomClient);
			
			while(true) {				
				ch = entree.readLine();
				
				if(ch != null) {
					
					if(ch.endsWith(MSG_DECO_CLIENT)) {
						listSockets.remove(connexion);
					}
					/* Enregistrement des parties pour exploitation avec JEE */
					else if(ch.startsWith(MSG_FIN_PARTIE)) {
						
						ch = ch.substring(ch.indexOf( SEP_FIN_PARTIE ) + 1);
						String strDateDebut = ch.split( SEP_INFOS_PARTIE )[0];
						Timestamp dateDebut = Timestamp.valueOf(strDateDebut);
						String vainqueur = ch.split( SEP_INFOS_PARTIE )[1];
						
						Partie partie = new Partie();
						partie.setDateDebut(dateDebut);
						partie.setVainqueur(vainqueur);
						PartieForm form = new PartieForm();
						form.enregistrerPartie(partie);
	
					}
					
					System.out.println(ch);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			terminer();
		}
	}
	
	private void notifyAllClient(String ch) {
		try {
			for(Socket so : this.listSockets) {
				if(so != this.connexion && !so.isClosed()) {
					DataOutputStream sortieSocket = new DataOutputStream (so.getOutputStream());
					sortieSocket.writeUTF(ch);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			terminer();
		}
	}
	
	private void terminer() {
		try{
	        if(this.connexion != null) {
	        	this.connexion.close();
	        }
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void main(String[] argu) {
		int p; // le port d’écoute
		ServerSocket ecoute;
		Socket so;

		List<Socket> listeClients = new ArrayList<Socket>();
		
		if (argu.length == 1) {
			try {
				p=Integer.parseInt(argu[0]);
				ecoute = new ServerSocket(p);
				System.out.println("serveur mis en place ");
				
				while (true) {
					so = ecoute.accept();
					
					/* Connexion d'un client */
					new ViewConnexion(so, listeClients);
				}

			} catch (IOException e) { 
				System.out.println("problème\n"+e.getMessage());
			}
		} else { 
			System.out.println("syntaxe d’appel java Serveur port\n");
		} 
	}
}
