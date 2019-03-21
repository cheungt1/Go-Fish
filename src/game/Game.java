package game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static game.Card.Suit;

public class Game {

	private static Queue<Integer> order = new LinkedList<>();
	private static int numPlayer = 0;
	
	private static int deckPos = 0;
	private static int[] deck = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 
			  			  14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 
			  			  27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 
			  			  40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52 };

	public Game() {
		deckPos = 0;
		numPlayer = 0;
		shuffle();
	}

	public static void join() {
		order.add(numPlayer);
		numPlayer++;
	}

	private int playerTurn() {
		Integer first = order.remove();
		
		order.add(first);

		return first;
	}
	
	public int getNumPlayer() {
		return numPlayer;
	}

	public static void shuffle() {
		Random rnd = new Random();

		for (int i = 0; i < deck.length; i++) {
			/*
			 * range of [0,51]
			 * swapping cards between current location and a random location
			 */
			int n = rnd.nextInt(deck.length);

			int temp = deck[n];
			deck[n] = deck[i];
			deck[i] = temp;
		}
	}

	public static LinkedList<Integer> createHand() {
		LinkedList<Integer> n = new LinkedList<>();
		
		for(int i = deckPos; i < 5; i++) {
			n.add(deck[i]);
		}
		
		deckPos += 5;	
		
		return n;
	}
}
