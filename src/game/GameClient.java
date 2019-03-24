package game;

import java.io.*;
import java.net.Socket;

public class GameClient {
	private final static String serverHostIP = "";
	private final static int port = 0;
	
	public static void main(String[] args) {
		Player you = new Player(GUI.getUserName());
		
		try {
			Socket socket = new Socket(serverHostIP, port);
			
			DataInputStream is = new DataInputStream(socket.getInputStream());
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			
			while(Game.matched != 13) {
				if(Game.playerTurn() == you.getPlayerNum()) {
					String playerChoice = GUI.getPlayerChoice();
					int card = Integer.parseInt(GUI.getCardValue());
					
					
					
					
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
