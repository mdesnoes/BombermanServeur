package Strategy;

import Model.Agent;
import Model.AgentAction;
import Model.BombermanGame;

public class RandomStrategy implements Strategy {

	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {
		AgentAction[] tabAction = {AgentAction.MOVE_UP, AgentAction.MOVE_DOWN, AgentAction.MOVE_LEFT, AgentAction.MOVE_RIGHT, AgentAction.STOP, AgentAction.PUT_BOMB};
		int nbRandom = (int) (Math.random() * tabAction.length);
		return tabAction[nbRandom];
	}

}
