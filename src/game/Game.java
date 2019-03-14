package game;

import java.util.LinkedList;
import java.util.Queue;

public class Game {
	private int[] deck = new int[52];
	Queue<Integer> order = new LinkedList<>();
	
	public Game() {
		
	}
	
	private void start() {
		
	}
	
	private int playerTurn() {
		Integer first = order.remove();
		order.add(first);
		
		return first;
	}
	
}
