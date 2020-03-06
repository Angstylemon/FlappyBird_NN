import NeuralNetwork.NeuralNetwork;
import processing.core.PApplet;
import processing.core.PVector;

public class Bird extends PApplet {
	protected final PApplet parent;
	
	public static int xPos = 300;
	private static int yStarting = Sketch.height/2;
	
	protected PVector pos;
	protected PVector vel;
	protected PVector acc;
	
	private static float size = 25;
	private PVector jump = new PVector(0, -15);
	private int[] colour;
	private static int[] DEFAULT_COLOUR = {255, 255, 0};
	
	protected float fitness;
	protected String status;
	
	private DNA dna;
	private static int _numInputs = 3;
	private static int[] _numHidden = {5};
	private static int _numOutputs = 1;
	
	public Bird(PApplet s) {
		this(s, DEFAULT_COLOUR);
	}
	
	public Bird(PApplet s, int[] c) {
		parent = s;
		colour = c;
		dna = new DNA();
		
		pos = new PVector(xPos, yStarting);
		vel = new PVector(0, 0);
		acc = new PVector(0, 0);
	
		initialise();
	}
	
	public Bird(PApplet p, DNA d) {
		parent = p;
		dna = d;
		colour = DEFAULT_COLOUR;
		
		initialise();
	}
	
	private void initialise() {
		pos = new PVector(xPos, yStarting);
		vel = new PVector(0, 0);
		acc = new PVector(0, 0);
		
		status = "alive";
	}
	
	public void evaluate(float f, int power) {
		fitness = (float)Math.pow(fitness/f, power);
	}
	
	public float fitness() {
		return fitness;
	}
	
	public void move() {
		vel.y += acc.y;
		pos.y += vel.y;
				
		acc.x *= 0;
		acc.y *= 0;
		
		if (vel.y > 20) {
			vel.y = 20;
		}
		
		if (pos.y + size/2 > Sketch.height) {
			vel.y = 0;
			status = "dead";
			fitness /= 2;
		} else if (pos.y - size < 0) {
			pos.y = 0 + size;
			vel.y = 0;
		}
		
		fitness++;
	}
		
	public void applyForce(PVector p) {
		acc.add(p);
	}
	
	public void jump() {
		if (vel.y > 0) {
			vel.y = -15;
		} else {
			vel.y -= 8;
		}
	}
	
	public void overlap(Pipe p) {
		float[] posArray = p.getPipe();
		
		float top = posArray[0];
		float bottom = posArray[1];
		float pipeX = posArray[2];
		float pipeWidth = posArray[3];
		
		if (status.equals("alive")) {
			//Check if bird overlaps horizontally
			if (pos.x + size/1.5 > pipeX - pipeWidth/2 && pos.x - size/1.5 < pipeX + pipeWidth/2) {
				//Check if bird overlaps vertically
				if (pos.y - size/1.5 < top || pos.y + size/1.5 > bottom) {
					status = "dead";
					double adjust = fitness * Math.abs((p.gap() - pos.y)/Sketch.height);
					
					fitness -= adjust;
					
					
					if (fitness < 0) {
						fitness = (float)0.01;
					}
				}
			}
		}
	}
	
	public String status() {
		return status;
	}
	
	public void display() {
		parent.noStroke();
		parent.fill(colour[0], colour[1], colour[2]);
		parent.circle(pos.x,  pos.y,  size*2);
	}
	
	public NeuralNetwork nn() {
		return dna.nn();
	}
	
	public Bird getCopy(double mutation_rate) {
		return new Bird(parent, dna.copy(mutation_rate));
	}
	
	public void process(Pipe p) {
		double[] inputs = new double[_numInputs];
		inputs[0] = (p.gap() - pos.y)/Sketch.height;
		inputs[1] = (p.x() - pos.x)/Sketch.width;
		
		if (inputs.length > 2) {
			inputs[2] = -vel.y/50;
		}
		
		if (inputs.length > 3) {
			inputs[3] = pos.y/Sketch.height;
		}
		
		double[] predict = dna.process(inputs);
		if (predict[0] < 0.5) {
			jump();
		}
	}
	
	private class DNA {
		private NeuralNetwork brain;
		
		private int inputs = _numInputs;
		private int[] hidden = _numHidden;
		private int outputs = _numOutputs;
		
		private DNA() {
			brain = new NeuralNetwork(inputs, hidden, outputs);
//			colour = new int[3];
//			for (int i = 0; i < colour.length; i++) {
//				colour[i] = (int)(Math.random()*255);
//			}
		}
		
		private DNA(NeuralNetwork nn) {
			brain = nn;	
		}
		
		private DNA copy(double mutation_rate) {
			DNA dnaCopy = new DNA(brain.copy());
			dnaCopy.brain.mutate(mutation_rate);
			return dnaCopy;
		}
		
		private NeuralNetwork nn() {
			return brain.copy();
		}
		
		private double[] process(double[] inputs) {
			return brain.feedForward(inputs);
		}
	}
}
