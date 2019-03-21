package game;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This is a player class. It is to create an object for all the 
 * players that are playing; keeping track of their name, score, 
 * order and their hand.
 * @author Ruiming Zeng, Z Yang, Martin Cheung
 */
public class Player {
	private String name;
	private int score;
	private int playerNum;
	private LinkedList<Integer> hand = new LinkedList<Integer>();
	
	/**
	 * Instantiating a player with their name, player number
	 * and a hand.
	 * @param name this player's name
	 */
	public Player(String name) {
		this.name = name;
		this.playerNum = Game.join();
		this.hand = Game.createHand();
	}
	
	/**
	 * Sees if the player has the card this player asks fir
	 * @param x the number value of the card asked
	 * @param p the player that is asked
	 * @return true if player has the card, false otherwise
	 */
	public boolean containCard(Integer x, Player p) {
		return p.hand.contains(x%13);
	}
	
	/**
	 * When a person got "Go-Fished", they add a card to their hand
	 */
	public void goFish() {
		if(Game.deckPos != Game.deck.length)
		this.hand.addLast(Game.deck[Game.deckPos]);
		Game.deckPos++;
	}
	
	/**
	 * Check if this player has four of a kind,
	 * if so increase their point by 1 and remove those cards,
	 * if not continue.
	 */
	public void updateHand() {		
		Collections.sort(hand);
		
		Integer current = hand.getLast();
		Integer count = 1;
		
		for(int i = hand.size()-1; i >= 0; i--) {			
			if(hand.get(i) == current) {
				count++;
				if(count == 4) {
					score++;
					
					hand.remove(i);
					hand.remove(i);
					hand.remove(i);
					hand.remove(i);
					
					if(i == hand.size()) {
						i--;
					}
				}
			}
			else {
				current = hand.get(i);
				count = 1;
			}
		}	
	}	
	
	/**
	 * A getter for the hand
	 * @return this player's hand
	 */
	public LinkedList<Integer> getHand(){
		return hand;
	}
	
	/**
	 * A getter for the score
	 * @return this player's score
	 */
	public int getScore() {
		return score;
	}
	
	public static void main(String[] args) {
	}
}
