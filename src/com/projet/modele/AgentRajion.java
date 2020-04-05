package com.projet.modele;

import com.projet.modele.type.ColorAgent;
import com.projet.strategy.Strategy;

public class AgentRajion extends AgentPNJ {

	public AgentRajion(int pos_x, int pos_y, char type, ColorAgent color, Strategy strategy) {
		super(pos_x, pos_y, type, color,strategy);
	}

}
