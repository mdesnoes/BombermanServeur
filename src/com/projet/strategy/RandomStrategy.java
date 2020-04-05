package com.projet.strategy;

import com.projet.modele.Agent;
import com.projet.modele.BombermanGame;
import com.projet.modele.type.AgentAction;

public class RandomStrategy implements Strategy {

	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {
		AgentAction[] tabAction = {AgentAction.MOVE_UP, AgentAction.MOVE_DOWN, AgentAction.MOVE_LEFT, AgentAction.MOVE_RIGHT, AgentAction.STOP, AgentAction.PUT_BOMB};
		int nbRandom = (int) (Math.random() * tabAction.length);
		return tabAction[nbRandom];
	}

}
