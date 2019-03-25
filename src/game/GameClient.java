package game;

import java.io.*;
import java.net.Socket;

import static game.Util.*;

public class GameClient {
	private final static String serverHostIP = "";
	private final static int port = 0;
	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket(serverHostIP, port);
			
			DataInputStream is = new DataInputStream(socket.getInputStream());
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			
			// send player name to server
			writeWithThread(os,GUI.getUserName());
			
			Player you = new Player(GUI.getUserName());
			
			while(Game.matched != 13) {
				if(Game.playerTurn() == you.getPlayerNum()) {
					String playerChoice = GUI.getPlayerChoice();
					int card = Integer.parseInt(GUI.getCardValue());
					
					String selection = playerChoice + " " + card;
					
					writeWithThread(os, selection);
					
					
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
