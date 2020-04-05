import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.projet.serveur.Serveur;

public class LancementServeur {

	public static void main(String[] argu) {
		if (argu.length == 1) {
			try {
				Socket so;
				int port = Integer.parseInt(argu[0]);
				ServerSocket ecoute = new ServerSocket(port);
				List<Socket> listeClients = new ArrayList<Socket>();
				System.out.println("serveur mis en place ");
				
				while (true) {
					so = ecoute.accept();
					
					listeClients.add(so);
					System.out.println("Nombre de clients sur le serveur : " + listeClients.size());
					
					Serveur serv = new Serveur(so, listeClients);
					Thread t = new Thread(serv);
					t.start();
				}

			} catch (IOException e) { 
				System.out.println("ERREUR : " + e.getMessage());
			}
		} else {
			System.out.println("ERREUR : Il manque des options !!");
			System.out.println("Les options :");
			System.out.println("\t 1. numero du port");
		} 
	}

}
