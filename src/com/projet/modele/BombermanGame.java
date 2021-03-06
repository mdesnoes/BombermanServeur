package com.projet.modele;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projet.entity.Partie;
import com.projet.factory.AgentFactory;
import com.projet.factory.FactoryProvider;
import com.projet.metier.PartieForm;
import com.projet.modele.type.ColorAgent;
import com.projet.modele.type.ModeJeu;
import com.projet.serveur.Serveur;
import com.projet.strategy.*;


public class BombermanGame extends Game {

	private static final int TURN_MAX_ITEM = 5;
	private static final char BOMBERMAN = 'B';
	private static final String MSG_FIN_PARTIE = "FIN_PARTIE";
	private static final String SEP_MSG_FIN_PARTIE = ">";
	private static final String SEP_DONNEES_FIN_PARTIE = ";";
	
	@JsonIgnore private ModeJeu mode;
    private ArrayList<AgentBomberman> listAgentsBomberman;
    private ArrayList<AgentPNJ> listAgentsPNJ;
	private boolean[][] listBreakableWalls;
	private ArrayList<Bombe> listBombs;
	private ArrayList<Item> listItems;
	@JsonIgnore private ArrayList<Agent> listAgentsDetruit = new ArrayList<>();
	@JsonIgnore private ArrayList<Bombe> listBombesDetruite = new ArrayList<>();
	@JsonIgnore private ArrayList<Item> listItemsUtilise = new ArrayList<>();
	@JsonIgnore private Strategy agentStrategy;
	
	@JsonIgnore private Timestamp dateDebutPartie;
	@JsonIgnore private String nomJoueur;
	@JsonIgnore private AgentBomberman bombermanJoueur1;
	@JsonIgnore private AgentBomberman bombermanJoueur2;
	@JsonIgnore private DataOutputStream sortie;
	@JsonIgnore private Map map;
	
	public BombermanGame(Serveur serveur, String nom, ModeJeu mode, Strategy agentStrategy, int maxturn, Map map) {
		super(serveur, maxturn);
		this.sortie = serveur.getSortie();
		this.nomJoueur = nom;
		this.mode = mode;
		this.agentStrategy = agentStrategy;
		this.map = map;
	}

	public void initialize_game() {
		/* Date de debut de la partie */
		Date dateCourante = new Date();
		this.dateDebutPartie = new Timestamp(dateCourante.getTime());

		this.listAgentsBomberman = new ArrayList<>();
		this.listAgentsPNJ = new ArrayList<>();
		this.listBombs = new ArrayList<>();
		this.listItems = new ArrayList<>();
		
		/* Copie profonde du tableau des murs cassables pour pouvoir les faire réaparaitre quand on réinitialise le jeu */
		boolean[][] _startBreakableWalls = this.map.getStart_breakable_walls();
		int x = 0;
		int y = 0;
		this.listBreakableWalls = new boolean[_startBreakableWalls.length][_startBreakableWalls[x].length];
		for (boolean[] tab : _startBreakableWalls) {
			for (boolean b : tab) {
				this.listBreakableWalls[x][y] = b;
				++y;
			}
			++x;
			y=0;
		}
		
		/* Creation des agents */
		ArrayList<InfoAgent> listAgentInit = this.map.getStart_agents();
		for(InfoAgent agent : listAgentInit) {
			AgentFactory agentFactory = FactoryProvider.getFactory(agent.getType());

			if(agent.getType() == BOMBERMAN) {
			    this.listAgentsBomberman.add((AgentBomberman) agentFactory.createAgent(agent.getX(), agent.getY(), agent.getType(), this.agentStrategy));
			}
			else {
		    	this.listAgentsPNJ.add((AgentPNJ) agentFactory.createAgent(agent.getX(), agent.getY(), agent.getType(), null));
			}
		}
		
		/* Initialisation des agents controlé par le client */
		/* En mode solo, on contrôle le premier agent */
		if(this.mode == ModeJeu.SOLO) {
			this.bombermanJoueur1 = this.listAgentsBomberman.get(0);
		} /* En mode duo, on peut controler les deux premiers agents (avec des touches différentes) */
		else if(this.mode == ModeJeu.DUO) {
			this.bombermanJoueur1 = this.listAgentsBomberman.get(0);
			if(this.listAgentsBomberman.size() > 1) {
				this.bombermanJoueur2 = this.listAgentsBomberman.get(1);
			}
		}
	}

