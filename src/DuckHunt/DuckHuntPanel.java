/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DuckHunt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
public class DuckHuntPanel extends JPanel implements ActionListener {

	private static final int WINDOW_SIZE = 600;

	// Saving targets
	private ArrayList<Target> topLane;
	private ArrayList<Target> middleLane;
	private ArrayList<Target> bottomLane;

	// spawns a target each X frames
	private static final int SPAWN_RATE = 180;

	private final static int TARGET_SIZE = 50;

	// used to calculate where to delete targets
	private final static double LEFT_BORDER = -TARGET_SIZE;
	private final static double RIGHT_BORDER = (TARGET_SIZE / 2) + WINDOW_SIZE;

	// spawn locations
	private final static Point SPAWN_TOP = new Point(-TARGET_SIZE, WINDOW_SIZE / 4);
	private final static Point SPAWN_MIDDLE = new Point(WINDOW_SIZE + TARGET_SIZE, WINDOW_SIZE / 2);
	private final static Point SPAWN_BOTTOM = new Point(-TARGET_SIZE, (WINDOW_SIZE / 4) * 3);

	// font
	private Font font = new Font(Font.DIALOG, Font.BOLD, 18);

	// location for score print
	private static final Point SCORE_LOCATION = new Point(10, 20);
	private static final String SCORE_NAME = "Score : ";

	// player score
	private int score;

	// location for remaining lives print
	private static final Point LIVES_LOCATION = new Point(10, 40);
	private static final String LIVES_NAME = "Lives : ";

	// number of lives
	private static final int MAX_LIVES = 5;
	private int livesLost;

	// location for remaining lives print
	private static final Point HS_LOCATION = new Point(300, 20);
	private static final String HS_NAME = "Highscore : ";

	// all time high score
	private int highscore;

	// location for saving highscore
	private static final String PATHHS = "." + File.separator + "res" + File.separator + "highscore.txt";

	// boolean for stopping the programm
	private boolean isRunning;

	// callback
	private LossInterface loss;

	// timer
	private int frameCount;

	// base unit for score keeping
	private static final int BASE_SCORE_UNIT = 100;

	/**
	 * constructor for content of game window
	 *
	 * @param li - used for callback to Main()
	 * @throws java.io.FileNotFoundException
	 */
	public DuckHuntPanel(LossInterface li) throws FileNotFoundException {
		resetAttributes(li);
		Timer timer = new Timer(1000 / 60, (ActionListener) this);
		timer.start();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				super.mouseClicked(me);
				checkForHits(topLane, me);
				checkForHits(middleLane, me);
				checkForHits(bottomLane, me);
			}
		});
	}

	/**
	 * Cleans constructor of unnecessary clutter, improves readability
	 *
	 * @param li
	 */
	public void resetAttributes(LossInterface li) {
		try {
			initHighscore();
		} catch (IOException ex) {
			Logger.getLogger(DuckHuntPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
		score = 0;
		frameCount = 0;
		livesLost = 0;
		isRunning = true;
		setBackground(Color.BLACK);
		loss = li;
		topLane = new ArrayList<>();
		middleLane = new ArrayList<>();
		bottomLane = new ArrayList<>();
	}

	private void initHighscore() throws IOException {
		File f = getHighscoreFile();
		Path path = Paths.get(f.getPath());
		String hs = new String(Files.readAllBytes(path), "UTF-8");
		if (hs.isEmpty()) {
			highscore = 0;
		} else {
			highscore = Integer.parseInt(hs);
		}
	}

	private File getHighscoreFile() throws IOException {
		File f = new File(PATHHS);
		f.getParentFile().mkdirs();
		f.createNewFile();
		return f;
	}

	private void checkForHits(ArrayList<Target> lane, MouseEvent me) {
		for (Iterator<Target> target = lane.iterator(); target.hasNext();) {
			Target t = target.next();
			if (t.getShape().contains(me.getPoint())) {
				if (t.isDuck) {
					score += BASE_SCORE_UNIT;
				} else if (score > 0) {
					score -= BASE_SCORE_UNIT;
				} else {
					livesLost++;
				}
				target.remove();
			}
		}
	}

	public void update() {
		if (!isRunning) {
			return;
		}
		frameCount++;

		moveTargets(topLane);
		moveTargets(middleLane);
		moveTargets(bottomLane);

		if (frameCount % SPAWN_RATE == 0) {
			topLane.add(
				new Target(
					SPAWN_TOP.x,
					SPAWN_TOP.y,
					TARGET_SIZE,
					TARGET_SIZE,
					false));
			middleLane.add(
				new Target(
					SPAWN_MIDDLE.x,
					SPAWN_MIDDLE.y,
					TARGET_SIZE,
					TARGET_SIZE,
					true));
			bottomLane.add(
				new Target(
					SPAWN_BOTTOM.x,
					SPAWN_BOTTOM.y,
					TARGET_SIZE,
					TARGET_SIZE,
					false));
		}
		if (livesLost >= MAX_LIVES) {
			postGameCleanUp();
		}
		repaint();
	}

	private void moveTargets(ArrayList<Target> lane) {
		for (Iterator<Target> target = lane.iterator(); target.hasNext();) {
			Target t = target.next();
			t.move();
			if (t.isOutsideScreen(LEFT_BORDER, RIGHT_BORDER)) {
				target.remove();
				if (t.isDuck) {
					livesLost++;
				}
			}
		}
	}

	@Override
	protected void paintComponent(Graphics grphcs) {
		super.paintComponent(grphcs);
		Graphics2D g2d = (Graphics2D) grphcs;

		drawTargets(g2d, topLane);
		drawTargets(g2d, middleLane);
		drawTargets(g2d, bottomLane);

		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		g2d.drawString(SCORE_NAME + String.valueOf(score),
			SCORE_LOCATION.x,
			SCORE_LOCATION.y);
		g2d.drawString(LIVES_NAME + String.valueOf(MAX_LIVES - livesLost),
			LIVES_LOCATION.x,
			LIVES_LOCATION.y);
		g2d.drawString(HS_NAME + String.valueOf(highscore),
			HS_LOCATION.x,
			HS_LOCATION.y);
	}

	private void drawTargets(Graphics2D g2d, ArrayList<Target> lane) {
		for (Target t : lane) {
			if (t.isDuck) {
				g2d.setColor(Color.MAGENTA);
			} else {
				g2d.setColor(Color.CYAN);
			}
			g2d.fill(t.getShape());
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WINDOW_SIZE, WINDOW_SIZE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		update();
	}

	private void postGameCleanUp() {
		isRunning = false;
		if (score > highscore) {
			File f;
			try {
				f = getHighscoreFile();
				FileWriter fw = new FileWriter(f);
				String newHighscore = String.valueOf(score);
				System.out.println(newHighscore);
				fw.write(newHighscore);
				fw.flush();
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(DuckHuntPanel.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		loss.onLossAction();
	}

	public int getScore() {
		return score;
	}
}
