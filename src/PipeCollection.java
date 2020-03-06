import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class PipeCollection extends PApplet {
	private PApplet _parent;
	
	private static final int PIPE_DISTANCE = 80;
	private int _frameCount;
	
	private List<Pipe> _pipes;
	
	public PipeCollection(PApplet parent) {
		_parent = parent;
		
		_pipes = new ArrayList<Pipe>();
		_pipes.add(new Pipe(_parent));
		
		_frameCount = 0;
	}
	
	public void addPipe() {
		_pipes.add(new Pipe(_parent));
	}
	
	public Pipe getClosestPipe() {
		Pipe closestPipe = _pipes.get(_pipes.size()-1);
		for (int i = _pipes.size()-2; i >= 0; i--) {
			if (_pipes.get(i).getEdge() < closestPipe.getEdge() && _pipes.get(i).getEdge() > Bird.xPos) {
				closestPipe = _pipes.get(i);
			}
		}
		
		return closestPipe;
	}
	
	public boolean update() {
		boolean pipeRemoved = false;
		
		for (int i = _pipes.size()-1; i >= 0; i--) {
			_pipes.get(i).move();
			_pipes.get(i).display();
			if (_pipes.get(i).outOfBounds()) {
				_pipes.remove(_pipes.get(i));
				
				pipeRemoved = true;
			}
		}

		if (_frameCount > PIPE_DISTANCE) {
			addPipe();
			_frameCount = 0;
		}
		
		_frameCount++;
		
		return pipeRemoved;
	}
}
