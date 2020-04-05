package com.projet.strategy;

import com.projet.modele.Agent;
import com.projet.modele.BombermanGame;
import com.projet.modele.type.AgentAction;

public interface Strategy {
	
	AgentAction chooseAction(BombermanGame bombermanGame, Agent agent);
}
