package game;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This is a player class. It is to create an object for all the players that
 * are playing; keeping track of their name, score, order and their hand.
 *
 * @author Ruiming Zeng, Z Yang, Martin Cheung
 */
public class Player {

	private String name;
	private int score;
	private LinkedList<Integer> hand;
	private static boolean isActive = true;

	/**
	 * Instantiating a player with their name, player number and a hand.
	 *
	 * @param name this player's name
	 */
	public Player(String name) {
		this.name = name;
		this.hand = Game.createHand();
		Game.join(this);
	}

	public void give(int card) {
		if (card < 0)
			throw new IllegalArgumentException("Invalid Card: " + card);

		hand.add(card);
	}

	public void give(int card, int num) {
		for (; num > 0; num--)
			give(card);
	}

	public int take(int card) {
		if (card < 0)
			throw new IllegalArgumentException("Invalid Card: " + card);

		int n = hasCard(card);
		if (n > 0)
			hand.removeIf(i -> i.equals(card));

		return n;
	}

	/**
	 * Check if this player has the specified card in hand.
	 *
	 * @param card the number value of the card asked
	 * @return the number of card this player has in hand, 0 if none
	 */
	public int hasCard(int card) {
		Iterator<Integer> itr = hand.iterator();
		int count = 0;
		while (itr.hasNext()) {
			int thisCard = convert(itr.next());
			if (thisCard == card)
				count++;
		}

		return count;
	}

	/**
	 * When a person got "Go-Fished", they add a card to their hand
	 */
	public void goFish() {
		if (Game.deckPos != Game.deck.length) {
			this.hand.addLast(Game.deck[Game.deckPos]);
			updateHand();
			Game.deckPos++;
		}
	}

	/**
	 * Sort this player's hand and check for four of a kind, if so increase their
	 * point by 1 and remove those cards, if not continue.
	 */
	public void updateHand() {
		// sort the hand in order to remove consecutive elements
		Collections.sort(hand);

		// if player has less than 4 cards in hand, skip
		if (hand.size() < 4)
			return;

		// use an iterator to traverse through a linked list
		ListIterator<Integer> itr = hand.listIterator();
		int current = itr.next(); // set current as first element
		int count = 1;

		// while there's more element and no four-of-a-kind is found
		while (itr.hasNext() && count < 4) {
			int next = itr.next(); // get the next element

			if (current == next) { // if current element is the same as next
				// increment count
				if (++count == 4) { // four-of-a-kind is found
					Game.totalMatched(); // signal there is a match to the game class
					// remove 4 elements
					for (int i = 0; i < 4; i++) {
						itr.previous();
						itr.remove();
					}
				}
			} else { // if current is different from next
				count = 1; // reset counter
			}

			current = next; // update current
		}

		setIsActive(); // see if the player still has any cards
	}

	/**
	 * Determines if a player has cards or not
	 */
	public void setIsActive() {
		isActive = hand.size() != 0;

		// if the player is not active, remove from order queue
		if (!isActive) {
			Game.removePlayer(name);
		}
	}

	/**
	 * A getter for whether or not this player is active
	 * 
	 * @return a boolean value of active or not
	 */
	public boolean getIsActive() {
		return isActive;
	}

	/**
	 * A getter for the hand
	 *
	 * @return this player's hand
	 */
	public LinkedList<Integer> getHand() {
		return hand;
	}

	/**
	 * A getter for the score
	 *
	 * @return this player's score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * A getter for this player's name
	 *
	 * @return this player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The toString of the player class would display the number of points this
	 * player has.
	 * 
	 * @return return the toString of this player
	 */
	@Override
	public String toString() {
		return String.format("%s has %d points", name, score);
	}

	private int convert(int card) {
		// x = 1(A): ((1 - 1) % 13) + 1 = 1(A)
		// x = 12(Q): ((12 - 1) % 13) + 1 = 12(Q)
		// x = 13(K): ((13 - 1) % 13) + 1 = 13(K)
		// x = 14(A): ((14 - 1) % 13) + 1 = 1(A)
		return ((card - 1) % 13) + 1;
	}
}
