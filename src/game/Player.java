    
package game;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This is a player class. It is to create an object for all the
 * players that are playing; keeping track of their name, score,
 * order and their hand.
 *
 * @author Ruiming Zeng, Z Yang, Martin Cheung
 */
public class Player {

    private String name;
    private int score;
    private LinkedList<Integer> hand;
    private boolean active = true;

    private Game game;

    /**
     * Instantiating a player with their name, player number
     * and a hand.
     *
     * @param name this player's name
     */
    public Player(Game game, String name) {
        this.game = game;
        this.name = name;
        this.hand = game.createHand();
    }

    public void give(int card) {
        if (card < 0)
            throw new IllegalArgumentException("Invalid Card: " + card);
        
        hand.add(card);
        updateHand();
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
            int thisCard = itr.next();
            if (thisCard == card)
                count++;
        }

        return count;
    }

    /**
     * When a person got "Go-Fished", they add a card to their hand
     *
     * @return true if the player got a card from deck; false otherwise
     */
    public boolean goFish() {
        if (game.cardsLeft() != 0) {
            this.hand.addLast(game.draw());
//            System.out.println("Go Fish! -> " + hand);
            updateHand();
            return true;
        }

        return false;
    }

    /**
     * Sort this player's hand and check for four of a kind,
     * if so increase their point by 1 and remove those cards,
     * if not continue.
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
                	game.matched(); // signal there is a match to the game class
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
        
        setActive(hand.size() != 0); // see if the player still has any cards
    }
    
    /**
     * Determines if a player has cards or not
     */
    public void setActive(boolean active) {
        this.active = active;

    	// if the player is not active, remove from order queue
    	if(!active) {
    		game.removePlayer(this);
    	}
    }

    /**
     * A getter for whether or not this player is active
     * @return a boolean value of active or not
     */
    public boolean isActive() {
    	return active;
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

    public Game getGame() {
        return game;
    }

    /**
     * The toString of the player class would display the 
     * number of points this player has.
     * @return return the toString of this player
     */
    @Override 
    public String toString() {
    	return String.format("%s has %d points", name, score);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            Player p = ((Player) obj);
            return p.getName().equals(name) && p.getScore() == score;
        }

        return false;
    }
}