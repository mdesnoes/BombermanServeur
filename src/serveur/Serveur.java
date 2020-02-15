package serveur;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Serveur implements Runnable {
	public Socket so;
	public List<Socket> listSockets = new ArrayList<Socket>();

	
	public Serveur(Socket s, List<Socket> listSockets){
		this.so=s;
		this.listSockets = listSockets;
	}
	
	@Override
	public void run() {
		BufferedReader entree;
		DataOutputStream sortie;
		String ch; // la chaîne reçue
		
		int i=0; // Nb de message
		String nomUser = null; // Nom de l'utilisateur
		
		try {
			entree = new BufferedReader(new InputStreamReader(so.getInputStream()));
			sortie = new DataOutputStream(so.getOutputStream());

			while(true) {				
				ch = entree.readLine(); // on lit ce qui arrive
				
				sortie.writeUTF("Connexion_OK");
				
				
//				for(Socket so : this.listSockets) {
//					if(so != this.so) {
//						DataOutputStream sortieSocket = new DataOutputStream (so.getOutputStream());
//						sortieSocket.writeUTF(ch);
//					}
//				}
				
				++i;
			}
		} catch (IOException e) {
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
			System.out.println("syntaxe d’appel java servTexte port\n");
		} 
	}
}
