package game;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * This is the game class of the Go Fish game.
 * It keeps track of the number of players in the game (max 4) 
 * and the order that the players are in (First come first serve).
 * 
 * @author Ruiming Zeng, Z Yang, Martin Cheung
 */
public class Game {

	private static Queue<Integer> order = new LinkedList<>();
	static int numPlayer = 0;
	
	public static int deckPos = 0;
	public static int[] deck = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 
			  			  		14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 
			  			  		27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 
			  			  		40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52 };
	public static int matched = 0;

	/**
	 * There is only a zero constructor because 
	 * it does not make sense to have a game object.
	 * However it is responsible for resetting the game,
	 * thus on instantiation, it resets the deck position, player count and order,
	 * and it shuffles the deck.
	 */
	public Game() {
		deckPos = 0;
		numPlayer = 0;
		order.clear();
		shuffle();
	}

	/**
	 * When a player joins, the player count is increased by one
	 * and it is added to the order list.
	 * @return a number to this Player, order number
	 */
	public static int join() {
		numPlayer++;
		order.add(numPlayer);
		
		return numPlayer;
	}

	/**
	 * Determines which player is going next using a Queue implementation.
	 * The first player is removed and returned, then added back to the queue.
	 * @return a player number
	 */
	static int playerTurn() {
		Integer first = order.remove();
		
		order.add(first);

		return first;
	}
	
	/**
	 * Remove a player from the order queue once they have no cards
	 * @param n the player whose hand is empty
	 */
	static void removePlayer(int n) {
		Iterator<Integer> itr = order.iterator();
		while(itr.hasNext()) {
			if(itr.next() == n) {
				itr.remove();
			}
		}
	}

	/**
	 * Shuffles the elements of the deck into different
	 * indices on the array.
	 */
	public static void shuffle() {
		Random rnd = new Random();

		for (int i = 0; i < deck.length; i++) {
			/*
			 * range of [0,51]
			 * swapping cards between current location and a random location
			 */
			int n = rnd.nextInt(deck.length);

			
			//  basic swapping between 2 indices in an array
			 
			int temp = deck[n];
			deck[n] = deck[i];
			deck[i] = temp;
		}
	}

	/**
	 * When a player joined, their first hand is created.
	 * Each player receives 5 card.
	 * @return a linked list of card for this player
	 */
	public static LinkedList<Integer> createHand() {
		LinkedList<Integer> n = new LinkedList<>();
		
		for(int i = 0; i < 5; i++) {
			n.add(deck[deckPos]); 
			deckPos++;
		}
		
		Collections.sort(n);
		
		return n;
	}
	
	/**
	 * Called when there is 4-of-a-kind by a player.
	 * 
	 */
	public static void totalMatched() {
		matched++;
	}
}
