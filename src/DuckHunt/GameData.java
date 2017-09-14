/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DuckHunt;

import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * 
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
abstract class GameData extends JPanel {
	
	/**
	 * Stores the data necessary for running the game. Is mainly used to clean up
	 * the DuckHuntGame class, as the overhead was getting pretty ridiculous.
	 */
	
	static final int WINDOW_SIZE = 600;
	
	// spawns a target each X frames
	static final int SPAWN_RATE = 180;

	

	// used to calculate where to delete targets
	static final double LEFT_BORDER = -100;
	static final double RIGHT_BORDER = (100 / 2) + WINDOW_SIZE;

	// spawn locations
	static final Point SPAWN_TOP = new Point(-100, WINDOW_SIZE / 4);
	ArrayList<Target> topLane;
	
	static final Point SPAWN_MIDDLE = new Point(WINDOW_SIZE + 100, WINDOW_SIZE / 2);
	ArrayList<Target> middleLane;
	
	static final Point SPAWN_BOTTOM = new Point(-100, (WINDOW_SIZE / 4) * 3);
	ArrayList<Target> bottomLane;

	// font
	final Font FONT = new Font(Font.DIALOG, Font.BOLD, 18);

	// location for score print
	static final Point SCORE_LOCATION = new Point(10, 20);
	static final String SCORE_NAME = "Score : ";

	// player score
	int score;

	// location for remaining lives print
	static final Point LIVES_LOCATION = new Point(10, 40);
	static final String LIVES_NAME = "Lives : ";

	// number of lives
	static final int MAX_LIVES = 5;
	int livesLost;

	// location for remaining lives print
	static final Point HS_LOCATION = new Point(300, 20);
	static final String HS_NAME = "Highscore : ";

	// all time high score
	int highscore;

	// location for saving highscore
	static final String PATHHS = "." + File.separator + "res" + File.separator + "highscore.txt";

	// boolean for stopping the programm
	boolean isRunning;

	// callback
	LossInterface loss;

	// counts number of frames rendered since program start
	int frameCount;

	// base multiplier for score keeping
	static final int BASE_SCORE_UNIT = 100;
	
	// number of game updates done per second
	int gameSpeed;
	
	// score value at which game speed is increase
	static final int SPEED_BUMP = 1500;
	static final int SPEED_INCREMENT = 6;
}
