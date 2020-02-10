package Factory;

import Model.Agent;
import Strategy.Strategy;

public interface AgentFactory {
		
	Agent createAgent(int pos_x, int pos_y, char type, Strategy strategy);
	
}
