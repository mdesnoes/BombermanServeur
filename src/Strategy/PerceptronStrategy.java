package Strategy;

import Model.Agent;
import Model.AgentAction;
import Model.BombermanGame;
import perceptron.SparseVector;

public class PerceptronStrategy implements Strategy {

	@Override
	public AgentAction chooseAction(BombermanGame bombermanGame, Agent agent) {

		SparseVector v = agent.encodeEtat(bombermanGame);

		System.out.println(v);
		
		return null;
	}

}
