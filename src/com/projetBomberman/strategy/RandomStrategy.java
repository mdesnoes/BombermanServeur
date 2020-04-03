package com.projetBomberman.strategy;

import com.projetBomberman.modele.Agent;
import com.projetBomberman.modele.BombermanGame;
import com.projetBomberman.modele.info.AgentAction;

public class RandomStrategy implements Strategy {

	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {
		AgentAction[] tabAction = {AgentAction.MOVE_UP, AgentAction.MOVE_DOWN, AgentAction.MOVE_LEFT, AgentAction.MOVE_RIGHT, AgentAction.STOP, AgentAction.PUT_BOMB};
		int nbRandom = (int) (Math.random() * tabAction.length);
		return tabAction[nbRandom];
	}

}
