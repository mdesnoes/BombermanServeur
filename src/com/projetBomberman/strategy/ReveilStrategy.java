package com.projetBomberman.strategy;

import java.util.ArrayList;

import com.projetBomberman.modele.Agent;
import com.projetBomberman.modele.AgentBomberman;
import com.projetBomberman.modele.BombermanGame;
import com.projetBomberman.modele.info.AgentAction;

public class ReveilStrategy implements Strategy {

	public static final int rayonAction = 5;
	public ArrayList<AgentBomberman> agentRayon = new ArrayList<>();

    public AgentAction chooseAction(BombermanGame b, Agent a) {
    	agentRayon.clear();

        for (AgentBomberman i : b.getListAgentsBomberman()) {
            if ((Math.abs(a.getPosX() - i.getPosX()) < rayonAction) && (Math.abs(a.getPosY() - i.getPosY()) < rayonAction)) {
                agentRayon.add(i);
            }
        }
        
        if (!agentRayon.isEmpty()) {
            int procheDist = rayonAction*2;
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
