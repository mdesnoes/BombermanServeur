package Factory;

import Model.Agent;
import Model.AgentBird;
import Model.AgentEnnemyBasique;
import Model.AgentRajion;
import Model.ColorAgent;
import Strategy.BasiqueStrategy;
import Strategy.ReveilStrategy;
import Strategy.TeteChercheuseStrategy;
import Strategy.Strategy;

public class PNJFactory implements AgentFactory {

	public Agent createAgent(int pos_x, int pos_y, char type, Strategy strategy) {
		switch(type) {
			case 'R':
				if(strategy != null) { //On est dans le cas du mode de jeu avec le perceptron
					return new AgentRajion(pos_x, pos_y, type, ColorAgent.ROUGE, strategy);
				}
				return new AgentRajion(pos_x, pos_y, type, ColorAgent.ROUGE, new TeteChercheuseStrategy());
			case 'V':
				if(strategy != null) { //On est dans le cas du mode de jeu avec le perceptron
					return new AgentRajion(pos_x, pos_y, type, ColorAgent.JAUNE, strategy);
				}
				return new AgentBird(pos_x, pos_y, type, ColorAgent.JAUNE, new ReveilStrategy());
			case 'E':
				if(strategy != null) { //On est dans le cas du mode de jeu avec le perceptron
					return new AgentRajion(pos_x, pos_y, type, ColorAgent.BLANC, strategy);
				}
				return new AgentEnnemyBasique(pos_x, pos_y, type, ColorAgent.BLANC, new BasiqueStrategy());
			default:
				return null;
		}
	}

}
