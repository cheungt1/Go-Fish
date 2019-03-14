package game;

import java.util.LinkedList;
import java.util.Queue;

public class Game {
	private int[] deck = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13,
						 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 ,25, 26,
						 27, 28 ,29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 
						 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52};
	private Queue<Integer> order = new LinkedList<Integer>();
	private static int numPlayer = 1;
	
	public Game() {
	}
	
	public void start() {
		shuffle();
		initialiseHand();
	}
	
	public void join() {
		order.add(numPlayer);
		numPlayer++;
	}
	private int playerTurn() {
		Integer first = order.remove();
		order.add(first);
		
		return first;
	}
	
	private void shuffle() {
		
	}
	
	private void initialiseHand() {
		
	}
	
}
