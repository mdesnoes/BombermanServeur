package com.projetBomberman.modele;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projetBomberman.modele.info.AgentAction;
import com.projetBomberman.modele.info.ColorAgent;
import com.projetBomberman.modele.info.StateBomb;
import com.projetBomberman.strategy.Strategy;

public class AgentBomberman extends Agent {

	@JsonIgnore private ArrayList<Bombe> listBombes = new ArrayList<>();
	@JsonIgnore private int nbBombe = 1;
	private boolean isInvincible = false;
	private boolean isSick = false;
	@JsonIgnore private int nbTurnBonusInvincible = 0;
	@JsonIgnore private int nbTurnMalusSick = 0;
	private int rangeBomb = 2;

	public AgentBomberman(int pos_x, int pos_y, char type, ColorAgent color, Strategy strategy) {
		super(pos_x,pos_y, type, color, strategy);
	}
	
	public void executer(BombermanGame bombermanGame) {

		AgentAction action = this.getStrategy().chooseAction(bombermanGame, this);
		this.setAction(action);
		
		if(action == AgentAction.PUT_BOMB) {
			Bombe bombe = new Bombe(this.getPosX(), this.getPosY(), this.getRangeBomb(), StateBomb.Step1);
			bombermanGame.addListBombs(bombe);
			this.addBombe(bombe);
		}
		else if(action == AgentAction.MOVE_UP || action == AgentAction.MOVE_DOWN 
				|| action == AgentAction.MOVE_LEFT || action == AgentAction.MOVE_RIGHT) {
			if(this.isLegalMove(bombermanGame, action)) {
				this.moveAgent(action);
				
				for(Item item : bombermanGame.getListItems()) {
					if(this.getPosX() == item.getPosX() && this.getPosY() == item.getPosY()) {
						this.takeItem(item);
						bombermanGame.addListItemUtilise(item);
					}
				}
			}
		}
	}
	
	public boolean isLegalMove(BombermanGame bombGame, AgentAction action) {
		int newX = this.getPosX();
    	int newY = this.getPosY();

    	switch(action) {
			case MOVE_UP:
				newY--;
				break;
			case MOVE_DOWN:
				newY++;
				break;
			case MOVE_LEFT:
				newX--;
				break;
			case MOVE_RIGHT:
				newX++;
				break;
			default:
				break;
    	}
    	
    	//On verifie si l'agent sort de la map ou non
    	if(!bombGame.appartientMap(newX,  newY)) {
    		return false;
    	}
    	
    	//On verifie s'il y a un mur, un mur cassable ou une bombe sur la nouvelle case
    	if(bombGame.getMap().getWalls()[newX][newY] || bombGame.getListBreakableWalls()[newX][newY] 
    		|| bombGame.getBombByCoord(newX, newY) != null) {
    		return false;
    	}
    	
    	 //Un agent bomberman ne peut pas se deplacer sur un autre agent
		return bombGame.getAgentByCoord(newX, newY) == null;
	}

	public void moveAgent(AgentAction action) {
		switch(action) {
			case MOVE_UP:
				this.setPosY(this.getPosY() - 1);
				break;
			case MOVE_DOWN:
				this.setPosY(this.getPosY() + 1);
				break;
			case MOVE_LEFT:
				this.setPosX(this.getPosX() - 1);
				break;
			case MOVE_RIGHT:
				this.setPosX(this.getPosX() + 1);
				break;
			default:
				break;
		}
	}
	
	
	private void takeItem(Item item) {
		switch(item.getType()) {
		case FIRE_UP:
			this.rangeBomb++;
			
			//On met a jour les bombes déjà poser sur le terrain
			for(Bombe bombAgent : this.listBombes) {
				bombAgent.setRange(this.rangeBomb);
			}

			break;
		case FIRE_DOWN:
			this.rangeBomb--;

			//On met a jour les bombes déjà poser sur le terrain
			for(Bombe bombAgent : this.listBombes) {
				bombAgent.setRange(this.rangeBomb);
			}

			break;	
		case BOMB_UP:
			this.nbBombe++;
			break;
		case BOMB_DOWN:
			this.nbBombe --;
			break;
		case FIRE_SUIT:
			this.isInvincible = true;
			break;
		case SKULL:
			this.isSick = true;
			break;
		}
	}

	private void addBombe(Bombe bomb) {
		this.listBombes.add(bomb);
	}
	
	void removeBombe(Bombe bomb) {
		this.listBombes.remove(bomb);
	}
	
	public boolean canPutBomb() {
		if(this.isSick) {
			return false;
		}

		return this.listBombes.size() < this.nbBombe;
	}

	
	
	public ArrayList<Bombe> getListBombes() {
		return listBombes;
	}
	public void setListBombes(ArrayList<Bombe> listBombes) {
		this.listBombes = listBombes;
	}
	public int getNbBombe() {
		return nbBombe;
	}
	public void setNbBombe(int nbBombe) {
		this.nbBombe = nbBombe;
	}
	public boolean isInvincible() {
		return isInvincible;
	}
	public void setInvincible(boolean isInvincible) {
		this.isInvincible = isInvincible;
	}
	public boolean isSick() {
		return isSick;
	}
	public void setSick(boolean isSick) {
		this.isSick = isSick;
	}
	public int getNbTurnBonusInvincible() {
		return nbTurnBonusInvincible;
	}
	public void setNbTurnBonusInvincible(int nbTurnBonusInvincible) {
		this.nbTurnBonusInvincible = nbTurnBonusInvincible;
	}
	public int getNbTurnMalusSick() {
		return nbTurnMalusSick;
	}
	public void setNbTurnMalusSick(int nbTurnMalusSick) {
		this.nbTurnMalusSick = nbTurnMalusSick;
	}
	public int getRangeBomb() {
		return rangeBomb;
	}
	public void setRangeBomb(int rangeBomb) {
		this.rangeBomb = rangeBomb;
	}
	
}
