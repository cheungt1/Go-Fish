package game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import static game.Card.Suit;

public class Game {

	private Queue<Integer> order = new LinkedList<>();
	private static int numPlayer = 0;

	private LinkedList<Card> deck;

	public Game() {
		init();
	}

	private void init() {
		deck = new LinkedList<>();
		Suit[] suits = {Suit.DIAMOND, Suit.CLUB, Suit.HEART, Suit.SPADE};

		for (int i = 0; i < 52; i++) {
			deck.add(new Card((i % 13) + 1, suits[i / 13]));
		}
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
		/*Random rnd = new Random();

		for (int i = 0; i < deck.length; i++) {
			*//*
			 * range of [0,51]
			 * swapping cards between current location and a random location
			 *//*
			int n = rnd.nextInt(deck.length);

			int temp = deck[n];
			deck[n] = deck[i];
			deck[i] = temp;
		}*/

		Collections.shuffle(deck);
	}

	private void initialiseHand() {

	}

}
