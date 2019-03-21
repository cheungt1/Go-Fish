package game;

public class Method_Test {
	public static void main(String[] args) {
		Game.shuffle();
		Player a = new Player("john");
		Object[] aHand = a.getHand().toArray();
		
		Player b = new Player("doe");
		Object[] bHand = b.getHand().toArray();
		
		System.out.println(a.toString() +"\n");
		for(int i = 0; i < aHand.length; i++) {
			System.out.println(aHand[i]);
		}
		System.out.println(b.toString()+"\n");
		for(int i = 0; i < bHand.length; i++) {
			System.out.println(bHand[i]);
			System.out.println("test");
		}
	}
}
