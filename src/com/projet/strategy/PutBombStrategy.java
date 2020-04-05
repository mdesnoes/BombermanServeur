package com.projet.strategy;


import com.projet.modele.Agent;
import com.projet.modele.AgentBomberman;
import com.projet.modele.BombermanGame;
import com.projet.modele.type.AgentAction;

/* Strategy pour les bomberman : 
 * S'il y a un agent dans le rayon de ça bombe, il pose une bombe
 * S'il est gené par un mur cassable, il pose une bombe grâce à la stratégy BreakWallStrategy
 * S'il est gené par un mur non cassable ou qu'il ne peut pas poser de bombe, il fait un deplacement random (cela permet egalement de debloquer les agents)
 * Sinon il se deplace avec la RaijonStrategy
 * Mais ça priorité est d'abord d'esquiver les bombes grâce à la stratégie EsquiveStrategy
 */

public class PutBombStrategy implements Strategy {
	
	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {
		EsquiveStrategy esquiveStrategy = new EsquiveStrategy();
		TeteChercheuseStrategy raijonStrategy = new TeteChercheuseStrategy();
		BreakWallStrategy breakWallStrategy = new BreakWallStrategy();
		RandomStrategy randomStrategy = new RandomStrategy();
		
		/* Priorité : il esquive les bombes */
		AgentAction esquiveAction = esquiveStrategy.chooseAction(bombermanGame, agent);
		if(esquiveAction != AgentAction.STOP) {
			return esquiveAction;
		}

		// Il pose une bombe si il detecte un agent ennemie
		for(int i=agent.getPosX() - ((AgentBomberman) agent).getRangeBomb(); i<=agent.getPosX() + ((AgentBomberman) agent).getRangeBomb(); ++i) {
			for(int j=agent.getPosY() - ((AgentBomberman) agent).getRangeBomb(); j<=agent.getPosY() + ((AgentBomberman) agent).getRangeBomb(); ++j) {
				Agent agentDetecte = bombermanGame.getAgentByCoord(i, j);

				if(agentDetecte != agent && agentDetecte != null) {
					if(agent.canPutBomb()) {
						return AgentAction.PUT_BOMB;
					}
					break; // il ne peut pas poser de bombe donc ça ne sert à rien de parcourir le reste de la boucle
				}
			}
		}
		
		AgentAction actionRaijonStrat = raijonStrategy.chooseAction(bombermanGame, agent);

		if(!agent.isLegalMove(bombermanGame, actionRaijonStrat)) {
			breakWallStrategy.setAction(actionRaijonStrat);

			if(breakWallStrategy.chooseAction(bombermanGame, agent) != AgentAction.STOP) {
				return AgentAction.PUT_BOMB;
			} else {
				return randomStrategy.chooseAction(bombermanGame, agent);
			}
		}
		
		return actionRaijonStrat;
	}

}
