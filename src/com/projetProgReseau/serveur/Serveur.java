package com.projetProgReseau.serveur;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.projetBomberman.modele.BombermanGame;
import com.projetBomberman.modele.ModeJeu;
import com.projetBomberman.strategy.PutBombStrategy;
import com.projetBomberman.strategy.Strategy;

public class Serveur implements Runnable {
	
	public Socket connexion;
	public List<Socket> listSockets = new ArrayList<Socket>();
	private BufferedReader entree;
	private DataOutputStream sortie;
	private BombermanGame game;
	
	public Serveur(Socket s, List<Socket> listSockets){
		this.connexion=s;
		this.listSockets = listSockets;
		
		try {
			this.entree = new BufferedReader(new InputStreamReader(this.connexion.getInputStream()));
			this.sortie = new DataOutputStream(this.connexion.getOutputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String ch;
		
		try {
			ch = entree.readLine();
			System.out.println("Connexion de " + ch + " sur le serveur !");
			
			this.game = new BombermanGame(ModeJeu.SOLO, new PutBombStrategy(), 1000);
			
			while(true) {				
				ch = entree.readLine();
				
				sortie.writeUTF(ch);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void notifyAllClient(String ch) {
		try {
			for(Socket so : this.listSockets) {
				if(so != this.connexion && !so.isClosed()) {
					DataOutputStream sortieSocket = new DataOutputStream (so.getOutputStream());
					sortieSocket.writeUTF(ch);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void terminer() {
		try{
	        if(this.connexion != null) {
	        	this.connexion.close();
	        }
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void main(String[] argu) {
		int p; // le port d’écoute
		ServerSocket ecoute;
		Socket so;

		List<Socket> listClients = new ArrayList<Socket>();
		
		if (argu.length == 1) {
			try {
				p=Integer.parseInt(argu[0]); // on récupère le port
				ecoute = new ServerSocket(p); // on crée le serveur
				System.out.println("serveur mis en place ");
				
				while (true) { // le serveur va attendre qu’une connexion arrive
					so = ecoute.accept();
					
					listClients.add(so);
					System.out.println(listClients);
					
					Serveur serv = new Serveur(so, listClients);
					Thread t = new Thread(serv);
					t.start();
				}
//				so.close();

			} catch (IOException e) { 
				System.out.println("problème\n"+e.getMessage());
			}
		} else { 
			System.out.println("syntaxe d’appel java Serveur port\n");
		} 
	}
}
