package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static game.Util.writeString;

public class GameClient {

    //	private GameServer server;
    private Socket socket;

    public GameClient() throws IOException {
        this(GameServer.getIP(), GameServer.getPort());
    }

    /**
     * Create a game client with the given host name and port.
     *
     * @param host the host name or IP address
     * @param port the port number
     * @throws IOException for any errors regards to creating the socket
     */
    public GameClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        start();
    }

    private void start() {
        try (DataOutputStream os = new DataOutputStream(socket.getOutputStream());
             DataInputStream is = new DataInputStream(socket.getInputStream())) {

            Scanner input = new Scanner(System.in);

            System.out.print(is.readUTF()); // welcoming message

            // send player name to server
//			writeString(os, GUI.getUserName());
            String myName = input.nextLine();
            writeString(os, myName);

            // get signal from server to ensure that player has been created
            is.readInt();

            while (true) {
                // get hand from server
                String myHand = is.readUTF();
                System.out.println("Your hand: " + myHand);

                // get the name of the player of this turn
                String thisTurn = is.readUTF();

                // if it's my turn
                if ((myHand.length() != 0) && (thisTurn.equals(myName))) {
                    System.out.println("[It's my turn!]");

//                    myHand = is.readUTF();
//                    System.out.printf("My hand: [%s]\n", myHand);

                    System.out.println(is.readUTF());

                    // prompt and send target player's name to server
                    String playerChoice = input.nextLine();
                    writeString(os, playerChoice);

                    System.out.println(is.readUTF());

                    // prompt and send target card to server
                    String card = input.nextLine();
                    writeString(os, card);

                    // read from server the number of cards received
                    int cardsRecv = is.readInt();
                    System.out.printf("[Received %d %s's]\n", cardsRecv, card);

                    if (cardsRecv == 0)
                        System.out.println("[Go Fish!]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}