package game;

import java.io.*;
import java.net.Socket;

import static game.Util.*;

public class GameClient {

	private GameServer server;

	public GameClient(GameServer server) {
		this.server = server;

		start();
	}

	public void start() {
		try {
			Socket socket = new Socket(server.getIP(), server.getPort());

			DataInputStream is = new DataInputStream(socket.getInputStream());
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());

			// send player name to server
			writeWithThread(os, GUI.getUserName());
			
			Player currentPlayer = (Player) (new ObjectInputStream(is).readObject());

			Game game = server.getGame();

			while (game.getMatched() != 13) {
				if (game.nextPlayer() == currentPlayer) {
					int cardsRec;
					
					do {
						String playerChoice = GUI.getPlayerChoice();
						int card = Integer.parseInt(GUI.getCardValue());

						String selection = playerChoice + " " + card;

						writeWithThread(os, selection);

						String resultMessage = is.readUTF();
						String[] rm = resultMessage.split("[\\s+]");
						cardsRec = Integer.parseInt(rm[3]);
					} while (cardsRec != 0);
				}
			}

			is.close();
			os.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GameServer getServer() {
		return server;
	}
}
