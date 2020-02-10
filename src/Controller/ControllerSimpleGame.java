package Controller;

import Model.Game;
import Model.SimpleGame;
import View.ViewCommand;
import View.ViewSimpleGame;

public class ControllerSimpleGame implements InterfaceController {

	private SimpleGame _simpleGame;
	
	public ControllerSimpleGame(SimpleGame game) {
		this._simpleGame = game;
		ViewCommand viewCommand = new ViewCommand(this, game);
		ViewSimpleGame viewSimpleGame = new ViewSimpleGame(this, game);
	}
	
	public void start() {
		this._simpleGame.init();
	}

	public void step() {
		this._simpleGame.step();
	}

	public void run() {
		this._simpleGame.launch();		
	}

	public void stop() {
		this._simpleGame.stop();
	}

	public void setTime(long time) {
		this._simpleGame.setTime(time);
	}
	
	public long getTime() {
		return _simpleGame.getTime();
	}
	
	public int getInitTime() {
		return Game.INIT_TIME;
	}

}
