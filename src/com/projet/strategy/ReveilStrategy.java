package com.projet.strategy;

import java.util.ArrayList;

import com.projet.modele.Agent;
import com.projet.modele.AgentBomberman;
import com.projet.modele.BombermanGame;
import com.projet.modele.type.AgentAction;

public class ReveilStrategy implements Strategy {

	private static final int RAYON_ACTION = 5;
	private ArrayList<AgentBomberman> agentRayon = new ArrayList<>();

    public AgentAction chooseAction(BombermanGame b, Agent a) {
    	agentRayon.clear();

        for (AgentBomberman i : b.getListAgentsBomberman()) {
            if ((Math.abs(a.getPosX() - i.getPosX()) < RAYON_ACTION) && (Math.abs(a.getPosY() - i.getPosY()) < RAYON_ACTION)) {
                agentRayon.add(i);
            }
        }
        
        if (!agentRayon.isEmpty()) {
            int procheDist = RAYON_ACTION*2;
            AgentBomberman procheBomb = null;

            for (AgentBomberman i : agentRayon) {
                int dist = Math.abs(a.getPosX() - i.getPosX()) + Math.abs(a.getPosY() - i.getPosY());
                if (dist < procheDist) {
                    procheDist = dist;
                    procheBomb = i;
                }
            }

            assert procheBomb != null;

            if (Math.abs(a.getPosX() - procheBomb.getPosX()) >= Math.abs(a.getPosY() - procheBomb.getPosY())) {
                if (a.getPosX() < procheBomb.getPosX()) {
                    return AgentAction.MOVE_RIGHT;
                } else {
                    return AgentAction.MOVE_LEFT;
                }
            } else {
                if (a.getPosY() < procheBomb.getPosY()) {
                    return AgentAction.MOVE_DOWN;
                } else {
                    return AgentAction.MOVE_UP;
                }
            }
        } else {
            return AgentAction.STOP;
        }
    }

}
