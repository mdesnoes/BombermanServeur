package com.projet.strategy;

import com.projet.modele.Agent;
import com.projet.modele.AgentBomberman;
import com.projet.modele.AgentPNJ;
import com.projet.modele.BombermanGame;
import com.projet.modele.type.AgentAction;

public class TeteChercheuseStrategy implements Strategy {
		
	public RandomStrategy randomStrategy = new RandomStrategy();
	
	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {
		AgentAction action = AgentAction.STOP;
		int procheDist = 1000;
		Agent procheBomb = null;

		for (AgentBomberman i : bombermanGame.getListAgentsBomberman()) {
			if(i != agent) {
				int dist = Math.abs(agent.getPosX() - i.getPosX()) + Math.abs(agent.getPosY() - i.getPosY());

				if (dist < procheDist) {
					procheDist = dist;
					procheBomb = i;
				}
			}
		}
		
		//S'il n'y a plus de bomberman sur la carte, l'agent va en direction d'un PNJ
		if(procheBomb == null) {
			for (AgentPNJ i : bombermanGame.getListAgentsPNJ()) {
				if(i != agent) {
					int dist = Math.abs(agent.getPosX() - i.getPosX()) + Math.abs(agent.getPosY() - i.getPosY());
					if (dist < procheDist) {
						procheDist = dist;
						procheBomb = i;
					}
				}
			}		
		}
		
		if(procheBomb != null) { // condition pour verifier s'il y a bien un autre bomberman sur la map
			if (Math.abs(agent.getPosX() - procheBomb.getPosX()) >= Math.abs(agent.getPosY() - procheBomb.getPosY())) {
				if (agent.getPosX() < procheBomb.getPosX()) {
					action = AgentAction.MOVE_RIGHT;
				} else {
					action = AgentAction.MOVE_LEFT;
				}
			} else {
				if (agent.getPosY() < procheBomb.getPosY()) {
					action = AgentAction.MOVE_DOWN;
				} else {
					action = AgentAction.MOVE_UP;
				}
			}
		}
		
		// Si l'agent est bloqué par un mur, il faut une action random pour se debloquer
		if(!agent.isLegalMove(bombermanGame, action)) {
			return randomStrategy.chooseAction(bombermanGame, agent);
		}

		return action;
	}

}
