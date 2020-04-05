package com.projet.factory;

import com.projet.modele.Agent;
import com.projet.modele.AgentBird;
import com.projet.modele.AgentEnnemyBasique;
import com.projet.modele.AgentRajion;
import com.projet.modele.type.ColorAgent;
import com.projet.strategy.BasiqueStrategy;
import com.projet.strategy.ReveilStrategy;
import com.projet.strategy.Strategy;
import com.projet.strategy.TeteChercheuseStrategy;

public class PNJFactory implements AgentFactory {

	public Agent createAgent(int pos_x, int pos_y, char type, Strategy strategy) {
		switch(type) {
			case 'R':
				return new AgentRajion(pos_x, pos_y, type, ColorAgent.ROUGE, new TeteChercheuseStrategy());
			case 'V':
				return new AgentBird(pos_x, pos_y, type, ColorAgent.JAUNE, new ReveilStrategy());
			case 'E':
				return new AgentEnnemyBasique(pos_x, pos_y, type, ColorAgent.BLANC, new BasiqueStrategy());
			default:
				return null;
		}
	}

}
