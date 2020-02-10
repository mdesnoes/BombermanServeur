package View;

import Controller.InterfaceController;
import Model.SimpleGame;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class ViewSimpleGame extends JFrame implements Observer {
	
	private JLabel _labelTurn;
	private InterfaceController _controllerGame;

	public ViewSimpleGame(InterfaceController controllerGame, SimpleGame simplegame) {
		this._controllerGame = controllerGame;
		simplegame.addObserver(this);
		this.createView();
	}
	
	private void createView() {
		this.setTitle("Game");
		this.setSize(new Dimension(700,700));
		Dimension windowSize = this.getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2 - 350;
		this.setLocation(dx,dy);
		this._labelTurn = new JLabel("Current turn 0");
		this._labelTurn.setHorizontalAlignment(JLabel.CENTER);
		this._labelTurn.setVerticalAlignment(JLabel.NORTH);
		this.setContentPane(this._labelTurn);
		this.setVisible(true);
 	}

	public void update(Observable obs, Object arg) {
		SimpleGame simplegame = (SimpleGame)obs;
		this._labelTurn.setText("Current turn " + simplegame.getTurn());
	}

}
