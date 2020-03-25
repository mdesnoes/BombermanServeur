package com.projetBomberman.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.projetBomberman.modele.*;
import com.projetBomberman.view.InfoAgent;
import com.projetBomberman.view.InfoBomb;
import com.projetBomberman.view.InfoItem;
import com.projetBomberman.view.Map;
import com.projetBomberman.view.ViewBombermanGame;
import com.projetBomberman.view.ViewCommand;
import com.projetBomberman.view.ViewModeInteractif;

public class ControllerBombermanGame implements InterfaceController {

	private BombermanGame _bombGame;
	private ViewCommand _viewCommand;
	private ViewBombermanGame _viewBombGame;
	private ViewModeInteractif _viewModeInteractif;
	private DataOutputStream sortie;
	
	
	public ControllerBombermanGame(BombermanGame bombGame, DataOutputStream sortie) {
		this._bombGame = bombGame;
		this.sortie = sortie;
		
		createView();
	}
	
	public void createView() {
		// En mode PERCEPTRON, on n'affiche pas les views (pour faire les simulations)
		if(this._bombGame.getModeJeu() != ModeJeu.PERCEPTRON) {
			this._viewCommand = new ViewCommand(this, this._bombGame);
			this._viewBombGame = new ViewBombermanGame(this, this._bombGame);
		}
	}
	
	public void start() {
		this._bombGame.init();
		
		try {
			this.sortie.writeUTF("INITIALISATION DE LA PARTIE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void step() {
		this._bombGame.step();
		
		try {
			this.sortie.writeUTF("AVANCEMENT DE LA PARTIE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		this._bombGame.launch();
		
		try {
			this.sortie.writeUTF("DEMARRAGE DE LA PARTIE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		this._bombGame.stop();
		
		try {
			this.sortie.writeUTF("PARTIE EN PAUSE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void quitter() {
		this._viewCommand.setVisible(false);
		this._viewBombGame.setVisible(false);
		if(this._viewModeInteractif != null) {
			this._viewModeInteractif.setVisible(false);
		}
		
		try {
			this.sortie.writeUTF("DECONNEXION");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public String getLayout() {
		if(this._bombGame.getModeJeu() == ModeJeu.PERCEPTRON) {
			return "mapPerceptron";
		}
		return this._viewCommand.getLayoutGame();
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
	public void setTime(long time) {
		this._bombGame.setTime(time);
	}
	public long getTime() {
		return _bombGame.getTime();
	}
	public int getInitTime() {
		return Game.INIT_TIME;
	}

}
