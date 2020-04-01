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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.projetBomberman.modele.BombermanGame;
import com.projetBomberman.modele.info.ModeJeu;
import com.projetBomberman.strategy.BreakWallStrategy;
import com.projetBomberman.strategy.EsquiveStrategy;
import com.projetBomberman.strategy.PutBombStrategy;
import com.projetBomberman.strategy.RandomStrategy;
import com.projetBomberman.strategy.Strategy;
import com.projetProgReseau.entity.Partie;
import com.projetProgReseau.metier.PartieForm;
import com.projetProgReseau.view.Map;
import com.projetProgReseau.view.ViewConnexion;


public class Serveur implements Runnable {
	
	/* A passée en paramètre du client lorsque le serveur sera adapté pour tous les modes de jeu */
	private static final ModeJeu MODE_JEU = ModeJeu.SOLO;
	
	private static final String RANDOM_STRATEGY = "random";
	private static final String PUT_BOMB_STRATEGY = "put_bomb";
	private static final String BREAK_WALL_STRATEGY = "break_wall";
	private static final String ESQUIVE_STRATEGY = "esquive";
	
	
	private static final String MSG_DECO_CLIENT = "DECONNEXION";
	private static final String MSG_INIT_GAME = "INITIALISATION";
	private static final String MSG_PAUSE_GAME = "PAUSE";
	private static final String MSG_ETAPE_GAME = "ETAPE";
	private static final String MSG_DEBUT_GAME = "DEBUT";
	private static final String MSG_FIN_PARTIE = "FIN_PARTIE";
	private static final String SEP_FIN_PARTIE = ">";
	private static final String SEP_INFOS_PARTIE = ";";
	
	
	public Socket connexion;
	public List<Socket> listSockets = new ArrayList<Socket>();
	private BufferedReader entree;
	private DataOutputStream sortie;
	
	private String nomClient;
	BombermanGame game;
	
	
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
		String strategy;
		int maxturn;
		
		try {
			/* Recuperation du nom du client, du nombre de tour et de la strategy des agents */
			sortie.writeUTF(nomClient);
			System.out.println("[SERVEUR] Connexion de " + nomClient);
			maxturn = Integer.parseInt( entree.readLine() );
			strategy = entree.readLine();
			Strategy agentStrategy = initStrategyAgent(strategy);
			
			/* Recuperation de la map initiale */
			Map map = null;
    		ObjectMapper mapper = new ObjectMapper();
			try {
				map = mapper.readValue(entree.readLine(), Map.class);
			} catch (Exception e) {
				System.out.println("ERREUR Map non trouvé");
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("Map recu avec succès");
			
			
			/* Creation du l'etat du jeu initial */
			game = new BombermanGame(nomClient, MODE_JEU, agentStrategy, maxturn, map);
			envoyerEtatJeu();
			
			
			while(true) {				
				ch = entree.readLine();
				
				if(ch != null) {
					
					if(ch.endsWith( MSG_DECO_CLIENT )) {
						listSockets.remove(connexion);
					} else {
						if(ch.contains( MSG_INIT_GAME )) {
							game.initialize_game();
						} else if(ch.contains( MSG_PAUSE_GAME )) {
							game.stop();
						} else if(ch.contains( MSG_DEBUT_GAME )) {
							game.launch();
						} else if(ch.contains( MSG_ETAPE_GAME )) {
							game.step();					
						}
						
						envoyerEtatJeu();
					}
					
					
					System.out.println(ch);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			terminer();
		}
	}
	
	private void envoyerEtatJeu() throws IOException {
		String gameJson = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			gameJson = mapper.writeValueAsString(game);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		sortie.writeUTF(gameJson);
		System.out.println("BombermanGame envoyé avec succès");
	}
	
	
	private Strategy initStrategyAgent(String strategyAgent) {
		switch(strategyAgent) {
			case RANDOM_STRATEGY: return new RandomStrategy();
			case PUT_BOMB_STRATEGY: return new PutBombStrategy();
			case BREAK_WALL_STRATEGY: return new BreakWallStrategy();
			case ESQUIVE_STRATEGY: return new EsquiveStrategy();
			default: return new RandomStrategy();
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
		
		if (argu.length == 1) {
			try {
				Socket so;
				int port = Integer.parseInt(argu[0]);
				ServerSocket ecoute = new ServerSocket(port);
				List<Socket> listeClients = new ArrayList<Socket>();
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
