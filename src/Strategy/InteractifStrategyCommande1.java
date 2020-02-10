package Strategy;


import java.awt.event.KeyEvent;

import Model.Agent;
import Model.AgentAction;
import Model.BombermanGame;
import View.ViewModeInteractif;


public class InteractifStrategyCommande1 implements Strategy {	

	@Override
	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {
		
		bombermanGame.getControllerBombGame().setViewModeInteractif();
		ViewModeInteractif viewModeInteractif = bombermanGame.getControllerBombGame().getViewModeInteractif();
		viewModeInteractif.addKeyListener(viewModeInteractif);

		viewModeInteractif.requestFocus();		
		
		switch (viewModeInteractif.getKeyPressed())
        {
            case KeyEvent.VK_UP:
                return AgentAction.MOVE_UP;
            case KeyEvent.VK_LEFT:
            	return AgentAction.MOVE_LEFT;
            case KeyEvent.VK_RIGHT:
            	return AgentAction.MOVE_RIGHT;
            case KeyEvent.VK_DOWN:
            	return AgentAction.MOVE_DOWN;
            case KeyEvent.VK_NUMPAD0:
            	return AgentAction.PUT_BOMB;
            case KeyEvent.VK_NUMPAD1:
            	return AgentAction.STOP;
            default:
            	return AgentAction.STOP;
        }
				
	}
	
}
