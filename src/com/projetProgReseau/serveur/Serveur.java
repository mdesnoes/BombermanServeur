package com.projetProgReseau.serveur;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.projetBomberman.modele.BombermanGame;
import com.projetBomberman.modele.Map;
import com.projetBomberman.modele.info.ModeJeu;
import com.projetBomberman.strategy.BreakWallStrategy;
import com.projetBomberman.strategy.EsquiveStrategy;
import com.projetBomberman.strategy.PutBombStrategy;
import com.projetBomberman.strategy.RandomStrategy;
import com.projetBomberman.strategy.Strategy;
import com.projetProgReseau.metier.UtilisateurForm;


public class Serveur implements Runnable {
	
	private static final String MODE_NORMAL = "normal";
	private static final String MODE_SOLO = "solo";
	private static final String MODE_DUO = "duo";
	
	private static final String RANDOM_STRATEGY = "random";
	private static final String PUT_BOMB_STRATEGY = "put_bomb";
	private static final String BREAK_WALL_STRATEGY = "break_wall";
	private static final String ESQUIVE_STRATEGY = "esquive";
	
	private static final String MSG_DECO_CLIENT = "DECONNEXION";
	private static final String MSG_INIT_GAME = "INITIALISATION";
	private static final String MSG_PAUSE_GAME = "PAUSE";
	private static final String MSG_ETAPE_GAME = "ETAPE";
	private static final String MSG_DEBUT_GAME = "DEBUT";
	private static final String REGEX_TIME = "[0-9]*";
	
	private static final String CONNEXION_OK = "Connexion acceptee";
	private static final String CONNEXION_NOK = "Connexion refusee";
	
	
	public Socket connexion;
	public List<Socket> listSockets = new ArrayList<Socket>();
	private BufferedReader entree;
	private DataOutputStream sortie;
	
	private String nomClient;
	private BombermanGame game;
	
	
	public Serveur(Socket s, List<Socket> listSockets){
		this.connexion=s;
		this.listSockets = listSockets;

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
		String mode;
		int maxturn;
		String mdp;
		
		try {
			
			/* Verification de la connexion du compte */
			this.nomClient = entree.readLine();
			mdp = entree.readLine();
			while(!connexion(mdp)) {
				System.out.println("[SERVEUR] Connexion refusée pour " + nomClient);
				
				this.sortie.writeUTF( CONNEXION_NOK );

				this.nomClient = entree.readLine();
				mdp = entree.readLine();
			}
			
			this.sortie.writeUTF( CONNEXION_OK );
			System.out.println("[SERVEUR] Connexion acceptée pour " + nomClient);
			
			/* Recuperation de la configuration du jeu : mode de jeu, nombre de tour et strategie des agents */
			mode = entree.readLine();
			ModeJeu modeJeu = initModeJeu(mode);
			maxturn = Integer.parseInt( entree.readLine() );
			strategy = entree.readLine();
			Strategy agentStrategy = initStrategyAgent(strategy);
			
			/* Recuperation de la map initiale */
			Map map = null;
    		ObjectMapper mapper = new ObjectMapper();
			try {
				map = mapper.readValue(entree.readLine(), Map.class);
			} catch (Exception e) {
				System.out.println("[SERVEUR] [ERREUR] erreur : lors de l'envoie de la récupération de la map initiale");
				e.printStackTrace();
				terminer();
			}
			
			
			/* Creation du l'etat du jeu initial */
			game = new BombermanGame(Serveur.this, nomClient, modeJeu, agentStrategy, maxturn, map);
			envoyerEtatJeu();
			
			
			while(true) {				
				ch = entree.readLine();
				
				if(ch != null) {
					
					if(ch.endsWith( MSG_DECO_CLIENT )) { /* Deconnexion du client */
						System.out.println("[CLIENT " + nomClient + " > SERVEUR] Deconnexion de " + nomClient);
						terminer();
						break;
					} else if(ch.matches( REGEX_TIME )) { /* Modification de la vitesse du jeu */
						System.out.println("[CLIENT " + nomClient + " > SERVEUR] Modification de la vitesse de la partie");
						game.setTime( Long.parseLong(ch) );
					} else { /* Action du client pour modifier l'etat du jeu */
						
						switch(ch) {
						case MSG_INIT_GAME:
							System.out.println("[CLIENT " + nomClient + " > SERVEUR] Initialisation de la partie");
							game.init();
							break;
						case MSG_PAUSE_GAME:
							System.out.println("[CLIENT " + nomClient + " > SERVEUR] Pause de la partie");
							game.stop();
							break;
						case MSG_DEBUT_GAME:
							System.out.println("[CLIENT " + nomClient + " > SERVEUR] Lancement de la partie");
							game.launch();
							break;
						case MSG_ETAPE_GAME:
							System.out.println("[CLIENT " + nomClient + " > SERVEUR] Nouvelle etape de la partie");
							game.step();
							break;
						}
						
						envoyerEtatJeu();
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			terminer();
		}
	}
	
	public boolean connexion(String mdp) throws IOException {
		UtilisateurForm form = new UtilisateurForm();
		return form.verifConnexion(this.nomClient, mdp);
	}
	
	public void envoyerEtatJeu() throws IOException {
		String gameJson = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			gameJson = mapper.writeValueAsString(game);
		} catch (JsonProcessingException e1) {
			System.out.println("[SERVEUR] [ERREUR] erreur : lors de l'envoie de l'etat du jeu");
			e1.printStackTrace();
			terminer();
		}
		sortie.writeUTF(gameJson);
	}
	
	
	private ModeJeu initModeJeu(String strategyAgent) {
		switch(strategyAgent) {
			case MODE_NORMAL: return ModeJeu.NORMAL;
			case MODE_SOLO: return ModeJeu.SOLO;
			case MODE_DUO: return ModeJeu.DUO;
			default: return ModeJeu.NORMAL;
		}
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
			listSockets.remove(this.connexion);
	        this.connexion.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	public DataOutputStream getSortie() {
		return sortie;
	}
	public void setSortie(DataOutputStream sortie) {
		this.sortie = sortie;
	}

}
