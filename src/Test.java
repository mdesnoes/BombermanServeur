import java.util.ArrayList;

import com.projetBomberman.modele.BombermanGame;
import com.projetBomberman.modele.ModeJeu;
import com.projetBomberman.strategy.PutBombStrategy;
import com.projetBomberman.strategy.RandomStrategy;
import com.projetBomberman.strategy.Strategy;
import com.projetBomberman.view.ViewBombermanGame;

public class Test {

	public static void main(String[] args) {
	
		//Test mode Normal
		// BombermanGame bombGame = new BombermanGame(ModeJeu.SOLO, new PutBombStrategy(),1000);
		
		//Test mode Interactif
		/*
		BombermanGame bombGame = new BombermanGame(ModeJeu.SOLO, new PutBombStrategy(),1000);
		BombermanGame bombGame = new BombermanGame(ModeJeu.DUO, new PutBombStrategy(),1000);
		BombermanGame bombGame = new BombermanGame(ModeJeu.DUEL, new PutBombStrategy(),1000);
		*/
		
		//Test simulation Perceptron
		//launchSimulation(100,100,new RandomStrategy());
		
		//visualize(100, new PutBombStrategy(), 100);
	}
	
	//Permet de lancer une simulation
	public static void launchSimulation(int nbTour, int nbSimu, Strategy strategy) {
		System.out.println("Début de la simulation");
		System.out.println("Reward Average : " + getAverageReward(nbTour, nbSimu, strategy));
		System.out.println("Fin de la simulation");
	}

	
	public static float getAverageReward(int nbSimu, int nbTour, Strategy strategy) {
		ArrayList<BombermanGame> listBombermanGame = new ArrayList<BombermanGame>();
		
		int reward = 0;
		for(int i=0; i<nbSimu; ++i) {
			BombermanGame bombGame = new BombermanGame(ModeJeu.PERCEPTRON, strategy, nbTour);
						
			bombGame.init();
			bombGame.setTime(0);

			bombGame.launch();
			
			
			listBombermanGame.add(bombGame);
		}
		
		for(BombermanGame bombermanGame : listBombermanGame) {
            try {
    			reward += bombermanGame.getReward();
                bombermanGame.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }		
		
		return reward / nbSimu;
	}
	
	public static void visualize (int nbTour, Strategy strategy, int pause) {
		BombermanGame bombGame = new BombermanGame(ModeJeu.PERCEPTRON, strategy, nbTour);
		bombGame.setTime(pause);
		bombGame.init();
		
		ViewBombermanGame v = new ViewBombermanGame(bombGame.getControllerBombGame(), bombGame);

		bombGame.getControllerBombGame().setViewBombermanGame(v);
		
		bombGame.launch();
	}

}
