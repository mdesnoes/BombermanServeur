package com.projetBomberman.strategy;

import com.projetBomberman.modele.*;
import com.projetBomberman.modele.info.AgentAction;
import com.projetBomberman.modele.info.StateBomb;

// Stratégie pour les Bombermans :
// Quand un bomberman voit qu'il est aligné avec une bombe au stade 3, il se déplace sur un des cotés pour esquiver si c'est possible
// Quand l'agent voit qu'un ennemy n'est pas loin, il fuit dans le sens opposé
// Si l'agent ne peut pas esquivé ou n'a pas besoin d'esquiver, il renvoie l'action STOP

public class EsquiveStrategy implements Strategy {

	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {
		int RANGE_BOMBE_DEFAULT = 2;

		for(int i = agent.getPosX() - RANGE_BOMBE_DEFAULT; i < agent.getPosX() + RANGE_BOMBE_DEFAULT; ++i) {
			Bombe bombe = bombermanGame.getBombByCoord(i, agent.getPosY());

			if(bombe != null) { // On regarde s'il y a une bombe
				if(bombermanGame.getAgentBombermanByBomb(bombe) != agent) { // On verifie que cette bombe n'appartient pas au bomberman
					if(bombe.getStateBomb() == StateBomb.Step3) {	// On regarde si c'est une bombe à l'etat 3
					
						if(agent.isLegalMove(bombermanGame, AgentAction.MOVE_UP)) {
							return AgentAction.MOVE_UP;
						}
						else if(agent.isLegalMove(bombermanGame, AgentAction.MOVE_DOWN)) {
							return  AgentAction.MOVE_DOWN;
						}
					}
				}
			}
		}
		
		for(int i = agent.getPosY() - RANGE_BOMBE_DEFAULT; i < agent.getPosY() + RANGE_BOMBE_DEFAULT; ++i) {
			Bombe bombe = bombermanGame.getBombByCoord(agent.getPosX(),i);

			if(bombe != null) {
				if(bombermanGame.getAgentBombermanByBomb(bombe) != agent) {
					if(bombe.getStateBomb() == StateBomb.Step3) {
			
						if(agent.isLegalMove(bombermanGame, AgentAction.MOVE_LEFT)) {
							return AgentAction.MOVE_LEFT;
						}
						else if(agent.isLegalMove(bombermanGame, AgentAction.MOVE_RIGHT)) {
							return AgentAction.MOVE_RIGHT;
						}
					}
				}
			}
		}
				
		for(int i = agent.getPosX() - RANGE_BOMBE_DEFAULT; i < agent.getPosX() + RANGE_BOMBE_DEFAULT; ++i) {
			for(int j = agent.getPosY() - RANGE_BOMBE_DEFAULT; j < agent.getPosY() + RANGE_BOMBE_DEFAULT; ++j) {

				Agent agentPNJ = bombermanGame.getAgentPNJByCoord(i,j);
				
				if(agentPNJ != null) {
					if (Math.abs(agent.getPosX() - agentPNJ.getPosX()) >= Math.abs(agent.getPosY() - agentPNJ.getPosY())) {
						if (agent.getPosX() < agentPNJ.getPosX()) {
							return AgentAction.MOVE_LEFT;
						} else {
							return AgentAction.MOVE_RIGHT;
						}
					} else {
						if (agent.getPosY() < agentPNJ.getPosY()) {
							return AgentAction.MOVE_UP;
						} else {
							return AgentAction.MOVE_DOWN;
						}
					}
				}
				
			}
		}

		return AgentAction.STOP;
	}

}
