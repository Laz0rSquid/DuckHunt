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
	
	public static final int MOVEMENT_SPEED = 1;
	
	static final int DUCK_SIZE = 60;
	
	static final int NON_DUCK_SIZE = 40;

	/**
	 *
	 * @param x
	 * @param y
	 * @param goesLeft
	 */
	public Target(double x, double y, boolean goesLeft) {
		this.isDuck = Math.random() < 0.7;
		int size;
		if (this.isDuck) {
			size = DUCK_SIZE;
		} else {
			size = NON_DUCK_SIZE;
		}
		this.shape = new Ellipse2D.Double(x, y - (size / 2), size, size);
		this.goesLeft = goesLeft;
	}

	// default move method, calls specific method with MOVEMENT_SPEED
	public void move() {
		move(MOVEMENT_SPEED);
	}

	// specific move method, can be called via parameter
	public void move(int moveBy) {
		if (this.goesLeft) {
			this.shape.setFrame(this.shape.getX() - moveBy,
				this.shape.getY(),
				this.shape.getWidth(),
				this.shape.getHeight());
		} else {
			this.shape.setFrame(this.shape.getX() + moveBy,
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
