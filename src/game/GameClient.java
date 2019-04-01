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

			DataInputStream is = new DataInputStream(socket.getInputStream());
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());

			Scanner input = new Scanner(System.in);

			System.out.print(is.readUTF()); // welcoming message

			// send player name to server
//			writeWithThread(os, GUI.getUserName());
			writeWithThread(os, input.nextLine());

			Player me = (Player) (new ObjectInputStream(is).readObject());
			List<Integer> hand = me.getHand();

//			System.out.println(is.readUTF()); // joined message

            while (game.isEnded()) {
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

						writeWithThread(os, selection);

						String resultMessage = is.readUTF();
						String[] rm = resultMessage.split("[\\s+]");
						cardsRec = Integer.parseInt(rm[3]);
                        System.out.printf("[Received %d %d's]%n", cardsRec, card);

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
