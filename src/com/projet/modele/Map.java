package com.projet.modele;

import java.io.Serializable;

import java.util.ArrayList;

/** 
 * Classe qui permet de charger une carte de Bomberman à partir d'un fichier de layout d'extension .lay
 * 
 * @author Kevin Balavoine, Victor Lelu--Ribaimont 
 * 
 */

public class Map implements Serializable {

	private static final long serialVersionUID = 1L;
	private int sizeX;
	private int sizeY;
	private boolean[][] walls;
	private boolean[][] start_breakable_walls;
	private ArrayList<InfoAgent> start_agents;

	
	public Map() {
	}

	
	public int getSizeX() {
		return sizeX;
	}
	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}
	public boolean[][] getWalls() {
		return walls;
	}
	public void setWalls(boolean[][] walls) {
		this.walls = walls;
	}
	public boolean[][] getStart_breakable_walls() {
		return start_breakable_walls;
	}
	public void setStart_breakable_walls(boolean[][] start_breakable_walls) {
		this.start_breakable_walls = start_breakable_walls;
	}
	public ArrayList<InfoAgent> getStart_agents() {
		return start_agents;
	}
	public void setStart_agents(ArrayList<InfoAgent> start_agents) {
		this.start_agents = start_agents;
	}

	
}