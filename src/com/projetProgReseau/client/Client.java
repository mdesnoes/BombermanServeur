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
	
	private String message;
	private ControllerBombermanGame controllerBombermanGame;
	
	public Client(ControllerBombermanGame controllerBombermanGame, String message) {
		this.controllerBombermanGame = controllerBombermanGame;
		this.message = message;	
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

			System.out.println("Demande de connexion de " + message.split(",")[0] +" sur le serveur");
			sortie.println(this.message);
			
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
	
	public void setMessage(String message) {
		this.message = message;
	}
}
