package Controller;

import java.util.ArrayList;

import Model.*;
import View.InfoAgent;
import View.InfoBomb;
import View.InfoItem;
import View.Map;
import View.ViewBombermanGame;
import View.ViewCommand;
import View.ViewModeInteractif;

public class ControllerBombermanGame implements InterfaceController {

	private BombermanGame _bombGame;
	private ViewCommand _viewCommand;
	private ViewBombermanGame _viewBombGame;
	private ViewModeInteractif _viewModeInteractif;
	
	public ControllerBombermanGame(BombermanGame bombGame) {
		this._bombGame = bombGame;
		// En mode PERCEPTRON, on n'affiche pas les views (pour faire les simulations)
		if(bombGame.getModeJeu() != ModeJeu.PERCEPTRON) {
			this._viewCommand = new ViewCommand(this, bombGame);
			this._viewBombGame = new ViewBombermanGame(this, bombGame);
		}
	}
	
	public Map getMap() {
		//En mode PERCEPTRON, la map est 'mapPerceptron'
		if(this._bombGame.getModeJeu() == ModeJeu.PERCEPTRON) {
			try {
				return new Map("layout/mapPerceptron");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this._viewBombGame.getMap();
	}
	
	public void start() {
		this._bombGame.init();
	}

	public void step() {
		this._bombGame.step();
	}

	public void run() {
		this._bombGame.launch();
	}

	public void stop() {
		this._bombGame.stop();
	}

	public void setTime(long time) {
		this._bombGame.setTime(time);
	}

	public long getTime() {
		return _bombGame.getTime();
	}

	public int getInitTime() {
		return Game.INIT_TIME;
	}

	public String getLayout() {
		if(this._bombGame.getModeJeu() == ModeJeu.PERCEPTRON) {
			return "mapPerceptron";
		}
		return this._viewCommand.getLayoutGame();
	}
	
	
	//Permet de renseigner les nouvelles coordonnées des agents à la liste d'InfoAgent et retourner cette liste
	public ArrayList<InfoAgent> getListInfoAgent() {
		ArrayList<InfoAgent> infoListAgent = new ArrayList<>();

		for(AgentBomberman agent : this._bombGame.getListAgentBomberman()) {
			infoListAgent.add(new InfoAgent(agent.getX(), agent.getY(), agent.getAction(), agent.getType(), agent.getColor(),agent.isInvincible(),agent.getIsSick()));
		}

		for(AgentPNJ agent : this._bombGame.getListAgentPNJ()) {
			infoListAgent.add(new InfoAgent(agent.getX(), agent.getY(), agent.getAction(), agent.getType(), agent.getColor(), false, false));
		}

		return infoListAgent;
	}
	
	//Permet de renseigner les nouvelles coordonnées des items à la liste d'InfoItem et retourner cette liste
	public ArrayList<InfoItem> getListInfoItems() {
		ArrayList<InfoItem> infoItemList = new ArrayList<>();

		for(Item item : this._bombGame.getListItem()) {
			infoItemList.add(new InfoItem(item.getX(), item.getY(), item.getType()));
		}

		return infoItemList;
	}
	
	//Permet de renseigner les nouvelles coordonnées des bombes à la liste d'InfoBombs et retourner cette liste
	public ArrayList<InfoBomb> getListInfoBombs() {
		ArrayList<InfoBomb> infoBombList = new ArrayList<>();

		for(Bombe bomb : this._bombGame.getListBomb()) {
			infoBombList.add(new InfoBomb(bomb.getX(), bomb.getY(), bomb.getRange(), bomb.getStateBomb()));
		}

		return infoBombList;
	}
	
	public boolean[][] getListBreakableWall() {
		return this._bombGame.getListBreakableWall();
	}
	
	
	public void setViewModeInteractif() {
		this._viewModeInteractif = ViewModeInteractif.getInstance();
	}
	
	public void setViewBombermanGame(ViewBombermanGame vbombGame) {
		this._viewBombGame = vbombGame;
	}

	public ViewModeInteractif getViewModeInteractif() {
		return this._viewModeInteractif;
	}
	
	public ViewCommand getViewCommand() {
		return this._viewCommand;
	}

	public ViewBombermanGame getViewBombGame() {
		return this._viewBombGame;
	}

}
