/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DuckHunt;

import java.awt.geom.Ellipse2D;

/**
 *
 * @author Marcus Kopp <marcus.kopp86 at gmail.com> 
 */
public class Target {

    public boolean isDuck;
    public Ellipse2D shape;
    public boolean goesLeft;

    /*
    logic needed for three lanes of targets. new balls will be randomly
    assigned to one of the three lanes. the movement goes like this:
    Lane 1 : to the right
    Lane 2 : to the left
    Lane 3 : to the right
     */
    /**
     *
     * @param x
     * @param y
     * @param height
     * @param width
     * @param goesLeft
     */
    public Target(double x, double y, double height, double width, boolean goesLeft) {
        this.shape = new Ellipse2D.Double(x, y - (height / 2), height, width);
        this.isDuck = Math.random() < 0.7;
        this.goesLeft = goesLeft;
    }

    public void move() {
        if (this.goesLeft) {
            this.shape.setFrame(this.shape.getX() - 1,
                    this.shape.getY(),
                    this.shape.getWidth(),
                    this.shape.getHeight());
        } else {
            this.shape.setFrame(this.shape.getX() + 1,
                    this.shape.getY(),
                    this.shape.getWidth(),
                    this.shape.getHeight());
        }
    }

    public boolean isIsDuck() {
        return isDuck;
    }

    public Ellipse2D getShape() {
        return shape;
    }

    public boolean isOutsideScreen(double leftBorder, double rightBorder) {
        if (this.goesLeft) {
            return this.getShape().getX() < leftBorder;
        } else {
            return this.getShape().getX() > rightBorder;
        }
    }
}
