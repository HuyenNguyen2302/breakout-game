/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {
	
	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 500;
	public static final int APPLICATION_HEIGHT = 600;
	
	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;
	
	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;
	
	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;
	
	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;
	
	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;
	
	/** Separation between bricks */
	private static final int BRICK_SEP = 4;
	
	/** Width of a brick */
	private static final int BRICK_WIDTH =
			(WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;
	
	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;
	
	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;
	
	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;
	
	/** Number of turns */
	private static final int NTURNS = 3;
	
	/** The coordinates of the paddle */
	private int x = WIDTH / 2 - PADDLE_WIDTH / 2;
	private int y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
	
	/** Animation delay or pause time between ball moves */
	private static final int DELAY = 10;
	
	/* Random number generator for vx */
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private double vy = 3;			/* The velocities in the y direction */
	private double xBall = 	WIDTH / 2 - BALL_RADIUS;	/* The x coordinate of the ball */
	private double yBall = 	HEIGHT / 2 - BALL_RADIUS;	/* The y coordinate of the ball */
	
	/*
	public static void main(String[] args) {
		new Breakout().start(args);
	}
	*/
	
	public void run() {
		setSize(WIDTH, HEIGHT);
		
		for (int turn = 1; turn <= NTURNS; turn++) {
			
			setUpGame();
			reSet();
			startGame();
			turnCount = turn;
			
			if (countBricks == 0) {
				winScreen();
				break;
			} else {
				removeAll();
				printLives();
				pause(1000);
				removeAll();
			}
		}
		loseScreen();
	}
	
	public void setUpGame() {
		createBrick();
		createPaddle();
		createBall();
	}
	
	public void reSet() {
		countBricks = NBRICK_ROWS * 10;
		xBall = WIDTH / 2 - BALL_RADIUS;	/* The x coordinate of the ball */
		yBall = HEIGHT / 2 - BALL_RADIUS;	/* The y coordinate of the ball */
	}
	
	public void startGame() {
		waitForClick();
		getXVelocity();
		moveBall();
	}
	
	/* Clear the window and print winning message */
	public void winScreen() {
		removeAll();
		GLabel win = new GLabel ("CONGRATULATIONS! YOU WON!", getWidth()/2, getHeight()/2);
		win.move(-win.getWidth() / 2, -win.getAscent());
		win.setColor(Color.BLUE);
		win.setFont("SansSerif-bold-20");
		add (win);
	}
	
	public void printLives() {
		GLabel lives = new GLabel ( (NTURNS - turnCount) + " live(s) left!", getWidth()/2, getHeight()/2);
		lives.move(-lives.getWidth()/2, -lives.getAscent());
		lives.setFont("SansSerif-20");
		add (lives);
	}
	
	/* Clear the window and print "game over" message */
	public void loseScreen() {
		removeAll();
		GLabel win = new GLabel ("Game Over :(", getWidth()/2, getHeight()/2);
		win.move(-win.getWidth()/2, -win.getAscent());
		win.setColor(Color.RED);
		win.setFont("SansSerif-bold-20");
		add (win);
	}
	
	public void createBrick() {
		for (int row = 1; row <= NBRICK_ROWS; row++) {
			for (int brickNum = 1; brickNum <= NBRICKS_PER_ROW; brickNum++) {
				
				/* Coordinates of each brick */
				int y = BRICK_Y_OFFSET + (row - 1) * (BRICK_HEIGHT + BRICK_SEP);
				int x = (brickNum - 1) * (BRICK_WIDTH + BRICK_SEP);
				
				grect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				grect.setFilled(true);
				
				if (row == 1 || row == 2) {
					grect.setColor(Color.RED);
				} 
				if (row == 3 || row == 4) {
					grect.setColor(Color.ORANGE);
				}
				if (row == 5 || row == 6) {
					grect.setColor(Color.YELLOW);
				}
				if (row == 7 || row == 8) {
					grect.setColor(Color.GREEN);
				}
				if (row == 9 || row == 10) {
					grect.setColor(Color.CYAN);
				}
				add(grect);
			}
		}
	}
	
	private void createPaddle() {
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
		addMouseListeners();
	}
	
	/* Called on mouse press to record the coordinates of the click */
	public void mousePressed(MouseEvent e) {
		lastMouse = new GPoint(e.getPoint());
		lastPaddle = paddle.getX();
	}
	
	
	/* Called on mouse drag to reposition the object */
	public void mouseDragged(MouseEvent e) {
		/* What happens if the mouse goes off the screen to the right */
		if (e.getX() >= WIDTH) {
			paddle.move(WIDTH - lastPaddle - PADDLE_WIDTH, 0);
			lastMouse = new GPoint(WIDTH - PADDLE_WIDTH, y);
			lastPaddle = WIDTH - PADDLE_WIDTH;
		} else {
			/* What happens if the mouse goes off the screen to the left */
			if (e.getX() <= 0) {
				paddle.move(- lastPaddle, 0);
				lastMouse = new GPoint(0, y);
				lastPaddle = 0;
			} else {
				/* What happens if the mouse stays in the screen */
				paddle.move(e.getX() - lastMouse.getX(), 0);
				lastMouse = new GPoint(e.getPoint());
				lastPaddle = paddle.getX();
			}
		}
	}
	
	public void createBall() {
		double xBall = WIDTH / 2 - BALL_RADIUS;	/* The x coordinate of the ball */
	        double yBall = HEIGHT / 2 - BALL_RADIUS;	/* The y coordinate of the ball */
		
		ball = new GOval(xBall, yBall, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}
	
	/* Get the random x velocity */
	public void getXVelocity() {
		vx = rgen.nextDouble(1.0, 5.0);
		if (rgen.nextBoolean(0.5)) {
			vx = - vx;
		}
	}
	
	/* The ball keeps moving as long as it doesn't hit one of the walls 
	 * or collide with any objects
	 */
	public void moveBall() {
		collider = getCollidingObject();
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		
		/* Conditions for the ball to collide with the walls */
		bottom = (yBall >= HEIGHT - BALL_RADIUS * 2);
		boolean top = (yBall  <= 0);
		boolean left = (xBall  >= WIDTH - BALL_RADIUS * 2);
		boolean right = (xBall  <= 0);
		
		while (true) {
			if (bottom) break;
			else {
				if (top || collider == paddle) vy = - vy;
				else if (collider != null) {
					remove(collider);
					countBricks--;
					vy = - vy;
					bounceClip.play();
				}
				if (right || left) vx = - vx;
			}
			xBall += vx;
			yBall += vy;
			ball.move(vx, vy);
			pause(DELAY);
			moveBall();
		}
	}
	
	/* Get the colliding object with the ball
	 * returns null if none is found
	 */
	public GObject getCollidingObject() {
		double xBall = ball.getX();
		double yBall = ball.getY();
		
		GPoint topLeft = new GPoint (xBall, yBall);
		GPoint topRight = new GPoint (xBall + 2 * BALL_RADIUS, yBall);
		GPoint bottomLeft = new GPoint (xBall, yBall + 2 * BALL_RADIUS);
		GPoint bottomRight = new GPoint (xBall + 2 * BALL_RADIUS, yBall + 2 * BALL_RADIUS);
		
		if (getElementAt(topLeft) != null) { return getElementAt(topLeft); }
		if (getElementAt(topRight) != null) { return getElementAt(topRight); }
		if (getElementAt(bottomLeft) != null) { return getElementAt(bottomLeft); }
		else { return getElementAt(bottomRight); }

	}
	
	/* Private instance variables */
	private GPoint lastMouse;              /* The last mouse position  */
	private double lastPaddle;         	/* The last paddle position */
	private GRect paddle;			/* The paddle */
	private GOval ball; 			/* The ball */
	private double vx;				/* The velocities in the x direction */
	private boolean bottom, top, left, right;	/* True if ball hit one of the specified walls */
	private GObject collider;
	private int turnCount;
	private GRect grect;
	private int countBricks;
}
