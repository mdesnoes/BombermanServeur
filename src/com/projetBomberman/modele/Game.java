package com.projetBomberman.modele;

import java.util.Observable;

import com.fasterxml.jackson.annotation.JsonIgnore;


public abstract class Game extends Observable implements Runnable {

	public static final int INIT_TIME = 1000;
	private int turn;
	private int maxturn;
	@JsonIgnore
	private boolean isRunning;
	private long time;
	@JsonIgnore
	private Thread thread;

	public Game(int maxturn) {
		this.maxturn = maxturn;
		this.time = INIT_TIME;
	}
	
	public void init() {
		this.turn = 0;
		this.isRunning = true;
		initialize_game();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void step() {
		if(gameContinue() && this.turn < this.maxturn) {
			this.turn++;
			takeTurn();
		}
		else {
			this.isRunning = false;
			gameOver();
		}
		
		this.setChanged();
		this.notifyObservers();
	}
	
	public void run() {
		while(this.isRunning) {
			step();
			
			try {
				Thread.sleep(this.time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		this.isRunning = false;
	}
	
	public void launch() {
		this.isRunning = true;
		this.thread = new Thread(this);
		thread.start();
	}

	public abstract void initialize_game();
	public abstract void takeTurn();
	public abstract boolean gameContinue();
	public abstract void gameOver();

	
	
	
	public int getTurn() {
		return turn;
	}
	public void setTurn(int turn) {
		this.turn = turn;
	}
	public int getMaxturn() {
		return maxturn;
	}
	public void setMaxturn(int maxturn) {
		this.maxturn = maxturn;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Thread getThread() {
		return thread;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	

}
