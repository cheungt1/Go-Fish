package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static game.Util.writeWithThread;

public class GameServer {

    private final Game game;

    private final int port;
    private final String IP;
    private final ServerSocket serverSocket;

    private Player[] players;
    private int numPlayers;

    public GameServer(Game game, int port) throws IOException {
        this.game = game;
        this.port = port;
        this.players = new Player[4];
        this.numPlayers = 0;
        this.serverSocket = new ServerSocket(port);
        this.IP = serverSocket.getInetAddress().getHostAddress();
    }

    public void start() {
        new Thread(() -> {
            try {
                System.out.printf("[Waiting for connection at port %d ...]", port);

                while (true) {
                    // accept player
                    Socket playerSocket = serverSocket.accept();

                    // retrieve i/o streams
                    DataInputStream is = new DataInputStream(playerSocket.getInputStream());
                    DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

                    // ask for username
                    writeWithThread(os, "[Welcome to Go Fish! Please enter your name]: ");

                    String username = is.readUTF();
                    writeWithThread(os, String.format("[Hello, %s!]", username));

                    new Thread(new PlayerHandler(username, is, os)).start();
                }
            } catch (IOException e) {
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
        for (Player p : players)
            names.add(p.getName());

        return names;
    }

    private class PlayerHandler extends Player implements Runnable {

        DataInputStream is;
        DataOutputStream os;

        PlayerHandler(String name, DataInputStream is, DataOutputStream os) {
            super(name);
            this.is = is;
            this.os = os;

            players[numPlayers++] = this;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String selection = is.readUTF();
                    String[] slct = selection.split("[\\s+]");
                    Player other = players[Integer.parseInt(slct[1])];
                    int targetCard = Integer.parseInt(slct[2]);

                    List<Integer> myHand = this.getHand();
                    List<Integer> otherHand = other.getHand();

                    int n;
                    if ((n = other.take(targetCard)) > 0) {
                        this.give(targetCard, n);
                    } else {
                        this.goFish();
                    }

                    writeWithThread(os, String.format("[Player %s has %d %s's!]", slct[1], n, slct[2]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
