package DuckHunt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

/**
 *
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
public class DuckHuntGame extends GameData implements ActionListener {

	/**
	 * interface as parameter is used for callbacks, allowing Main() to restart
	 * the game
	 *
	 * @param li - used for callback to Main()
	 * @throws java.io.FileNotFoundException
	 */
	public DuckHuntGame(LossInterface li) throws FileNotFoundException {
		resetAttributes(li);
		timer = new Timer(1000 / gameSpeed, (ActionListener) this);
		timer.start();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				super.mouseClicked(me);
				playSound(Sound.GUNFIRE);
				checkForHits(topLane, me);
				checkForHits(middleLane, me);
				checkForHits(bottomLane, me);
			}
		});
	}

	/**
	 * Helper method. Unclutters constructor of unnecessary clutter, improves
	 * readability, code can be reused for restarting the game.
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
		level = 1;
		lastLevelUp = 0;
	}

	private void initHighscore() throws IOException {
		InputStream highscoreInput = getClass().getClassLoader().getResourceAsStream("res/highscore.txt");
		String hs = new BufferedReader(new InputStreamReader(highscoreInput)).lines()
			.collect(Collectors.joining(System.lineSeparator()));
		if (hs.isEmpty()) {
			highscore = 0;
		} else {
			highscore = Integer.parseInt(hs);
		}
	}

	private void checkForHits(ArrayList<Target> lane, MouseEvent me) {
		for (Iterator<Target> target = lane.iterator(); target.hasNext();) {
			Target t = target.next();
			if (t.getShape().contains(me.getPoint())) {
				Sound sound = null;
				if (t.isDuck) {
					score += BASE_SCORE_UNIT;
					sound = Sound.DUCK;
				} else if (score > 0) {
					score -= BASE_SCORE_UNIT;
					sound = Sound.DEVIL;
				}
				playSound(sound);
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
		if (score > 0 && score % SPEED_BUMP == 0 && lastLevelUp != score) {
			lastLevelUp = score;
			gameSpeed += SPEED_INCREMENT;
			level += LVL_INCREMENT;
			timer.stop();
			timer = new Timer(1000 / gameSpeed, (ActionListener) this);
			timer.start();
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
		g2d.drawString(LVL_NAME + String.valueOf(level),
			LVL_LOCATION.x,
			LVL_LOCATION.y);
	}

	private void playSound(Sound sound) {
		InputStream input = new BufferedInputStream(getClass().getClassLoader()
			.getResourceAsStream(sound.getPath()));
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(input);
			AudioFormat format = audioIn.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioIn);
			clip.start();
			audioIn.close();
			input.close();
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void drawTargets(Graphics2D g2d, ArrayList<Target> lane) {
		for (Target t : lane) {
			g2d.fill(t.getShape());
			InputStream input;
			if (t.isDuck) {
				input = getClass().getClassLoader().getResourceAsStream("res/duck.png");
			} else {
				input = getClass().getClassLoader().getResourceAsStream("res/devil.png");
			}
			try {
				BufferedImage image = ImageIO.read(input);
				g2d.drawImage(image, (int) t.getShape().getX(), (int) t.getShape().getY(), this);
			} catch (Exception e) {
				Logger.getLogger(DuckHuntGame.class.getName()).log(Level.SEVERE, null, e);
			}
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
				f = new File(getClass().getClassLoader().getResource("res/highscore.txt").getFile());
				FileWriter fw = new FileWriter(f);
				String newHighscore = String.valueOf(score);
				fw.write(newHighscore);
				fw.flush();
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(DuckHuntGame.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		playSound(Sound.ENDGAME);
		loss.onLossAction();
	}

	public int getScore() {
		return score;
	}
}
