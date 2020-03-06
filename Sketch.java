import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import NeuralNetwork.Painter;
import processing.core.PApplet;
import processing.core.PVector;

public class Sketch extends PApplet {
	public enum Gamemode {
		MENU, AI, PLAY;
	}
	public static int width = 1280;
	public static int height = 800;
	
	private List<Bird> population;
	private List<Bird> activePopulation;
	private List<Pipe> pipes;
	private Bird player1;
	private Bird player2;
	private int[] col1 = {255, 255, 0};
	private int[] col2 = {0, 255, 255};
	private Painter p;
	
	private int population_size = 100;
	private double mutation_rate = 0.04;	
	private PVector gravity = new PVector(0, (float)0.9999);
	
	private int frameCount;
	private int pipeDistance = 80;
	
	private int generation;
	private int score1;
	private int score2;
	private int highscore;
	private Bird bestBird;
	
	private Gamemode gamemode = Gamemode.MENU;
	private int players = 1;
	
	public static void main(String[] args) {
		PApplet.main("Sketch");
	}
	
	public void settings() {
		size(width, height);
	}
	
	public void setup() {
		population = new ArrayList<Bird>();
		activePopulation = new ArrayList<Bird>();
		pipes = new ArrayList<Pipe>();
	}
	
	public void draw() {
		background(20);
		
		fill(255);
		textSize(40);
		
		textAlign(CENTER);
		if (gamemode == Gamemode.MENU) {
			textSize(60);
			text("COMMANDS", width/2, height/3);
			
			textSize(40);
			text("UP arrow - Machine Learning", width/2, height/3 + 50);
			text("DOWN arrow - Arcade Mode", width/2, height/3 + 100);
		}
		
		if (gamemode == Gamemode.AI) {
			textAlign(LEFT);
			text("Score: " + score1, 20, 40);
			text("Highscore: " + highscore, 20, 80);
			text("Gen: " + generation, 20, 120); 
		
			Pipe closestPipe = getClosestPipe();
			
			for (int i = pipes.size()-1; i >= 0; i--) {
				pipes.get(i).move();
				pipes.get(i).display();
				
				if (pipes.get(i).outOfBounds()) {
					pipes.remove(pipes.get(i));
					
					score1++;
					if (score1 > highscore) {
						highscore = score1;
					}
				}
			}
			
			for (int i = activePopulation.size()-1; i >= 0; i--) {
				Bird bird = activePopulation.get(i);
				bird.overlap(closestPipe);
				
				if (bird.status().equals("alive")) {
					bird.process(closestPipe);
					bird.applyForce(gravity);
					bird.move();
					bird.display();
				}
			}
			
			if (frameCount > pipeDistance) {
				pipes.add(new Pipe(this));
				frameCount = 0;
			}
			
			frameCount++;
		
		
			for (int i = 0; i < activePopulation.size(); i++) {
				if (activePopulation.get(i).status().equals("dead")) {
					activePopulation.remove(activePopulation.get(i));
				}
			}
			
			textAlign(RIGHT);
			fill(255);
			text("Alive: " + activePopulation.size(), width-20, 40);
			
			if (activePopulation.size() == 0) {
				reproduce();
				initialise();
				
				if (score1 > highscore) {
					highscore = score1;
				}
				score1 = 0;
			}
			
			
			PVector x_bounds = new PVector(0, width/3);
			PVector y_bounds = new PVector(height/2, height);
			
			if (bestBird.status().equals("dead")) {
				int randBird = (int)(Math.random()*activePopulation.size());
				bestBird = activePopulation.get(randBird);
				p.setNetwork(bestBird.nn());
			}
			
			p.displayNetwork(x_bounds, y_bounds);
			
		} else if (gamemode == Gamemode.PLAY) {
			textAlign(LEFT);
			text("Player 1: " + score1, 20, 40);
			if (players == 2) {
				text("Player 2: " + score2, 20, 80);
			} else {
				text("Highscore: " + highscore, 20, 80);
			}
			
			Pipe closestPipe = getClosestPipe();
			
			for (int i = pipes.size()-1; i >= 0; i--) {
				pipes.get(i).move();
				pipes.get(i).display();
				
				if (pipes.get(i).outOfBounds()) {
					pipes.remove(pipes.get(i));
				}
			}
						
			if (player1.status().equals("alive")) {
				player1.overlap(closestPipe);
				player1.applyForce(gravity);
				player1.move();
				player1.display();
			}
			
			if (players == 2 && player2.status().equals("alive")) {
				player2.overlap(closestPipe);
				player2.applyForce(gravity);
				player2.move();
				player2.display();
			}
			
			if (player1.status().equals("dead") || (players == 2 && player2.status().equals("dead"))) {
				if (players == 2) {
					if (player1.status().equals("alive")) {
						score1++; 
					} else {
						score2++;
					}
				} else {
					score1 = 0;
				}
				
				startGame();
			}
			
			if (frameCount > pipeDistance) {
				pipes.add(new Pipe(this));
				frameCount = 0;
				
				if (players == 1 && player1.status().equals("alive")) {
					score1++;
					
					if (score1 > highscore) {
						highscore = score1;
					}
				}
			}
			
			frameCount++;
		}
	}
	
