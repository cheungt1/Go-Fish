package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static game.Util.writeString;

public class GameClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        startGameGUI();

//        primaryStage.show();
    }

    //////////////////////
    // Welcoming Window //
    //////////////////////

    Stage startStage = new Stage();
    TextField tfUserName = new TextField();

    //Creates Font objects to reference throughout formatting GUI components
    Font f16 = new Font("System", 16);
    Font f18 = new Font("System", 18);
    Font f20 = new Font("System", 20);

    //Creates the first box that a player would see
    public void startGameGUI() {
        //Creates a temporary StackPane
        StackPane startPane = new StackPane();

        //Creates components
        Label lblMessage1 = new Label("Welcome!");
        Label lblMessage2 = new Label("Please Enter Your Name!");

        Button btConfirm = new Button("Play the Game!");

        //Stage modifications
        startStage.initStyle(StageStyle.UNDECORATED);

        //Setting font sizes
        lblMessage1.setFont(f18);
        lblMessage2.setFont(f18);
        tfUserName.setFont(f16);
        btConfirm.setFont(f18);

        //Shrinking the text field's width
        tfUserName.setMaxWidth(192);

        //Setting up btConfirm functionality (Read from tfPlayerName and set the string playerName to that)
        btConfirm.setDefaultButton(true);

        //Adds all components into the stack pane
        startPane.getChildren().addAll(lblMessage1, lblMessage2, tfUserName, btConfirm);

        //Translating all components
        translate(0, -48, lblMessage1);
        translate(0, -24, lblMessage2);
        translate(0, 16, tfUserName);
        translate(0, 64, btConfirm);


        Scene startScene = new Scene(startPane, 384, 192);
        startStage.setScene(startScene);
        startStage.setTitle("Welcome Player!");
        startStage.show();

        new Thread(() -> {
            btConfirm.setOnAction(event -> {
                // if user enters a valid username
                if (!tfUserName.getText().equals("")) {
                    myName = tfUserName.getText();
                    writeString(os, myName);

                    // start game
                    start();

                    // close welcoming window
                    startStage.close();
                } else { // invalid username
                    // change label to warn user to re-enter a name
                    Platform.runLater(() -> {
                        lblMessage1.setVisible(false);
                        lblMessage2.setText("Invalid Username");
                        lblMessage2.setTextFill(Color.RED);
                    });
                }
            });
        }).start();
    }

    //Created to translate a GUI component in the x and y axis at the same time
    public void translate(double x, double y, Node node) {
        node.setTranslateX(x);
        node.setTranslateY(y);
    }





    ///// ORIGINAL CODE

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    private String myName;

    private Scanner input;

    public GameClient() throws IOException {
        input = new Scanner(System.in);
        System.out.print("Enter server IP: ");
        String ip = input.nextLine();
        System.out.print("Enter server port: ");
        int port = input.nextInt();
        if (input.hasNextLine()) input.nextLine();

        socket = new Socket(ip, port);
        os = new DataOutputStream(socket.getOutputStream());
        is = new DataInputStream(socket.getInputStream());
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
        os = new DataOutputStream(socket.getOutputStream());
        is = new DataInputStream(socket.getInputStream());

//        start();
    }

    private void start() {
        try {
//            Scanner input = new Scanner(System.in);

//            System.out.print(is.readUTF()); // welcoming message

            // send player name to server
//			writeString(os, GUI.getUserName());
//            String myName = input.nextLine();
//            myName = tfUserName.getText();
//            writeString(os, myName);

            // get signal from server to ensure that player has been created
            is.readInt();

            while (true) {
                // get hand from server
                String myHand = is.readUTF();
                System.out.println("Your hand: " + myHand);

                // get the name of the player of this turn
                String thisTurn = is.readUTF();
//                System.out.println(myName);

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