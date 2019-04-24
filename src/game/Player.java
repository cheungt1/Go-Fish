
package game;

import java.io.Serializable;
import java.util.*;

/**
 * This is a player class. It is to create an object for all the
 * players that are playing; keeping track of their name, score,
 * order and their hand.
 *
 * @author Ruiming Zeng, Z Yang, Martin Cheung
 */
public class Player implements Serializable {

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
        this.hand = new LinkedList<>();
//        this.hand = game.createHand();
    }

    /**
     * Give this player a card.
     *
     * @param card the card value
     * @throws IllegalArgumentException for invalid card value
     */
    public void give(int card) {
        if (card < 0)
            throw new IllegalArgumentException("Invalid Card: " + card);

        hand.add(card);
        updateHand();
    }

    /**
     * Give this player a specified number of certain card.
     *
     * @param card the card value
     * @param num  the number of card
     */
    public void give(int card, int num) {
        for (; num > 0; num--)
            give(card);
    }

    /**
     * Give this player a list of cards.
     *
     * @param cards the list of cards
     */
    public void give(Collection<Integer> cards) {
        for (int card : cards) {
            give(card);
        }
    }

    /**
     * Take a card from this player.
     *
     * @param card the card value
     * @return the card taken
     * @throws IllegalArgumentException for invalid card value
     */
    public boolean take(int card) {
        if (card < 0)
            throw new IllegalArgumentException("Invalid Card: " + card);

        ListIterator<Integer> itr = hand.listIterator();
        while (itr.hasNext()) {
            int c = itr.next();
            if (c == card) {
                itr.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Take all cards of the given rank from this player.
     *
     * @param rank the card rank
     * @return number of cards taken
     */
    public int take(String rank) {
        ListIterator<Integer> itr = hand.listIterator();
        int removed = 0;

        while (itr.hasNext()) {
            int card = itr.next();
            if (Game.toRank(card).equals(rank)) {
                itr.remove();
                removed++;
            }
        }

        return removed;
    }

    /**
     * Check if this player has the specified card in hand.
     *
     * @param card the number value of the card asked
     * @return the number of card this player has in hand; 0 if none
     */
    public boolean hasCard(int card) {
        return hand.contains(card);
    }

    public int hasCard(String rank) {
        Iterator<Integer> itr = hand.iterator();
        int count = 0;
        while (itr.hasNext()) {
            if (Game.toRank(itr.next()).equals(rank))
                count++;
        }
        return count;
    }

    /**
     * When a person got "Go-Fished", they add a card to their hand
     *
     * @return the card this player got from deck; 0 if none
     */
    public int goFish() {
        int draw = game.draw();
        if (draw != 0) {
            hand.addLast(draw);
            updateHand();
        }

        return draw;
    }

    /**
     * Sort this player's hand and check for four of a kind,
     * if so increase their point by 1 and remove those cards,
     * if not continue.
     */
    public void updateHand() {
        // sort the hand in order to remove consecutive elements
        hand.sort(Game.IGNORE_SUIT);

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
                    matched(); // signal there is a match to the game class
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
     * Called when there is 4-of-a-kind by a player.
     */
    public void matched() {
        score++;
    }

    /**
     * Determines if a player has cards or not
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * A getter for whether or not this player is active
     *
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

    /**
     * A getter for this player's associated game.
     *
     * @return the game this player is in
     */
    public Game getGame() {
        return game;
    }

    /**
     * The toString of the player class would display the
     * number of points this player has.
     *
     * @return return the toString of this player
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks whether the given object is equal to this player.
     *
     * @param obj the object
     * @return true if obj = this player; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            Player p = ((Player) obj);
            return p.getName().equals(name) && p.getScore() == score;
        }

        return false;
    }
}