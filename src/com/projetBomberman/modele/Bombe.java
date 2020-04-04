package com.projetBomberman.modele;

import com.projetBomberman.modele.info.ItemType;
import com.projetBomberman.modele.info.StateBomb;

public class Bombe {
	
	private static final int PROBABILITE_OBJET = 6;
	private static final int VAL_OBJET_APPARITION = 1;
	private static final ItemType[] TAB_ITEM = {ItemType.FIRE_UP,ItemType.FIRE_DOWN,ItemType.BOMB_UP,ItemType.BOMB_DOWN,ItemType.FIRE_SUIT,ItemType.SKULL}; 
	
	private int posX;
	private int posY;
	private int range;
	private StateBomb stateBomb;

	Bombe(int x, int y, int range, StateBomb stateBomb) {
		this.posX = x;
		this.posY = y;
		this.range = range;
		this.stateBomb = stateBomb;
	}
	
	
	public void explosion(BombermanGame bombermanGame) {
		AgentBomberman agentBomberman = bombermanGame.getAgentBombermanByBomb(this);
		
		//Si agentBomberman != null, cela veut dire que la bombe appartient à un agent
		if(agentBomberman != null) {
			if(this.stateBomb == StateBomb.Boom) {					
				this.destroyBreakableWall(bombermanGame); // On detruit les murs cassables
				this.destroyAgent(agentBomberman, bombermanGame); // On detruit les autres agents
				bombermanGame.addListBombeDetruite(this); //Bombe à supprimer de la liste
				agentBomberman.removeBombe(this); //L'agent peut reposer une nouvelle bombe
			}
			else {
				//Nouvelle etat des bombes de l'agent
				for(Bombe bomb : agentBomberman.getListBombes()) {
					bomb.changeStateBomb();
				}
			}
		}
		// Sinon cela veut dire que la bombe n'a plus de propriétaire (l'agent à été detruit avant que ça bombe n'explose) 
		else {
			if(this.getStateBomb() == StateBomb.Boom) {
				this.destroyBreakableWall(bombermanGame);
				this.destroyAgent(null, bombermanGame);
				bombermanGame.addListBombeDetruite(this); //Bombe à supprimer de la liste
			}
			else {
				this.changeStateBomb();
			}
		}
	}
	
	private void destroyBreakableWall(BombermanGame bombermanGame) {
		//Sur la ligne 
		for(int i=this.posX - this.range; i<=this.posX + this.range; ++i) {
			if(bombermanGame.appartientMap(i, this.posY)) { // On verifie que les coordonnées appartiennent à la map
				if(bombermanGame.getListBreakableWalls()[i][this.posY]) { // On regarde si il y a un mur au coordonnées courante
					bombermanGame.getListBreakableWalls()[i][this.posY] = false;
					
					//Probabilité qu'un item apparaisse, tout les items on la meme probabilité
					int nb = (int) (Math.random() * PROBABILITE_OBJET);

					if(nb == VAL_OBJET_APPARITION) {
						int nbRandom = (int) (Math.random() * TAB_ITEM.length);
						Item item = new Item(i, this.posY, TAB_ITEM[nbRandom]);
						bombermanGame.addListItems(item);
					}
				}
			}
		}
		
		//Sur la colonne
		for(int i=this.posY - this.range; i<=this.posY + this.range; ++i) {
			if(bombermanGame.appartientMap(this.posX, i)) {
				if(bombermanGame.getListBreakableWalls()[this.posX][i]) {
					bombermanGame.getListBreakableWalls()[this.posX][i] = false;

					//Probabilité qu'un item apparaisse, tout les items on la meme probabilité
					int nb = (int) (Math.random() * PROBABILITE_OBJET);

					if(nb == VAL_OBJET_APPARITION) {
						int nbRandom = (int) (Math.random() * TAB_ITEM.length);
						Item item = new Item(this.posX, i, TAB_ITEM[nbRandom]);
						bombermanGame.addListItems(item);
					}
				}
			}
		}
	}
	

	private void destroyAgent(AgentBomberman agentProprietaireBomb, BombermanGame bombermanGame) {
		//Sur la ligne 
		for(int i=this.posX - this.range; i<=this.posX + this.range; ++i) {
			Agent agent = bombermanGame.getAgentByCoord(i, this.posY);

			if(agent != null && agent != agentProprietaireBomb) { // Si on trouve un agent different de celui qui a posé la bombe
				if(!agent.isInvincible()) {

					bombermanGame.addListAgentDetruit(agent);
				}
			}
		}
		
		//Sur la colonne
		for(int i=this.posY - this.range; i<=this.posY + this.range; ++i) {
			Agent agent = bombermanGame.getAgentByCoord(this.posX, i);

			if(agent != null && agent != agentProprietaireBomb) {
				if(!agent.isInvincible()) {

					bombermanGame.addListAgentDetruit(agent);
				}
			}
		}
	}
	
	
	
	private void changeStateBomb() {
		switch(this.stateBomb) {
			case Step1: this.stateBomb = StateBomb.Step2; break;
			case Step2: this.stateBomb = StateBomb.Step3; break;
			case Step3: this.stateBomb = StateBomb.Boom; break;
			case Boom: break;
		}
	}


	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public StateBomb getStateBomb() {
		return stateBomb;
	}
	public void setStateBomb(StateBomb stateBomb) {
		this.stateBomb = stateBomb;
	}


}
