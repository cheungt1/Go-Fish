package game;

import java.io.Serializable;
import java.util.*;

/**
 * This is the game class of the Go Fish game.
 * It keeps track of the number of players in the game (max 4) 
 * and the players that the players are in (First come first serve).
 * 
 * @author Ruiming Zeng, Z Yang, Martin Cheung
 */
public class Game implements Serializable {

	private Deque<Player> players;
	private Player current;
	private int numPlayers;
	private boolean isEnded;
	
	private int deckPos;
	private int[] deck = {
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
			27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
			40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52
	};
	private int matched;

	private boolean ended;

	/**
	 * There is only a zero constructor because
	 * it does not make sense to have a game object.
	 * However it is responsible for resetting the game,
	 * thus on instantiation, it resets the deck position, player count and players,
	 * and it shuffles the deck.
	 */
	public Game() {
		players = new LinkedList<>();
		current = null;
		numPlayers = 0;
		deckPos = 0;
		ended = false;
		matched = 0;

		shuffle();
	}

	public Player addPlayer(String name) {
        Player player = new Player(this, name);
        return join(player) ? player : null;
	}

	/**
	 * When a player joins, the player count is increased by one
	 * and it is added to the players list.
     *
     * @return true if the player has successfully joined; false otherwise
	 */
	public boolean join(Player player) {
	    if (players.size() < 4) {
            numPlayers++;
            players.addLast(player);

            return true;
        }

	    return false;
	}

	/**
	 * Determines which player is going next using a Queue implementation.
	 * The first player is removed and returned, then added back to the queue.
	 * @return a player number
	 */
	@Deprecated
	public Player nextPlayer() {
		current = players.removeFirst();
		players.addLast(current);

		return current;
	}

	/**
	 * Remove a player from the players queue once they have no cards
	 * @param player the player whose hand is empty
	 */
	@Deprecated
	public void removePlayer(Player player) {
		players.removeIf(player1 -> player1 == player);
	}

	/**
	 * Shuffles the elements of the deck into different
	 * indices on the array.
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
	 * When a player joined, their first hand is created.
	 * Each player receives 5 card.
	 * @return a linked list of card for this player
	 */
	public LinkedList<Integer> createHand() {
		LinkedList<Integer> n = new LinkedList<>();

		for(int i = 0; i < 5; i++) {
			n.add(draw());
		}

		Collections.sort(n);

		return n;
	}
	
	public boolean isEnded() {
		for (Player p: players) {
			/*
			 * if there is one player that is active,
			 * then allInActive would be false
			 */
			if (p.isActive()) {
				return false;
			}
		}

		/*
		 * game ends when there are 13 matched (all cards are found)
		 * or when all players are inactive
		 */
		return matched == 13;
	}
	
	/**
	 * Called when there is 4-of-a-kind by a player.
	 *
	 */
	public void matched() {
		matched++;
	}

	public int getMatched() {
		return matched;
	}

	public int numPlayers() {
		return numPlayers;
	}

	public int deckPos() {
		return deckPos;
	}

	public int cardsLeft() {
		return deck.length - deckPos;
	}

	public int draw() {
		return convert(deck[deckPos++]);
	}

	public List<Player> players() {
	    return ((LinkedList<Player>) players);
    }

    public Player findPlayer(String name) {
        for (Player p : players) {
            if (p.getName().equals(name))
                return p;
        }

	    return null;
    }

    protected Deque<Player> playerQueue() {
	    return players;
    }

    public static String toRank(int card) {
	    card = convert(card);

	    switch (card) {
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default: return Integer.toString(card);
        }
    }

    public static int toCard(String rank) {
	    rank = rank.trim();
	    if (rank.length() > 2)
	        throw new IllegalArgumentException();
	    else if (rank.matches("[0-9]+")) {
	        int card = Integer.parseInt(rank);
	        if (card < 1 || card > 10)
	            throw new IllegalArgumentException();
	        return card;
        }

	    switch (rank) {
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            default: throw new IllegalArgumentException();
        }
    }

	private static int convert(int card) {
		// x = 1(A): ((1 - 1) % 13) + 1 = 1(A)
		// x = 12(Q): ((12 - 1) % 13) + 1 = 12(Q)
		// x = 13(K): ((13 - 1) % 13) + 1 = 13(K)
		// x = 14(A): ((14 - 1) % 13) + 1 = 1(A)
		return ((card - 1) % 13) + 1;
	}
}