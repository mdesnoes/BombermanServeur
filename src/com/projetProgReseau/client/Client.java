package com.projetProgReseau.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.projetBomberman.controller.ControllerBombermanGame;


public class Client implements Runnable {
	
	private final static String nomServ = "localhost";
	private final static int port = 1030; // le port de connexion
	
	private String nom;
	private ControllerBombermanGame controllerBombermanGame;
	
	public Client(ControllerBombermanGame controllerBombermanGame, String nom) {
		this.controllerBombermanGame = controllerBombermanGame;
		this.nom = nom;	
	}
	
	public void run() {		
		Socket so;
		DataInputStream entree;
		PrintWriter sortie;
		String messageRecu;
		try{
			so = new Socket(nomServ, port);
			sortie = new PrintWriter(so.getOutputStream(), true);
			entree = new DataInputStream(so.getInputStream());
			
			System.out.println("Demande de connexion de " + this.nom +" sur le serveur");
			sortie.println(this.nom);
			
			while(true) {
				messageRecu = entree.readUTF(); // on lit l’entier qui arrive
				System.out.println(messageRecu);
			
				if(messageRecu.equals("Connexion_OK")) {
					controllerBombermanGame.createView();
				}
			}
		} catch(UnknownHostException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println("Aucun serveur n’est rattaché au port ");
		}
	}

}
