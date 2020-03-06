import processing.core.PApplet;

public class Game extends PApplet {
	PApplet _parent;
	
	private int _score;
	private int _highscore;
	
	public Game(PApplet parent) {
		_parent = parent;
		
		_score = 0;
		_highscore = 0;
	}
}
