package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static game.Util.writeInt;
import static game.Util.writeString;

public class GameServer {

    private static Game game;

    private static ServerSocket serverSocket;
    private static String IP;
    private static int port;

    private static PlayerHandler[] players;
    private static int numPlayers;

    static {
        game = new Game();
        port = 8000;

        try {
            serverSocket = new ServerSocket(port);
            IP = serverSocket.getInetAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        players = new PlayerHandler[4];
        numPlayers = game.numPlayers();
    }

    public static void start() {
        new Thread(() -> {
            try {
                System.out.printf("[Waiting for connection at port %d ...]\n", port);

                while (true) {
                    // accept player
                    Socket playerSocket = serverSocket.accept();

                    // retrieve i/o streams
                    DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());
                    DataInputStream is = new DataInputStream(playerSocket.getInputStream());

                    // ask for username
                    writeString(os, "[Server] Welcome to Go Fish! Please enter your name: ");

                    String username = is.readUTF();
                    Player newPlayer = game.addPlayer(username);
                    // ensures that the client can find this new player
                    while (game.findPlayer(username) == null)
                        Thread.sleep(1);
                    os.writeInt(1); // signal the client


//                    writeString(os, String.format("[Hello, %s!]", username));
                    System.out.printf("[%s has joined!]\n", username);

                    new Thread(new PlayerHandler(newPlayer, is, os)).start();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static Game getGame() {
        return game;
    }

    public static int getPort() {
        return port;
    }

    public static String getIP() {
        return IP;
    }

    public static List<String> players() {
        List<String> names = new ArrayList<>();
        for (PlayerHandler handler : players) {
            if (handler != null) {
                names.add(handler.getPlayer().getName());
            }
        }

        return names;
    }

    private static class PlayerHandler implements Runnable {

        Player player;

        DataInputStream is;
        DataOutputStream os;

        PlayerHandler(Player player, DataInputStream is, DataOutputStream os) {
            this.player = player;
            this.is = is;
            this.os = os;


            players[numPlayers++] = this;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (!game.isEnded()) {
                        Player other;
                        String targetName;
                        do {
                            writeString(os, "Choose a player from " + players());
                            targetName = is.readUTF();
                            other = game.findPlayer(targetName);
                        } while (other == null);

                        writeString(os, "[Request for a card]");
                        int targetCard = is.readInt();
                        List<Integer> myHand = player.getHand();
                        List<Integer> otherHand = other.getHand();

                        int n;
                        if ((n = other.take(targetCard)) > 0) {
                            player.give(targetCard, n);
                        } else {
                            player.goFish();
                        }

                        writeInt(os, n);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Player getPlayer() {
            return player;
        }
    }
}