package com.projetBomberman.modele;

import com.projetBomberman.modele.info.ColorAgent;
import com.projetBomberman.strategy.Strategy;

public class AgentRajion extends AgentPNJ {

	public AgentRajion(int pos_x, int pos_y, char type, ColorAgent color, Strategy strategy) {
		super(pos_x, pos_y, type, color,strategy);
	}

}
