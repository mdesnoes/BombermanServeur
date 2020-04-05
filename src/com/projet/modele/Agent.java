package com.projet.modele;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projet.modele.type.AgentAction;
import com.projet.modele.type.ColorAgent;
import com.projet.strategy.Strategy;

public abstract class Agent {

	private static int compteur = 0;
	@JsonIgnore private int id;
	private int posX;
	private int posY;
	private ColorAgent color;
	protected AgentAction action;
	private char type;
	@JsonIgnore protected Strategy strategy;
	
	public Agent(int pos_x, int pos_y, char type, ColorAgent color, Strategy strategy) {
		this.posX = pos_x;
		this.posY = pos_y;
		this.type = type;
		this.color = color;
		this.strategy = strategy;
		this.id = Agent.compteur;
		Agent.compteur++;
	}
	

	public abstract boolean isInvincible();
	public abstract boolean canPutBomb();
	public abstract void executer(BombermanGame bombermanGame);
	public abstract boolean isLegalMove(BombermanGame bombGame, AgentAction action);
	public abstract void moveAgent(AgentAction action);

	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public ColorAgent getColor() {
		return color;
	}
	public void setColor(ColorAgent color) {
		this.color = color;
	}
	public AgentAction getAction() {
		return action;
	}
	public void setAction(AgentAction action) {
		this.action = action;
	}
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public Strategy getStrategy() {
		return strategy;
	}
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

}
