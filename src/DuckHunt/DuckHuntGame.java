/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DuckHunt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import javax.swing.Timer;

/**
 *
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
public class DuckHuntGame extends GameData implements ActionListener {
	
	/**
	 * interface as parameter is used for callbacks, allowing Main() to restart the game
	 * @param li - used for callback to Main()
	 * @throws java.io.FileNotFoundException
	 */
	public DuckHuntGame(LossInterface li) throws FileNotFoundException {
		resetAttributes(li);
		Timer timer = new Timer(1000 / gameSpeed, (ActionListener) this);
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
	 * Helper method. Unclutters constructor of unnecessary clutter, improves readability,
	 * code can be reused for restarting the game.
	 *
	 * @param li
	 */
	public void resetAttributes(LossInterface li) {
		try {
			initHighscore();
		} catch (IOException ex) {
			Logger.getLogger(DuckHuntGame.class.getName()).log(Level.SEVERE, null, ex);
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
		gameSpeed = 60;
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
		// prevents frameCount from overflowing
		frameCount = frameCount % Integer.MAX_VALUE;
		
		frameCount++;

		moveTargets(topLane);
		moveTargets(middleLane);
		moveTargets(bottomLane);

		if (frameCount % SPAWN_RATE == 0) {
			topLane.add(
				new Target(
					SPAWN_TOP.x,
					SPAWN_TOP.y,
					false));
			middleLane.add(
				new Target(
					SPAWN_MIDDLE.x,
					SPAWN_MIDDLE.y,
					true));
			bottomLane.add(
				new Target(
					SPAWN_BOTTOM.x,
					SPAWN_BOTTOM.y,
					false));
		}
		if (score > 0 && score % SPEED_BUMP == 0) {
			gameSpeed += SPEED_INCREMENT;
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
		g2d.setFont(FONT);
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
				Logger.getLogger(DuckHuntGame.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		loss.onLossAction();
	}

	public int getScore() {
		return score;
	}
}
