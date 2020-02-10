package Factory;

import Model.Agent;
import Model.AgentBomberman;
import Model.ColorAgent;
import Strategy.Strategy;

public class BombermanFactory implements AgentFactory {

	private ColorAgent[] _tabColor = { ColorAgent.BLEU, ColorAgent.ROUGE, ColorAgent.VERT, ColorAgent.JAUNE, ColorAgent.BLANC};
	private static int COMPTEUR = 0;
	
	public Agent createAgent(int pos_x, int pos_y, char type, Strategy strategy) {
		if(COMPTEUR < this._tabColor.length - 1) {
			COMPTEUR++;
		}
		else {
			COMPTEUR = 0;
		}
		
		return new AgentBomberman(pos_x, pos_y, type, this._tabColor[COMPTEUR], strategy);
	}

}