	private void initialise() {
		pipes.clear();
		pipes.add(new Pipe(this));
		
		activePopulation.clear();
		for (int i = 0; i < population.size(); i++) {
			activePopulation.add(population.get(i));
		}
		
	
		frameCount = 0;
	}
	
	private void startGame() {
		player1 = new Bird(this, col1);
		
		if (players == 2) {
			player2 = new Bird(this, col2);
		}
		
		initialise();
	}
	
	private void setupLearningHistory() {
		population.clear();
		
		for (int i = 0; i < population_size; i++) {
			Bird newBird = new Bird(this);
			population.add(newBird);
		}
		
		generation = 1;
		highscore = 0;
		
		bestBird = population.get(0);
		p = new Painter(this, bestBird.nn());
	}
	
	private Pipe getClosestPipe() {
		Pipe closestPipe = pipes.get(pipes.size()-1);
		for (int i = pipes.size()-2; i >= 0; i--) {
			if (pipes.get(i).getEdge() < closestPipe.getEdge() && pipes.get(i).getEdge() > Bird.xPos) {
				closestPipe = pipes.get(i);
			}
		}
		
		return closestPipe;
	}
	
	private void reproduce() {
		float maxFitness = 1;
		for (int i = 0; i < population.size(); i++) {
			if (population.get(i).fitness() > maxFitness) {
				maxFitness = population.get(i).fitness();
			}
		}
		
		
		float fitnessSum = 0;
		for (int i = 0; i < population.size(); i++) {
			population.get(i).evaluate(maxFitness, 2);
			fitnessSum += population.get(i).fitness();
		}
		
		List<Bird> newPopulation = new ArrayList<Bird>();
		for (int i = 0; i < population_size; i++) {
			double fitnessIndex = Math.random()*fitnessSum;
			int agentIndex = -1;
			
			while (fitnessIndex > 0) {
				agentIndex++;
				fitnessIndex -= population.get(agentIndex).fitness();
			}
						
			newPopulation.add(population.get(agentIndex).getCopy(mutation_rate));
		}
		
		population = newPopulation;
		generation++;
	}
	
	public void keyPressed() {
		if (gamemode == Gamemode.MENU) {
			if (keyCode == UP) {
				gamemode = Gamemode.AI;
				
				score1 = 0;
				score2 = 0;
				
				setupLearningHistory();
				initialise();
				
			} else if (keyCode == DOWN) {
				gamemode = Gamemode.PLAY;
				players = 1;
				
				score1 = 0;
				score2 = 0;
				
				startGame();
				initialise();
			}
		}
		if (gamemode == Gamemode.AI) {
			if (keyCode == 82) {
				reproduce();
				initialise();
			}
		}
		if (gamemode == Gamemode.PLAY) {
			if (keyCode == 49) {
				players = 1;
				highscore = 0;
				score1 = 0;
				
				startGame();
			} else if (keyCode == 50) {
				players = 2;
				score1 = 0;
				score2 = 0;
				
				startGame();
			}
			
			if (player1.status().equals("alive") && (keyCode == UP || keyCode == 32)) {
				player1.jump();
			}
			
			if (players == 2 && player2.status().equals("alive") && keyCode == 87) {
				player2.jump();
			}
		}
		
		
		if (keyCode == 80) {
			gamemode = Gamemode.MENU;
		}
	}
}
