package com.projet.modele;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projet.modele.type.AgentAction;
import com.projet.modele.type.ColorAgent;
import com.projet.strategy.Strategy;


public abstract class AgentPNJ extends Agent {

	public AgentPNJ(int pos_x, int pos_y, char type, ColorAgent color, Strategy strategy) {
		super(pos_x, pos_y, type, color, strategy);
	}
	
	public void executer(BombermanGame bombermanGame) {
		AgentAction action = this.getStrategy().chooseAction(bombermanGame, this);
		this.setAction(action);
		
		if(action == AgentAction.MOVE_UP || action == AgentAction.MOVE_DOWN 
				|| action == AgentAction.MOVE_LEFT || action == AgentAction.MOVE_RIGHT) {
			if(this.isLegalMove(bombermanGame, action)) {
				this.moveAgent(action);
				AgentBomberman agentBomberman = bombermanGame.getAgentBombermanByCoord(this.getPosX(), this.getPosY());

				if(agentBomberman != null) {
					bombermanGame.removeAgentBomberman(agentBomberman);
				}
			}
		}
	}

	public void moveAgent(AgentAction action) {
		switch(action) {
			case MOVE_UP: this.setPosY(this.getPosY() - 1); break;
			case MOVE_DOWN: this.setPosY(this.getPosY() + 1); break;
			case MOVE_LEFT: this.setPosX(this.getPosX() - 1); break;
			case MOVE_RIGHT: this.setPosX(this.getPosX() + 1); break;
			default: break;
		}
	}
	
	public boolean isLegalMove(BombermanGame bombGame, AgentAction action) {
		int newX = this.getPosX();
    	int newY = this.getPosY();
    	
    	switch(action) {
			case MOVE_UP: newY--; break;
			case MOVE_DOWN: newY++; break;
			case MOVE_LEFT: newX--; break;
			case MOVE_RIGHT: newX++; break;
			default: break;
    	}
    	
    	//On verifie si l'agent sort de la map ou non
    	if(!bombGame.appartientMap(newX,newY)) {
    		return false;
    	}
    	
    	//On verifie s'il y a un mur, un mur cassable ou une bombe sur la nouvelle case
    	if(bombGame.getMap().getWalls()[newX][newY] || bombGame.getListBreakableWalls()[newX][newY]
    			|| bombGame.getBombByCoord(newX, newY) != null) {
    		return false;
    	}
    	
    	//Un agent PNJ ne peut pas se deplacer sur un autre agent PNJ
		return bombGame.getAgentPNJByCoord(newX, newY) == null;
	}
	
	public boolean canPutBomb() {
		return false;
	}
	
	@JsonIgnore
	public boolean isInvincible() {
		return false;
	}

}
