package game;

import java.util.Collections;
import java.util.LinkedList;

public class Player {
	private String name;
	private int score;
	private LinkedList<Integer> hand = new LinkedList<Integer>();
	
	public Player(String name) {
		this.name = name;
		Game.join();
		this.hand = Game.createHand();
		
	}
	
	public boolean containCard(Integer x) {
		return hand.contains(x%13);
	}
	
	public void cardCheck() {		
		Collections.sort(hand);
		
		Integer current = hand.getFirst();
		Integer count = 1;
		
		for(int i = 1; i < hand.size(); i++) {			
			if(hand.get(i) == current) {
				count++;
				if(count == 4) {
					score++;
				}
			}
			else {
				current = hand.get(i);
				count = 1;
			}
		}	
	}		
	
	public LinkedList<Integer> getHand(){
		return hand;
	}
	
	public String toString() {
		return name;
	}
}
