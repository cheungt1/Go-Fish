package game;

import java.io.Serializable;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

/**
 * This class represents a virtual GoFish game in which contains a
 * queue of players and other properties of a GoFish game. This class
 * should be used to create a GoFish game server and be handled by
 * only the server.
 *
 * @author Ruiming Zeng, Z Yang, Martin Cheung
 * @see GameServer
 */
public class Game implements Serializable {

    /**
     * A Deque to represent the order of the players' turns as a
     * queue. This queue is to be handled by the GameServer class
     * and thus calling methods in this class will not change the
     * ordering of this queue.
     */
    private Deque<Player> players;

    /**
     * The current player whose turn is being handled by the server.
     */
    private Player current;

    /**
     * The most recent winner of this game.
     */
    private Player lastWinner;

    /**
     * The total number of players in this game.
     */
    private int numPlayers;

    /**
     * Whether or not this game has started.
     */
    private boolean isEnded;

    /**
     * An index pointer to the virtual deck array, usually used to
     * incrementally traverse through the deck array to create a
     * "drawing" effect.
     */
    private int deckPos;

    /**
     * An array of integers that represents a deck of cards. Note
     * that this array should not be changed throughout the game;
     * instead, change deckPos.
     */
    private int[] deck = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
    };

    /**
     * Number of total matches (four-of-a-kinds) achieved by current
     * players.
     */
    private int matched;

    /**
     * Whether or not this game has started.
     */
    private boolean started;

    /**
     * The default constructor of this class which instantiates all
     * instance variables.
     */
    public Game() {
        players = new LinkedList<>();
        current = null;
        numPlayers = 0;
        deckPos = 0;
        started = false;
        matched = 0;
        lastWinner = null;
    }

    /**
     * Start this game and shuffle the deck.
     */
    public void start() {
        if (numPlayers < 2)
            throw new IllegalStateException("At least 2 players are needed: " + numPlayers);

        shuffle();

        for (Player p : players) {
            p.give(createHand());
        }

        started = true;
    }

    public void end(Player winner) {
        started = false;
        deckPos = 0;
        matched = 0;
        current = null;

        shuffle();

        if (winner != null) {
            lastWinner = winner;
        }
    }

    /**
     * Add a new player to this game with the give name. This method
     * creates and returns a Player object upon a successful addPlayer.
     *
     * @param name the name of the player
     * @return the Player object created, or null if fail to addPlayer
     */
    public Player addPlayer(String name) {
        Player player = new Player(this, name);
        return addPlayer(player) ? player : null;
    }

    /**
     * Add a new player to this game with the given Player object.
     * This method is not intended for outside use.
     * <p>
     * This method checks for the number of players and add the
     * Player if this game is not full and not started. It returns
     * true or false based on whether or not the given player joined
     * successfully.
     *
     * @return true if the player has successfully joined; false otherwise
     */
    private boolean addPlayer(Player player) {
        if (!isFull() && !started) {
            numPlayers++;
            players.addLast(player);

            return true;
        }

        return false;
    }

    /**
     * Virtually draw a card from the deck, if possible.
     *
     * @return the card value drawn; 0 if deck is empty
     */
    public int draw() {
        return deckPos < 52 ? deck[deckPos++] : 0;
    }

    /**
     * Return the player who is next to make a move.
     *
     * @return the player object of next turn
     */
    public Player nextPlayer() {
        return current;
    }

    /**
     * Remove a player object from the players queue.
     *
     * @param player the player to be removed
     */
    public boolean removePlayer(Player player) {
        boolean removed = players.remove(player);
        if (removed)
            numPlayers--;

        return removed;
    }

    /**
     * Shuffles the elements of the deck into different indices in
     * the array.
     */
    public void shuffle() {
        Random rnd = new Random();

        for (int i = 0; i < deck.length; i++) {
            /*
             * range of [0,51]
             * swapping cards between current location and a random location
             */
            int n = rnd.nextInt(deck.length - i);

            //  basic swapping between 2 indices in an array
            int temp = deck[n];
            deck[n] = deck[i];
            deck[i] = temp;
        }
    }


    /**
     * Create a hand, represented by a list of card values.
     * <p>
     * This method should only be used before the game starts.
     *
     * @return a linked list of card
     * @throws IllegalStateException if game is started
     */
    public LinkedList<Integer> createHand() {
        if (started)
            throw new IllegalStateException("Game has already been started");

        LinkedList<Integer> n = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            n.add(draw());
        }

        Collections.sort(n);

        return n;
    }

    /**
     * Return whether or not this game has started.
     *
     * @return true if this game is started; false otherwise
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Return the total number of matches in this game.
     *
     * @return number of matches
     */
    public int getMatched() {
        return matched;
    }

    /**
     * Return the total number of current players in this game.
     *
     * @return the number of players
     */
    public int numPlayers() {
        return numPlayers;
    }

    /**
     * Return the position of the index pointer in the deck.
     *
     * @return the current index
     */
    public int deckPos() {
        return deckPos;
    }

    /**
     * Return the number of remaining cards in the deck of this game.
     *
     * @return the number of cards left in the deck
     */
    public int cardsLeft() {
        return deck.length - deckPos;
    }

    /**
     * Return the most recent winner of this game.
     *
     * @return the last winner
     */
    public Player getLastWinner() {
        return lastWinner;
    }

    /**
     * Search for a player in this game with the given name.
     *
     * @param name the name to look for
     * @return the Player object with name; null if not found
     */
    public Player findPlayer(String name) {
        for (Player p : players) {
            if (p.getName().equals(name))
                return p;
        }

        return null;
    }

    /**
     * Return the order of players in this game in a Deque that
     * represents a queue.
     * <p>
     * This method is intended to be used by the game server in which
     * the order of the players is manipulated accordingly with server
     * and client communications.
     *
     * @return a Deque of Players
     */
    protected Deque<Player> playerQueue() {
        return players;
    }

    /**
     * Return whether or not this game is full (4 players).
     *
     * @return true if this game is full; false otherwise
     */
    private boolean isFull() {
        return numPlayers >= 4;
    }

    /**
     * Given a card value, return its string representation.
     * (e.g. 13 --> "K")
     *
     * @param card the card value
     * @return the string representation of card
     */
    public static String toRank(int card) {
        if (card < 0)
            throw new IllegalArgumentException("Invalid Card: " + card);

        card = convert(card);

        switch (card) {
            case 1:
                return "A";
            case 11:
                return "J";
            case 12:
                return "Q";
            case 13:
                return "K";
            default:
                return Integer.toString(card);
        }
    }

    /**
     * Given a card rank as a string, return its corresponding card
     * value, if possible.
     * (e.g. "K" --> 13)
     *
     * @param rank the card rank
     * @return the card value
     * @throws IllegalArgumentException for invalid rank argument
     */
    public static int toCard(String rank) {
        String exMsg = "Invalid rank: " + rank;

        rank = rank.trim();
        if (rank.length() > 2)
            throw new IllegalArgumentException(exMsg);
        else if (rank.matches("[0-9]+")) {
            int card = Integer.parseInt(rank);
            if (card < 1 || card > 10)
                throw new IllegalArgumentException(exMsg);
            return card;
        }

        switch (rank) {
            case "A":
                return 1;
            case "J":
                return 11;
            case "Q":
                return 12;
            case "K":
                return 13;
            default:
                throw new IllegalArgumentException(exMsg);
        }
    }

    /**
     * Simplify a card value to 1 - 13.
     * <p>
     * This method should be used when a card value could be greater
     * than 13 for representing other cards with different suits in a
     * deck.
     *
     * @param card the card value
     * @return a reduced card value from 1 - 13
     */
    private static int convert(int card) {
        if (card < 1)
            throw new IllegalArgumentException("Invalid card: " + card);

        // x = 1(A): ((1 - 1) % 13) + 1 = 1(A)
        // x = 13(K): ((13 - 1) % 13) + 1 = 13(K)
        // x = 14(A): ((14 - 1) % 13) + 1 = 1(A)
        // x = 43(4): ((43 - 1) % 13) + 1 = 4(4)
        return ((card - 1) % 13) + 1;
    }
}