	public void takeTurn() {
		this.listAgentsDetruit = new ArrayList<>();
		this.listBombesDetruite = new ArrayList<>();
		this.listItemsUtilise = new ArrayList<>();
		
		/* Action des agents bomberman */
		for (AgentBomberman agentBomberman: this.listAgentsBomberman) {
			//Verification des malus/bonus des bomberman
			if(agentBomberman.isInvincible()) {
				//Au bout de 5 tours, l'agent n'est plus invincible
				if(agentBomberman.getNbTurnBonusInvincible() >= TURN_MAX_ITEM) {
					agentBomberman.setInvincible(false);
					agentBomberman.setNbTurnBonusInvincible(0);
				} else {
					agentBomberman.setNbTurnBonusInvincible(agentBomberman.getNbTurnBonusInvincible() + 1);
				}
			}

			if(agentBomberman.isSick()) {
				//Au bout de 5 tours, l'agent n'est plus malade
				if(agentBomberman.getNbTurnMalusSick() >= TURN_MAX_ITEM) {
					agentBomberman.setSick(false);
					agentBomberman.setNbTurnMalusSick(0);
				} else {
					agentBomberman.setNbTurnMalusSick(agentBomberman.getNbTurnMalusSick() + 1);
				}
			}

			/* On effectue l'action du bomberman */
			agentBomberman.executer(this);
		}

		/* On supprime les items ramassés par les agents */
		for(Item item : this.listItemsUtilise) {
			this.listItems.remove(item);
		}

		/* Actions des PNJ survivants */
		for(AgentPNJ agentPNJ : this.listAgentsPNJ) {
			agentPNJ.executer(this);
		}

		/* Explosion des bombes */
		for(Bombe bomb : this.listBombs) {
			bomb.explosion(this);
		}

		/* On supprime les agents qui ont été detruit suite à l'explosion des bombes */
		for(Agent agent : this.listAgentsDetruit) {
			if(agent instanceof AgentBomberman) {
				this.listAgentsBomberman.remove(agent);
			}
			else if(agent instanceof AgentPNJ){
				this.listAgentsPNJ.remove(agent);
			}
		}

		/* On supprime les bombes qui ont explosé */
		for(Bombe bomb : this.listBombesDetruite) {
			this.listBombs.remove(bomb);
		}
   	}

	


	public boolean gameContinue() {
		if(this.listAgentsBomberman.size() == 1) {
			if(this.listAgentsPNJ.size() > 0) {
				return true;
			}
			return false;
		}
		if(this.listAgentsBomberman.isEmpty()) {
			return false;
		}
		return true;
	}
	

