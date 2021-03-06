package com.projet.serveur;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.projet.metier.UtilisateurForm;
import com.projet.modele.BombermanGame;
import com.projet.modele.Map;
import com.projet.modele.type.AgentAction;
import com.projet.modele.type.ModeJeu;
import com.projet.strategy.BreakWallStrategy;
import com.projet.strategy.EsquiveStrategy;
import com.projet.strategy.PutBombStrategy;
import com.projet.strategy.RandomStrategy;
import com.projet.strategy.Strategy;


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
	private static final String MSG_MODIF_TIME = "TIME";
	
	private static final String CONNEXION_OK = "Connexion acceptee";
	
	private static final String MSG_DEJA_CONNECTE = "<html>Vous êtes déjà connecté !<html>";
	private static final String MSG_AUTH_INCORRECTE = "<html>Nom d'utilisateur ou mot de passe introuvable<br/>Reessayer ou creer un compte</html>";

	
	private Socket connexion;
	private List<Socket> listSockets;
	private BufferedReader entree;
	private DataOutputStream sortie;
	
	private String nomClient;
	private BombermanGame game;
	private String msgErreurConnexion;
	
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
		
		try {
			/* Verification de la connexion du compte */
			this.nomClient = entree.readLine();
			String mdp = entree.readLine();
			while(!connexionAccepte(mdp)) {
				System.out.println("[SERVEUR] Connexion refusée pour " + nomClient);
				
				this.sortie.writeUTF( msgErreurConnexion );

				this.nomClient = entree.readLine();
				mdp = entree.readLine();
			}
			
			Thread.currentThread().setName(this.nomClient);
			this.sortie.writeUTF( CONNEXION_OK );
			System.out.println("[SERVEUR] Connexion acceptée pour " + nomClient);
			

			/* Recuperation de la configuration du jeu : mode de jeu, nombre de tour et strategie des agents */
			String mode = entree.readLine();
			ModeJeu modeJeu = initModeJeu(mode);
			int maxturn = Integer.parseInt( entree.readLine() );
			String strategy = entree.readLine();
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
					if(ch.startsWith( MSG_DECO_CLIENT )) { /* Deconnexion du client */
						System.out.println("[CLIENT " + nomClient + " > SERVEUR] Deconnexion de " + nomClient);
						terminer();
						break;
					} else if(ch.startsWith( MSG_MODIF_TIME )) { /* Modification de la vitesse du jeu */
						String newTime = ch.substring( MSG_MODIF_TIME.length() );
						System.out.println("[CLIENT " + nomClient + " > SERVEUR] Modification de la vitesse de la partie : " + newTime + " ms" );
						game.setTime( Long.parseLong(newTime) );
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
							default:
								/* L'utilisateur a appuyé sur une touche du clavier */
								if( game.getMode() == ModeJeu.SOLO || game.getMode() == ModeJeu.DUO ) {
									actionClavier(ch);
								}
								break;
						}
						
						envoyerEtatJeu();
					}
				}
			}
		} catch (IOException e) {
			System.out.println("[SERVEUR] [ERREUR] La connexion avec le client + " + nomClient + " a été perdue");
			terminer();
		}
	}
	
	private void actionClavier(String ch) {
		int key = Integer.parseInt(ch);
		
		System.out.println("[CLIENT " + this.nomClient + " > SERVEUR] Action clavier - touche " + KeyEvent.getKeyText(key) );
		
		switch(key) {
			case KeyEvent.VK_Z: game.getBombermanJoueur1().setAction(AgentAction.MOVE_UP); break;
			case KeyEvent.VK_Q: game.getBombermanJoueur1().setAction(AgentAction.MOVE_LEFT); break;
			case KeyEvent.VK_D: game.getBombermanJoueur1().setAction(AgentAction.MOVE_RIGHT); break;
			case KeyEvent.VK_S: game.getBombermanJoueur1().setAction(AgentAction.MOVE_DOWN); break;
			case KeyEvent.VK_F: game.getBombermanJoueur1().setAction(AgentAction.PUT_BOMB); break;
			default: game.getBombermanJoueur1().setAction(AgentAction.STOP); break;
        }
		
		/* En mode duo, on detecte egalement les touches du joueur 2 */
		if(game.getMode() == ModeJeu.DUO) {
			switch(key) {
				case KeyEvent.VK_UP: game.getBombermanJoueur2().setAction(AgentAction.MOVE_UP); break;
				case KeyEvent.VK_LEFT: game.getBombermanJoueur2().setAction(AgentAction.MOVE_LEFT); break;
				case KeyEvent.VK_RIGHT: game.getBombermanJoueur2().setAction(AgentAction.MOVE_RIGHT); break;
				case KeyEvent.VK_DOWN: game.getBombermanJoueur2().setAction(AgentAction.MOVE_DOWN); break;
				case KeyEvent.VK_NUMPAD0: game.getBombermanJoueur2().setAction(AgentAction.PUT_BOMB); break;
				default: game.getBombermanJoueur2().setAction(AgentAction.STOP); break;
			}
		}
	}

	
	private boolean connexionAccepte(String mdp) throws IOException {
		UtilisateurForm form = new UtilisateurForm();
		
		/* Verification du pseudo et du mot de passe avec le JEE */
		if(!form.verifConnexion(this.nomClient, mdp)) {
			this.msgErreurConnexion = MSG_AUTH_INCORRECTE;
			return false;
		} else {
			/* Verification que le client n'est pas déjà connecté sur le serveur */
			int nbThreadCourant = Thread.activeCount();
			Thread th[] = new Thread[ nbThreadCourant ];
		    Thread.enumerate(th);
		    for (int i = 0; i < nbThreadCourant; i++) {
		    	if( th[i].getName().equals(this.nomClient) ) {
		    		this.msgErreurConnexion = MSG_DEJA_CONNECTE;
		    		return false;
		    	}
		    }
			return true;
		}
		
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
