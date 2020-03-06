import processing.core.PApplet;
import processing.core.PVector;

public class Sketch extends PApplet {
	public enum Gamemode {
		MENU, AI, PLAY;
	}
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 800;
	
	private Population _population;
	private PipeCollection _pipes;
	private Bird _player1;
	private Bird _player2;
	private int[] _p1Colour = {255, 255, 0};
	private int[] _p2Colour = {0, 255, 255};
		
	private int _score1;
	private int _score2;
	private int _highscore;
	
	private static final PVector GRAVITY = new PVector(0, (float)0.9999);

	
	private Gamemode gamemode = Gamemode.MENU;
	private int _players = 1;
	
	public static void main(String[] args) {
		PApplet.main("Sketch");
	}
	
	public void settings() {
		size(WIDTH, HEIGHT);
	}
	
	public void setup() {
		_pipes = new PipeCollection(this);
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
			text("Score: " + _score1, 20, 40);
			text("Highscore: " + _highscore, 20, 80);
			text("Gen: " + _population.getGeneration(), 20, 120); 
		
			Pipe closestPipe = _pipes.getClosestPipe();
			boolean increaseScore = _pipes.update();
			
			if (increaseScore) {
				_score1++;
				if (_score1 > _highscore) {
					_highscore = _score1;
				}
			}
			
			_population.update(closestPipe, GRAVITY);
			int aliveCount = _population.getAliveCount();
			
			textAlign(RIGHT);
			fill(255);
			text("Alive: " + aliveCount, width-20, 40);
			
			if (aliveCount == 0) {
				_population.reproduce();
				initialise();
			
				_score1 = 0;
			}
			
			
			PVector xBounds = new PVector(0, width/3);
			PVector yBounds = new PVector(height/2, height);
			
			_population.displayNetwork(xBounds, yBounds);
			
		} else if (gamemode == Gamemode.PLAY) {
			textAlign(LEFT);
			text("Player 1: " + _score1, 20, 40);
			if (_players == 2) {
				text("Player 2: " + _score2, 20, 80);
			} 
			
			text("Highscore: " + _highscore, 20, 120);
			
			Pipe closestPipe = _pipes.getClosestPipe();
			boolean increaseScore = _pipes.update();
			
			if (increaseScore) {
				if (_player1.status().equals("alive")) {
					_score1++; 
					
					if (_score1 > _highscore) {
						_highscore = _score1;
					}
				} 
				
				if (_players == 2 && _player2.status().equals("alive")) {
					_score2++;
					
					if (_score2 > _highscore) {
						_highscore = _score2;
					}
				}
			}
						
			if (_player1.status().equals("alive")) {
				_player1.overlap(closestPipe);
				_player1.applyForce(GRAVITY);
				_player1.move();
				_player1.display();
			}
			
			if (_players == 2 && _player2.status().equals("alive")) {
				_player2.overlap(closestPipe);
				_player2.applyForce(GRAVITY);
				_player2.move();
				_player2.display();
			}
			
			if (_player1.status().equals("dead") || (_players == 2 && _player2.status().equals("dead"))) {
				startGame();
			}
		}
	}
	
	private void initialise() {
		_pipes = new PipeCollection(this);
	}
	
	private void startGame() {
		_player1 = new Bird(this, _p1Colour);
		
		if (_players == 2) {
			_player2 = new Bird(this, _p2Colour);
		}
		
		_score1 = 0;
		_score2 = 0;
		
		initialise();
	}
	
	public void keyPressed() {
		if (gamemode == Gamemode.MENU) {
			if (keyCode == UP) {
				gamemode = Gamemode.AI;
				
				_score1 = 0;
				_score2 = 0;
				_highscore = 0;
				
				_population = new Population(this);

				initialise();
				
			} else if (keyCode == DOWN) {
				gamemode = Gamemode.PLAY;
				_players = 1;
				
				_score1 = 0;
				_score2 = 0;
				_highscore = 0;
				
				startGame();
				initialise();
			}
		}
		if (gamemode == Gamemode.AI) {
			if (keyCode == 82) {
				_population.reproduce();
				initialise();
			}
		}
		if (gamemode == Gamemode.PLAY) {
			if (keyCode == 49) {
				_players = 1;
				_highscore = 0;
				_score1 = 0;
				
				startGame();
			} else if (keyCode == 50) {
				_players = 2;

				_highscore = 0;
				
				startGame();
			}
			
			if (_player1.status().equals("alive") && (keyCode == UP || keyCode == 32)) {
				_player1.jump();
			}
			
			if (_players == 2 && _player2.status().equals("alive") && keyCode == 87) {
				_player2.jump();
			}
		}
		
		
		if (keyCode == 80) {
			gamemode = Gamemode.MENU;
		}
	}
}
