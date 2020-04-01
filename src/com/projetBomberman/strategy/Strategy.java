package com.projetBomberman.strategy;

import com.projetBomberman.modele.Agent;
import com.projetBomberman.modele.BombermanGame;
import com.projetBomberman.modele.info.AgentAction;

public interface Strategy {
	
	AgentAction chooseAction(BombermanGame bombermanGame, Agent agent);
}
