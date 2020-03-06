import processing.core.PApplet;
import processing.core.PVector;

public class Pipe extends PApplet {
	private PApplet parent;
	
	private static final int WIDTH = 120;
	private int gap;
	private int top;
	private int bottom;
	private int gapOffset = 100;
	private static int gapHeight = 90;
	
	private static int xStart = Sketch.WIDTH + WIDTH;
	private static float xVel = -5;
	
	protected PVector pos;
	protected PVector vel;
	protected PVector acc;
	
	
	private static float[] colour = {0, 255, 0};
	
	public Pipe(PApplet p) {
		parent = p;
		
		pos = new PVector(xStart, 0);
		vel = new PVector(xVel, 0);
		acc = new PVector(0, 0);
		
		int topLimit = gapOffset + gapHeight/2;
		int bottomLimit = Sketch.HEIGHT - gapOffset - gapHeight/2;
		int gapRange = bottomLimit - topLimit;
		
		gap = (int)(Math.random()*gapRange + topLimit);
		top = gap - gapHeight;
		bottom = gap + gapHeight;
	}
	
	public void move() {
		vel.x += acc.x;
		vel.y += acc.y;
		pos.x += vel.x;
		pos.y += vel.y;
		
		acc.x *= 0;
		acc.y *= 0;
	}
		
	public void applyForce(PVector p) {
		acc.add(p);
	}
	
	public boolean outOfBounds() {
		if (pos.x + width < 0) {
			return true;
		}
		
		return false;
	}
	
	public float getEdge() {
		return pos.x + width/2;
	}
	
	public float[] getPipe() {
		float[] posArray = {top, bottom, pos.x, width};
		return posArray;
	}
	
	public int gap() {
		return gap;
	}
	
	public int top() {
		return top;
	}
	
	public int bottom() {
		return bottom;
	}
	
	public float x() {
		return pos.x;
	}
	
	public void display() {
		parent.fill(colour[0], colour[1], colour[2]);
		parent.noStroke();
		parent.rectMode(CORNER);
		
		parent.rect(pos.x - width/2, 0, width, top);
		parent.rect(pos.x - width/2, bottom, width, Sketch.HEIGHT - bottom);
	}
}
