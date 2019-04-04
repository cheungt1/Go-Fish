package game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static game.Util.*;

public class GameServer {

    private final Game game;

    private final int port;
    private final String IP;
    private final ServerSocket serverSocket;

    private PlayerHandler[] players;
    private int numPlayers;

    public GameServer(Game game, int port) throws IOException {
        this.game = game;
        this.port = port;
        this.players = new PlayerHandler[4];
        this.numPlayers = 0;
        this.serverSocket = new ServerSocket(port);
        this.IP = serverSocket.getInetAddress().getHostAddress();
    }

    public void start() {
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
                    writeString(os, "[Welcome to Go Fish! Please enter your name]: ");

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

    public Game getGame() {
        return game;
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return IP;
    }

    public List<String> players() {
        List<String> names = new ArrayList<>();
        for (PlayerHandler handler : players) {
            if (handler != null) {
                names.add(handler.getPlayer().getName());
            }
        }

        return names;
    }

    private class PlayerHandler implements Runnable {

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
                    if (!game.isEnded() && is.available() > 0) {
                        String selection = is.readUTF();

                        String[] slct = selection.split("[\\s+]");

                        // TODO: handle invalid player name
                        Player other = game.findPlayer(slct[0]);
                        int targetCard = Integer.parseInt(slct[1]);

                        List<Integer> myHand = player.getHand();
                        List<Integer> otherHand = other.getHand();

                        int n;
                        if ((n = other.take(targetCard)) > 0) {
                            player.give(targetCard, n);
                        } else {
                            player.goFish();
                        }

                        writeString(os, String.format("[Player %s has %d %s's!]%n", slct[0], n, slct[1]));
                        System.out.println("wrote");
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