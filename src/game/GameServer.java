package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static game.Util.writeInt;
import static game.Util.writeString;

/**
 * The server for the Go Fish game.
 * <p>
 * Run this class directly to start a game server on the runner's
 * address and port. It includes a simple UI that tells the user the
 * IP and port of this server, and an area in which the interactions
 * between the server and the client are shown.
 */
public class GameServer extends Application {

    // the visual text area for server-client interactions
    private TextArea ta = new TextArea();

    // Go Fish game
    private static Game game;

    // this server's socket and its IP and port number
    private static ServerSocket serverSocket;
    private static String IP;
    private static int port;

    // an array of player handlers
    private static PlayerHandler[] players;
    private static int numPlayer;

    // the next free index in the handler array
    private static int nextFree;

    // thread for accepting new players
    private static Thread accept;

    // whether or not game has started
    private static int numReady;

    /**
     * Execute to start the server and run the UI.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Overrides the start method in Application class.
     *
     * @param primaryStage the main window
     */
    @Override
    public void start(Stage primaryStage) {
        // initialize class variables
        initialize();
        ta.appendText("[Server] Initialized\n");

        // create a stack pane to hold the text area
        StackPane main = new StackPane(ta);
        main.setPadding(new Insets(5, 5, 0, 5));

        // labels for IP and port number
        Label IPLabel = new Label("IP: " + IP);
        Label portLabel = new Label("Port: " + port);

        // HBox to hold the labels
        HBox infoHBox = new HBox(30);
        infoHBox.setPadding(new Insets(5));
        infoHBox.setAlignment(Pos.CENTER);
        infoHBox.getChildren().addAll(IPLabel, portLabel);

        // a text field for server admin to run commands
        TextField tfCommand = new TextField();
        tfCommand.setOnAction(event -> {
            String command = tfCommand.getText();
            if (command.equals("reset")) {
                // stop accepting player thread
                accept.interrupt();

                // close all i/o streams of current players
                for (PlayerHandler handler : players) {
                    try {
                        handler.is.close();
                        handler.os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // re-initialize
                initialize();

                // make sure server socket is closed
                while (serverSocket.isClosed()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // restart accepting player thread
                accept = new Thread(new AcceptPlayer());
                accept.start();
                Platform.runLater(() -> ta.appendText("[Server] has been reset"));
            } else if (command.equals("players")) {
                Platform.runLater(() -> ta.appendText("[Server] " + players()));
            }

            tfCommand.clear();
        });

        // stack pane to hold command text field
        StackPane commandPane = new StackPane(tfCommand);
        commandPane.setPadding(new Insets(0, 5, 5, 5));

        // base pane for organizing labels and text area
        BorderPane base = new BorderPane(main);
        base.setTop(infoHBox);
        base.setBottom(commandPane);

        // create a scene
        Scene scene = new Scene(base);

        // set stage info and show stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GoFish Server");
        primaryStage.setOnCloseRequest(event -> System.exit(1));
        primaryStage.show();

        // set size of text area
        ta.setPrefSize(500, 350);

        // start server thread
        accept = new Thread(new AcceptPlayer());
        accept.start();
    }

    /**
     * A Runnable class for accepting players with a thread.
     */
    private class AcceptPlayer implements Runnable {

        @Override
        public void run() {
            try {
//                System.out.printf("[Waiting for connection at port %d ...]\n", port);
                Platform.runLater(() ->
                        ta.appendText(String.format("[Server] Waiting for connection at port %d ...\n", port)));

                while (!game.isStarted()) {
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

                    // start a new player handler for the new player
                    new Thread(new PlayerHandler(newPlayer, is, os)).start();

                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize class variables.
     */
    private static void initialize() {
        game = new Game(); // create  a new game
        port = 8000; // set port number

        // create server socket and retrieve user IP
        try {
            if (serverSocket != null)
                serverSocket.close();

            serverSocket = new ServerSocket(port);
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create an array of size 4 because the maximum
        // number of players is 4
        players = new PlayerHandler[4];
        numPlayer = 0;

        // next free index is at 0
        nextFree = 0;

        // game has not started
        numReady = 0;
    }

    /**
     * @return the Go Fish game
     */
    public static Game getGame() {
        return game;
    }

    /**
     * @return this server's port number
     */
    public static int getPort() {
        return port;
    }

    /**
     * @return this server's IP address
     */
    public static String getIP() {
        return IP;
    }

    /**
     * Get a list of current player names in the game.
     *
     * @return an ArrayList of names in string
     */
    public static List<String> players() {
        List<String> names = new ArrayList<>();
        for (PlayerHandler handler : players) {
            if (handler != null) {
                names.add(handler.player.getName());
            }
        }

        return names;
    }

    /**
     * Remove a player handler from the handler array.
     *
     * @param player the player handler
     * @return the next free index
     * @throws IllegalArgumentException if the given player handler
     *                                  does not exist in the array
     */
    private static int removeHandler(PlayerHandler player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                players[i] = null;
                return i;
            }
        }

        throw new IllegalArgumentException("Player Not Found: " + player);
    }

    /**
     * The handler class for each player in the game. This class
     * implements the java.lang.Runnable interface to be easily
     * passed into a thread.
     */
    private class PlayerHandler implements Runnable {

        // the player for this handler
        Player player;

        // whether or not this player is ready to start
        boolean ready;

        // i/o streams
        DataInputStream is;
        DataOutputStream os;

        /**
         * Create a new handler with the given player and streams.
         *
         * @param player the player for this handler
         * @param is     the input stream for the player
         * @param os     the output stream for the player
         */
        PlayerHandler(Player player, DataInputStream is, DataOutputStream os) throws InterruptedException {
            this.player = player;
            this.is = is;
            this.os = os;

            // add this handler to the array
            players[nextFree] = this;
            numPlayer++;

            // send list of names to client
            for (PlayerHandler handler : players) {
                if (handler != null) {
                    StringBuilder names = new StringBuilder();
                    for (String name : players()) {
                        if (!name.equals(handler.player.getName())) {
                            names.append(name).append(" ");
                        }
                    }
                    writeString(handler.os, names.toString());
                    Thread.sleep(100);
                }
            }

            // update nextFree
            int i = 0;
            while (i < 4 && players[i] != null) {
                i++;
                nextFree = i;
            }
        }

        /**
         * Override the abstract run() method in Runnable.
         */
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    if (!game.isStarted() && !ready) {
                        System.out.printf("[%s] Game has not started: %d/%d\n", player, numReady, numPlayer);
                        is.readInt();
                        ready = true;
                        numReady++;
//                        System.out.printf("[%s] is ready: %d/%d\n", player, numReady, numPlayer);
                        Platform.runLater(() -> ta.appendText(String.format("[Server] %s is ready: %d/%d\n",
                                player, numReady, numPlayer)));

                        if (numReady == numPlayer) {
                            writeToAll("start");
                            game.start();
                            Thread.sleep(100);
                            System.out.printf("[%s] Wrote start to all players\n", player);
                        }
                    } else if (game.isStarted()) {
//                        System.out.printf("[%s] Game has started\n", player);
                        Platform.runLater(() -> ta.appendText("[Server] Game has started\n"));

                        // send this player's hand
                        writeString(os, formatHand());
                        Thread.sleep(100);
                        System.out.printf("[%s] Hand sent\n", player);

                        // get and send the name of the player of this turn
                        Player thisTurn = game.playerQueue().peekFirst();
                        String thisTurnName = thisTurn.getName();
                        writeString(os, thisTurn.getName());
                        Thread.sleep(100);
                        System.out.printf("[%s] Player of this turn sent\n", player);
                        Platform.runLater(() -> ta.appendText(String.format("[Server] %s's turn\n",
                                thisTurnName)));

                        // if that player is this player
                        if (thisTurn == player) {
                            System.out.printf("[%s] My turn\n", player);

                            // read target player and card from client
                            String[] target = is.readUTF().split(" ");
                            String targetName = target[0];
                            int targetCard = Game.toCard(target[1]);
                            System.out.printf("[%s] Received target\n", player);

                            // request target player from client
                            Player other = game.findPlayer(targetName);
                            Platform.runLater(() -> ta.appendText(String.format("[Server] %s requests %d from %s\n",
                                    thisTurnName, targetCard, targetName)));

                            System.out.printf("[%s] my hand: %s\n", player, player.getHand());
                            System.out.printf("[%s] target hand: %s\n", player, other.getHand());

                            // check if target player has target card
                            int recv;
                            if ((recv = other.take(targetCard)) > 0) { // target player has target card
                                // give this player those cards
                                player.give(targetCard, recv);
                                System.out.printf("[%s] Got %d %s's!\n", player, recv, targetCard);

                                Platform.runLater(() -> ta.appendText(String.format("[Server] %s received %d %s's\n",
                                        thisTurnName, recv, targetCard)));
                            } else { // target player does not have = target card
                                // this player go fish
                                player.goFish();
                                System.out.printf("[%s] Go Fish!\n", player);

                                // move this player to the back of the queue
                                game.playerQueue().removeFirst();
                                game.playerQueue().addLast(player);

                                Platform.runLater(() -> ta.appendText(String.format("[Server] %s Go Fish!\n",
                                        thisTurnName)));
                            }

                            // tell client the number of target cards this player got
                            writeInt(os, recv);
                            Thread.sleep(100);
                            System.out.printf("[%s] Recv sent: %d\n", player, recv);

                            // send hand after go fish
                            writeString(os, formatHand());
                            Thread.sleep(100);
                            System.out.printf("[%s] Hand sent after Go Fish\n", player);
                        } else {
                            while (thisTurn != player) {
                                thisTurn = game.playerQueue().peekFirst();
                                Thread.sleep(500);
                            }

                            writeInt(os, 1);
                            Thread.sleep(100);
                        }
                    } else {
                        Thread.sleep(500);
                    }
                }
            } catch (SocketException | EOFException disconnect) { // when client is disconnected
                try {
                    numPlayer--;
                    if (ready)
                        numReady--;

                    // remove this player from the queue
                    game.playerQueue().remove(player);

                    // remove this handler from the array
                    nextFree = removeHandler(this);

                    // close the i/o streams
                    is.close();
                    os.close();

                    Platform.runLater(() -> ta.appendText(String.format("[Server] %s disconnected\n",
                            player.getName())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Format the hand of a player to a string of cards to ease
         * the reading for the client.
         *
         * @return a string of cards separated by spaces
         */
        private String formatHand() {
            // if this player's hand is empty
            if (player.getHand().isEmpty())
                return "";

            // build the string
            StringBuilder hand = new StringBuilder();
            for (int card : player.getHand()) {
                hand.append(card).append(" ");
            }

            return hand.toString();
        }

        /**
         * Write a message to all PlayerHandlers in this current server.
         *
         * @param msg the msg
         */
        private void writeToAll(String msg) {
            for (PlayerHandler handler : players) {
                if (handler != null) {
                    writeString(handler.os, msg);
                }
            }
        }

        @Override
        public String toString() {
            return player.toString();
        }
    }
}