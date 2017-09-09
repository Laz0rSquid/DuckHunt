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
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
public class DuckHuntPanel extends JPanel implements ActionListener {

    private static final Dimension DIM = new Dimension(600, 600);

    // Saving targets
    private ArrayList<Target> topLane;
    private ArrayList<Target> middleLane;
    private ArrayList<Target> bottomLane;

    // player score
    private static int score = 0;

    // timer
    private static int frameCount = 0;
    
    // number of lives
    private static int lives = 10;

    // spawns a target each X frames
    private static final int SPAWN_RATE = 150;

    private final static double TARGET_SIZE = 50;

    // used to calculate where to delete targets
    private final static double LEFT_BORDER = -TARGET_SIZE;
    private final static double RIGHT_BORDER = (TARGET_SIZE / 2) + DIM.getWidth();

    // spawn locations
    private final static Point SPAWN_TOP = new Point((int) -TARGET_SIZE, (int) DIM.getWidth() / 4);
    private final static Point SPAWN_MIDDLE = new Point((int) ((int) DIM.getHeight() + TARGET_SIZE), (int) DIM.getWidth() / 2);
    private final static Point SPAWN_BOTTOM = new Point((int) -TARGET_SIZE, (int) ( DIM.getWidth() / 4) * 3);
    
    // font
    Font font = new Font(Font.DIALOG, Font.BOLD, 18);
    
    // location for score print
    private static final Point SCORE_LOCATION = new Point(10, 20);
    private static final String SCORE_NAME = "Score : ";
    
    // location for remaining lives print
    private static final Point LIVES_LOCATION = new Point(10, 40);
    private static final String LIVES_NAME = "Lives : ";

    public DuckHuntPanel() {
        setBackground(Color.BLACK);
        topLane = new ArrayList<>();
        middleLane = new ArrayList<>();
        bottomLane = new ArrayList<>();
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

    private static void checkForHits(ArrayList<Target> lane, MouseEvent me) {
        for (Iterator<Target> target = lane.iterator(); target.hasNext();) {
            Target t = target.next();
            if (t.getShape().contains(me.getPoint())) {
                if (t.isDuck) {
                    score++;
                } else {
                    score--;
                }
                target.remove();
            }
        }
    }
    
    public void update() {
        frameCount++;

        removeTargetsOutsideScreen(topLane);
        removeTargetsOutsideScreen(middleLane);
        removeTargetsOutsideScreen(bottomLane);

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
        repaint();
    }

    private static void removeTargetsOutsideScreen(ArrayList<Target> lane) {
        for (Iterator<Target> target = lane.iterator(); target.hasNext();) {
            Target t = target.next();
            t.move();
            if (t.isOutsideScreen(LEFT_BORDER, RIGHT_BORDER)) {
                target.remove();
                if (t.isDuck) {
                    lives--;
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
        g2d.drawString(SCORE_NAME + String.valueOf(score), SCORE_LOCATION.x, SCORE_LOCATION.y);
        g2d.drawString(LIVES_NAME + String.valueOf(lives), LIVES_LOCATION.x, LIVES_LOCATION.y);
    }

    private static void drawTargets(Graphics2D g2d, ArrayList<Target> lane) {
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
        return DIM;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }
}
