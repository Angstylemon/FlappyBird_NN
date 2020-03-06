import java.util.ArrayList;
import java.util.List;

import NeuralNetwork.Painter;
import processing.core.PApplet;
import processing.core.PVector;

public class Population extends PApplet {
	private final int POPULATION_SIZE = 100;

	private PApplet _parent;
	private List<Bird> _population;
	private List<Bird> _activePopulation;

	
	private int _generation;
	private double _mutationRate = 0.04;	
	
	private Bird _bestBird;
	private Painter _networkPainter;
	
	public Population (PApplet parent) {
		_parent = parent;
		
		_population = new ArrayList<Bird>();
		_activePopulation = new ArrayList<Bird>();

		for (int i = 0; i < POPULATION_SIZE; i++) {
			Bird newBird = new Bird(_parent);
			_population.add(newBird);
			_activePopulation.add(newBird);
		}
		
		_generation = 1;
		
		_bestBird = _population.get(0);
		_networkPainter = new Painter(_parent, _bestBird.nn());
	}
	
	public void update(Pipe closestPipe, PVector force) {
		for (int i = _activePopulation.size()-1; i >= 0; i--) {
			Bird bird = _activePopulation.get(i);
			bird.overlap(closestPipe);
			
			if (bird.status().equals("dead")) {
				_activePopulation.remove(bird);
			} else {
				bird.process(closestPipe);
				bird.applyForce(force);
				bird.move();
				bird.display();
			}
		}
	}
	
	public void displayNetwork(PVector xBounds, PVector yBounds) {
		if (_bestBird.status().equals("dead")) {
			int randBird = (int)(Math.random()*_activePopulation.size());
			
			_bestBird = _activePopulation.get(randBird);
			_networkPainter.setNetwork(_bestBird.nn());
		}
		
		_networkPainter.displayNetwork(xBounds, yBounds);
	}
	
	public void reproduce() {
		float maxFitness = 1;
		_activePopulation.clear();
		
		for (int i = 0; i < _population.size(); i++) {
			if (_population.get(i).fitness() > maxFitness) {
				maxFitness = _population.get(i).fitness();
			}
		}
		
		float fitnessSum = 0;
		for (int i = 0; i < _population.size(); i++) {
			_population.get(i).evaluate(maxFitness, 2);
			fitnessSum += _population.get(i).fitness();
		}
		
		List<Bird> newPopulation = new ArrayList<Bird>();
		for (int i = 0; i < POPULATION_SIZE; i++) {
			double fitnessIndex = Math.random()*fitnessSum;
			int agentIndex = -1;
			
			while (fitnessIndex > 0) {
				agentIndex++;
				fitnessIndex -= _population.get(agentIndex).fitness();
			}
			
			Bird newBird = _population.get(agentIndex).getCopy(_mutationRate);
			newPopulation.add(newBird);
			_activePopulation.add(newBird);
		}
		
		_population = newPopulation;
		_generation++;
	}
	
	public Bird getBestBird() {
		return _bestBird;
	}
	
	public int getGeneration() {
		return _generation;
	}
	
	public int getAliveCount() {
		return _activePopulation.size();
	}
}
