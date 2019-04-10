package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static game.Util.writeInt;
import static game.Util.writeString;

public class GameServer extends Application {

    private TextArea ta = new TextArea();

    private static Game game;

    private static ServerSocket serverSocket;
    private static String IP;
    private static int port;

    private static PlayerHandler[] players;
    private static int numPlayers;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();
        ta.appendText("[Server] Initialized\n");

        StackPane main = new StackPane(ta);
        main.setPadding(new Insets(5));

        Label IPLabel = new Label("IP: " + IP);
        Label portLabel = new Label("Port: " + port);

        HBox infoHBox = new HBox(30);
        infoHBox.setPadding(new Insets(5));
        infoHBox.setAlignment(Pos.CENTER);
        infoHBox.getChildren().addAll(IPLabel, portLabel);

        BorderPane base = new BorderPane(main);
        base.setTop(infoHBox);

        Scene scene = new Scene(base);
        primaryStage.setScene(scene);
        primaryStage.setTitle("GoFish Server");
        primaryStage.setOnCloseRequest(event -> System.exit(1));
        primaryStage.show();

        ta.setPrefSize(500, 350);

        new Thread(() -> {
            try {
//                System.out.printf("[Waiting for connection at port %d ...]\n", port);
                Platform.runLater(() ->
                        ta.appendText(String.format("[Server] Waiting for connection at port %d ...\n", port)));

                while (true) {
                    // accept player
                    Socket playerSocket = serverSocket.accept();

                    // retrieve i/o streams
                    DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());
                    DataInputStream is = new DataInputStream(playerSocket.getInputStream());

                    // ask for username
//                    writeString(os, "[Server] Welcome to Go Fish! Please enter your name: ");
                    String username = is.readUTF();
                    Player newPlayer = game.addPlayer(username);

                    // ensures that the client can find this new player
                    while (game.findPlayer(username) == null) {
                        Thread.sleep(1);
                    }
                    writeInt(os, 1); // signal the client

                    Platform.runLater(() -> ta.appendText(String.format("[Server] %s has joined!\n", username)));

                    new Thread(new PlayerHandler(newPlayer, is, os)).start();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void initialize() {
        game = new Game();
        port = 8000;

        try {
            serverSocket = new ServerSocket(port);
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        players = new PlayerHandler[4];
        numPlayers = game.numPlayers();
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
                names.add(handler.player.getName());
            }
        }

        return names;
    }

    private static PlayerHandler remove(PlayerHandler player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                PlayerHandler removed = players[i];
                players[i] = null;
                return removed;
            }
        }

        return null;
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
                    // send this player's hand
                    writeString(os, formatHand());

                    // get and send the name of the player of this turn
                    Player thisTurn = game.playerQueue().peekFirst();
                    writeString(os, thisTurn.getName());
                    Platform.runLater(() -> ta.appendText(String.format("[Server] %s's turn\n",
                            thisTurn.getName())));

                    // if that player is this player
                    if (!game.isEnded() && thisTurn.equals(player)) {
                        // send this player's hand
//                        writeString(os, formatHand());

                        // request target player from client
                        writeString(os, "Choose a player from " + players());
                        String targetName = is.readUTF();
                        Player other = game.findPlayer(targetName);

                        // request target card from client
                        writeString(os, "[Request for a card]");
                        int targetCard = Game.toCard(is.readUTF());

                        Platform.runLater(() -> ta.appendText(String.format("[Server] %s requests %d from %s\n",
                                thisTurn.getName(), targetCard, targetName)));

                        // get hands from both players
                        List<Integer> myHand = player.getHand();
                        List<Integer> otherHand = other.getHand();

                        // check if target player has target card
                        int recv;
                        if ((recv = other.take(targetCard)) > 0) { // target player has target card
                            // give this player those cards
                            player.give(targetCard, recv);

                            Platform.runLater(() -> ta.appendText(String.format("[Server] %s received %d %s's\n",
                                    thisTurn.getName(), recv, targetCard)));
                        } else { // target player does not have = target card
                            // this player go fish
                            player.goFish();

                            // move this player to the back of the queue
                            game.playerQueue().removeFirst();
                            game.playerQueue().addLast(player);

                            Platform.runLater(() -> ta.appendText(String.format("[Server] %s Go Fish!\n",
                                    thisTurn.getName())));
                        }

                        // tell client the number of target cards this player got
                        writeInt(os, recv);
                    }
                }
            } catch (SocketException | EOFException disconnect) {
                try {
                    game.playerQueue().remove(player);
                    remove(this);

                    is.close();
                    os.close();

                    Platform.runLater(() -> ta.appendText(String.format("[Server] %s disconnected\n",
                            player.getName())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String formatHand() {
            if (player.getHand().isEmpty())
                return "";

            StringBuilder hand = new StringBuilder();
            for (int card : player.getHand()) {
                hand.append(card).append(" ");
            }

            return hand.toString();
        }
    }
}