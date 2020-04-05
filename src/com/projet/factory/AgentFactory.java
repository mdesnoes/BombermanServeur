package com.projet.factory;

import com.projet.modele.Agent;
import com.projet.strategy.Strategy;

public interface AgentFactory {
		
	Agent createAgent(int pos_x, int pos_y, char type, Strategy strategy);
	
}
