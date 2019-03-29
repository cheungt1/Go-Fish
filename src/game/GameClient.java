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

			while (Game.matched != 13) {
				Player currentPlayer = new Player("x");// = GameServer.getNextPlayer();

				int cardsRec;

				do {
					String playerChoice = GUI.getPlayerChoice();
					int card = Integer.parseInt(GUI.getCardValue());

					String selection = playerChoice + " " + card;

					writeWithThread(os, selection);

					currentPlayer.updateHand();

					String resultMessage = is.readUTF();
					String[] rm = resultMessage.split("[\\s+]");
					cardsRec = Integer.parseInt(rm[3]);
				} while (cardsRec != 0);
			}

			is.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GameServer getServer() {
		return server;
	}
}
