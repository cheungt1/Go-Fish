package game;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import static game.Util.*;

public class GameClient {

	private GameServer server;

	public GameClient(GameServer server) {
		this.server = server;

		start();
	}

	public void start() {
		Game game = server.getGame();

		try {
			Socket socket = new Socket(server.getIP(), server.getPort());

			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			DataInputStream is = new DataInputStream(socket.getInputStream());

			Scanner input = new Scanner(System.in);

			System.out.print(is.readUTF()); // welcoming message

			// send player name to server
//			writeString(os, GUI.getUserName());
			String playerName = input.nextLine();
			writeString(os, playerName);

			is.readInt(); // get signal from server
			Player me = server.getGame().findPlayer(playerName);
			List<Integer> hand = me.getHand();

			while (!game.isEnded()) {
				System.out.println("Your hand: " + hand);

                if ((hand.size() != 0) && (game.nextPlayer().equals(me))) {
                    int cardsRec;

                    do {
                        System.out.println("[It's my turn!]");
                        System.out.println("[Choose a player to pick cards from]");
                        System.out.println("Players: " + server.players());

//						String playerChoice = GUI.getPlayerChoice();
                        String playerChoice = input.nextLine();

                        System.out.println("[Pick a card]");
//						int card = Integer.parseInt(GUI.getCardValue());
                        int card = input.nextInt();

						String selection =  playerChoice+ " " + card;

						writeString(os, selection);

						String resultMessage = is.readUTF();
						System.out.println("read");
						String[] rm = resultMessage.split("[\\s+]");
						cardsRec = Integer.parseInt(rm[3]);
                        System.out.printf("[Received %d %d's]%n", cardsRec, card);

						System.out.printf("My hand: %s\n", me.getHand());
                        input.nextLine();
					} while (cardsRec != 0);

                    System.out.println("[Go Fish!]");
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