package game;

import java.util.LinkedList;
import java.util.Queue;

public class Player {
	private String name;
	private LinkedList<Integer> hand = new LinkedList<Integer>();
	
	public Player(String name) {
		this.name = name;
		Game.join();
		this.hand = Game.createHand();
		
	}
	
	public boolean containCard(Integer x) {
		return hand.contains(x%13);
	}
	
	public LinkedList<Integer> getHand(){
		return hand;
	}
	
	public String toString() {
		return name;
	}
}