	public void gameOver() {
		System.out.println("[SERVEUR] Fin de la partie du client " + this.nomJoueur);
		
		String vainqueur;
		if(this.getTurn() >= this.getMaxturn()) {
			try {
				this.sortie.writeUTF(MSG_FIN_PARTIE + SEP_MSG_FIN_PARTIE + "Pas de gagnant" + SEP_DONNEES_FIN_PARTIE + "black");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if(this.listAgentsBomberman.size() <= 0) {
				vainqueur = "PNJ";
	
				try {
					this.sortie.writeUTF(MSG_FIN_PARTIE + SEP_MSG_FIN_PARTIE + "PNJ" + SEP_DONNEES_FIN_PARTIE + "black");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				AgentBomberman bombermanVainqueur = this.listAgentsBomberman.get(0);
				if(bombermanVainqueur == this.bombermanJoueur1) {
					vainqueur = this.nomJoueur;
				} else {
					vainqueur = "IA";
				}
				
				String color = colorAgentToColor(bombermanVainqueur.getColor());
				try {
					this.sortie.writeUTF(MSG_FIN_PARTIE + SEP_MSG_FIN_PARTIE 
							+ bombermanVainqueur.getColor().toString() + SEP_DONNEES_FIN_PARTIE + color);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
			/* Enregistrement de la partie pour exploitation avec JEE */
			Partie partie = new Partie();
			partie.setDateDebut(dateDebutPartie);
			partie.setVainqueur(vainqueur);
			PartieForm form = new PartieForm();
			form.enregistrerPartie(partie);
		}
	}
	
	public String colorAgentToColor(ColorAgent colorAgent) {
		switch(colorAgent) {
			case BLEU: return "blue";
			case ROUGE: return "red";
			case VERT: return "green";
			case JAUNE: return "yellow";
			case BLANC: return "white";
			default: return "";
		}
	}
	
	   
	
	// retourne vrai si les coordonnées appartient à la map
	boolean appartientMap(int x, int y) {
		return x >=0 && x <= this.map.getSizeX()-1
				&& y>=0 && y <= this.map.getSizeY()-1;
	}
		
		
	public Bombe getBombByCoord(int x, int y) {
		for(Bombe bomb : this.listBombs) {
			if(bomb.getPosX() == x && bomb.getPosY() == y) {
				return bomb;
			}
		}
		return null;
	}
	// Recupere l'agent PNJ en fonction des coordonnées passées en paramètre
	public AgentPNJ getAgentPNJByCoord(int x, int y) {
		for(AgentPNJ agent : this.listAgentsPNJ) {
			if(agent.getPosX() == x && agent.getPosY() == y) {
				return agent;
			}
		}
		return null;
	}
	// Recupere l'agent Bomberman en fonction des coordonnées passées en paramètre
	public AgentBomberman getAgentBombermanByCoord(int x, int y) {
		for(AgentBomberman agent : this.listAgentsBomberman) {
			if(agent.getPosX() == x && agent.getPosY() == y) {
				return agent;
			}
		}
		return null;
	}
	// Recupere l'agent en fonction des coordonnées passées en paramètre
	public Agent getAgentByCoord(int x, int y) {
		AgentBomberman agentBomberman = this.getAgentBombermanByCoord(x, y);
		AgentPNJ agentPNJ = this.getAgentPNJByCoord(x, y);

		if(agentBomberman != null) {
			return agentBomberman;
		} else {
			return agentPNJ;
		}
	}
	// Recupere l'agent Bomberman en fonction de la bombe passée en paramètre
	public AgentBomberman getAgentBombermanByBomb(Bombe bomb) {
		for(AgentBomberman agent : this.listAgentsBomberman) {
			for(Bombe bombAgent : agent.getListBombes()) {
				if(bombAgent == bomb) {
					return agent;
				}
			}
		}
		return null;
	}
	
	
	
	
	public void addListBombs(Bombe bomb) {
		this.listBombs.add(bomb);
	}
	public void addListItems(Item item) {
		this.listItems.add(item);
	}
	public void addListItemUtilise(Item item) {
		this.listItemsUtilise.add(item);
	}
	public void addListBombeDetruite(Bombe bomb) {
		this.listBombesDetruite.add(bomb);
	}
	public void addListAgentDetruit(Agent agent) {
		this.listAgentsDetruit.add(agent);
	}
	public void removeAgentBomberman(AgentBomberman bomberman) {
	    this.listAgentsBomberman.remove(bomberman);
	}
	
	
	
	public ModeJeu getMode() {
		return mode;
	}
	public void setMode(ModeJeu mode) {
		this.mode = mode;
	}
	public ArrayList<AgentBomberman> getListAgentsBomberman() {
		return listAgentsBomberman;
	}
	public void setListAgentsBomberman(ArrayList<AgentBomberman> listAgentsBomberman) {
		this.listAgentsBomberman = listAgentsBomberman;
	}
	public ArrayList<AgentPNJ> getListAgentsPNJ() {
		return listAgentsPNJ;
	}
	public void setListAgentsPNJ(ArrayList<AgentPNJ> listAgentsPNJ) {
		this.listAgentsPNJ = listAgentsPNJ;
	}
	public boolean[][] getListBreakableWalls() {
		return listBreakableWalls;
	}
	public void setListBreakableWalls(boolean[][] listBreakableWalls) {
		this.listBreakableWalls = listBreakableWalls;
	}
	public ArrayList<Bombe> getListBombs() {
		return listBombs;
	}
	public void setListBombs(ArrayList<Bombe> listBombs) {
		this.listBombs = listBombs;
	}
	public ArrayList<Item> getListItems() {
		return listItems;
	}
	public void setListItems(ArrayList<Item> listItems) {
		this.listItems = listItems;
	}
	public ArrayList<Agent> getListAgentsDetruit() {
		return listAgentsDetruit;
	}
	public void setListAgentsDetruit(ArrayList<Agent> listAgentsDetruit) {
		this.listAgentsDetruit = listAgentsDetruit;
	}
	public ArrayList<Bombe> getListBombesDetruite() {
		return listBombesDetruite;
	}
	public void setListBombesDetruite(ArrayList<Bombe> listBombesDetruite) {
		this.listBombesDetruite = listBombesDetruite;
	}
	public ArrayList<Item> getListItemsUtilise() {
		return listItemsUtilise;
	}
	public void setListItemsUtilise(ArrayList<Item> listItemsUtilise) {
		this.listItemsUtilise = listItemsUtilise;
	}
	public Strategy getAgentStrategy() {
		return agentStrategy;
	}
	public void setAgentStrategy(Strategy agentStrategy) {
		this.agentStrategy = agentStrategy;
	}
	public Timestamp getDateDebutPartie() {
		return dateDebutPartie;
	}
	public void setDateDebutPartie(Timestamp dateDebutPartie) {
		this.dateDebutPartie = dateDebutPartie;
	}
	public String getNomJoueur() {
		return nomJoueur;
	}
	public void setNomJoueur(String nomJoueur) {
		this.nomJoueur = nomJoueur;
	}
	public AgentBomberman getBombermanJoueur1() {
		return bombermanJoueur1;
	}
	public void setBombermanJoueur1(AgentBomberman bombermanJoueur1) {
		this.bombermanJoueur1 = bombermanJoueur1;
	}
	public AgentBomberman getBombermanJoueur2() {
		return bombermanJoueur2;
	}
	public void setBombermanJoueur2(AgentBomberman bombermanJoueur2) {
		this.bombermanJoueur2 = bombermanJoueur2;
	}
	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		this.map = map;
	}

}
