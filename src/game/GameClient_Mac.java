package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import static game.Util.writeInt;
import static game.Util.writeString;

public class GameClient_Mac extends Application {
    Socket socket;
    DataOutputStream os;
    DataInputStream is;

    String playerList;

    // Creates global stages to allow only one stage to be active at once
    Stage playingStage = new Stage();
    Stage startStage = new Stage();

    // Pane initialization, global for method access
    BorderPane overallPane = new BorderPane();
    StackPane pInteraction = new StackPane();
    StackPane pVisual = new StackPane();
//    StackPane pTextLog = new StackPane();

    Label lblPlayer2Name = new Label("Player 2");
    Label lblPlayer3Name = new Label("Player 3");
    Label lblPlayer4Name = new Label("Player 4");

    Label lblLastAction = new Label();
    Label lblAction = new Label();

    VBox actionsBox = new VBox(5);

    // Public GameClient Components to send to server/client
    TextField tfUserName = new TextField();

    RadioButton rbPlayer2 = new RadioButton("Player 2");
    RadioButton rbPlayer3 = new RadioButton("Player 3");
    RadioButton rbPlayer4 = new RadioButton("Player 4");

    ToggleGroup rbPlayers = new ToggleGroup();

    ComboBox<String> cbCardValues = new ComboBox<>();

    // Creates Font objects to reference throughout formatting GameClient components
    Font f16 = new Font("System", 16);
    Font f18 = new Font("System", 18);
    Font f20 = new Font("System", 20);

    // Creates card images
    ImageView imgCardBack = new ImageView();

    // Global player name to be used throughout various methods
    String userName = "";
    Label lblUserName = new Label(userName); // Used to display the user's name

    Button btConfirmAction;
    Button btReady;

    IntegerProperty playerScore = new SimpleIntegerProperty(0);
    Label lblPlayerScore = new Label("Your Score: " + playerScore.get());

    public GameClient_Mac() {
        try {
            socket = new Socket("localhost", 8000);
            os = new DataOutputStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // server.start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Starts the game, allowing user to input a name
            startGameGUI();

            // Background Image initialization
            ImageView background = new ImageView();

            // Label initialization
            Label lblPlayerSection = new Label("Available Players");
            Label lblCardSection = new Label("Select a Card Value");

            // Button initialization
            btConfirmAction = new Button("Ask for that card");
            Button btQuit = new Button("Leave Game");
            btReady = new Button("Ready!");

            // Stage modifications
            playingStage.initStyle(StageStyle.TRANSPARENT);

            // Sets up a toggle group so only one option can be true out of the three
            rbPlayer2.setToggleGroup(rbPlayers);
            rbPlayer3.setToggleGroup(rbPlayers);
            rbPlayer4.setToggleGroup(rbPlayers);

            rbPlayer2.setSelected(true);

            rbPlayer2.setDisable(true);
            rbPlayer3.setDisable(true);
            rbPlayer4.setDisable(true);

            // Sets up ComboBox's values

            // The following two blocks of code are from:
            // https://stackoverflow.com/questions/45144853/javafx-combobox-displayed-item-font-size?rq=1
            cbCardValues.setCellFactory(l -> new ListCell<>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("-fx-font-size:16");
                    } else {
                        setStyle("-fx-font-size:16");
                        setText(item);
                    }
                }

            });

            cbCardValues.setButtonCell(new ListCell<>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("-fx-font-size:16");
                    } else {
                        setStyle("-fx-font-size:16");
                        setText(item);
                    }
                }
            });

            // End of code from outside help

            lblPlayer2Name.setVisible(false);
            lblPlayer3Name.setVisible(false);
            lblPlayer4Name.setVisible(false);

            ImageView imgCard = new ImageView();
            ImageView imgCardBack1 = new ImageView(
                    new Image(new FileInputStream("card/b1fv.png")));
            ImageView imgCardBack2 = new ImageView(
                    new Image(new FileInputStream("card/b1fv.png")));
            ImageView imgCardBack3 = new ImageView(
                    new Image(new FileInputStream("card/b1fv.png")));

            cbCardValues.setOnAction(e -> {
                try {
                    switch (cbCardValues.getValue()) {
                        case "A":
                            imgCard.setImage(new Image(
                                    new FileInputStream("card/1.png")));
                        case "J":
                            imgCard.setImage(new Image(
                                    new FileInputStream("card/11.png")));
                            break;
                        case "Q":
                            imgCard.setImage(new Image(
                                    new FileInputStream("card/12.png")));
                            break;
                        case "K":
                            imgCard.setImage(new Image(
                                    new FileInputStream("card/13.png")));
                            break;
                        default:
                            imgCard.setImage(new Image(new FileInputStream("card/" + cbCardValues.getValue() + ".png")));
                            break;
                    }
                } catch (NullPointerException npe) {
                    // ignored
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Sets default image
            imgCard.setImage(new Image(new FileInputStream("card/1.png")));

            // Setting up btQuit functionality, ending game
            btQuit.setOnAction(e -> {
                // Create temporary stage
                Stage confirmStage = new Stage();

                // Create a StackPane for the temporary stage
                StackPane pConfirm = new StackPane();

                // Create a buttons for user decision
                Button btYes = new Button("Yes");
                Button btNo = new Button("No");

                // Creates a label to prompt a decision from user
                Label lblConfirm = new Label("Do you want to leave this game?");

                // Stage modifications
                confirmStage.initStyle(StageStyle.UNDECORATED);

                // Sets font size for all components
                lblConfirm.setFont(f18);
                btYes.setFont(f16);
                btNo.setFont(f16);

                btYes.setDefaultButton(true);

                // Creates actions for the buttons
                btYes.setOnAction(f -> {
                    /*
                     * Remove when multiple clients can connect if(server.getGame().isStarted()) { try
                     * { is.close(); os.close(); } catch(Exception ex) {
                     * System.out.println("Cannot close server correctly"); }
                     *
                     * confirmStage.close(); playingStage.close(); } else {
                     * lblConfirm.setText("You can't leave an active game. \n\t   No Rage-quitting"
                     * ); }
                     */

                    // Remove these two lines when above code can work
                    confirmStage.close();
                    playingStage.close();

                    System.exit(1);
                });

                btNo.setOnAction(f -> confirmStage.close());

                // Adds all components onto the pane, pConfirm
                pConfirm.getChildren().addAll(lblConfirm, btYes, btNo);

                // Translates the components
                pConfirm.setAlignment(Pos.CENTER);
                translate(-100, 32, btYes);
                translate(100, 32, btNo);
                translate(0, -32, lblConfirm);

                // Size modifications to buttons
                btYes.setPrefWidth(100);
                btNo.setPrefWidth(100);

                // Creates a scene for the stage, confirmStage, and show it
                Scene confirmScene = new Scene(pConfirm, 384, 192);
                confirmStage.setScene(confirmScene);
                confirmStage.setTitle("Are you sure you wanna quit?");
                confirmStage.show();

            });

            btConfirmAction.setOnAction(e -> {
                String targetPlayer = ((RadioButton) rbPlayers.getSelectedToggle()).getText();
                String targetCard = cbCardValues.getValue();
                writeString(os, String.format("%s %s", targetPlayer, targetCard));
            });

            // Setting font sizes
            lblPlayerSection.setFont(f20);
            lblCardSection.setFont(f20);
            lblPlayerScore.setFont(f18);
            rbPlayer2.setFont(f18);
            rbPlayer3.setFont(f18);
            rbPlayer4.setFont(f18);
            btConfirmAction.setFont(f18);
            btQuit.setFont(f18);
            btReady.setFont(f18);
            lblLastAction.setFont(f16);
            lblLastAction.setTextFill(Color.GRAY);
            lblAction.setFont(f16);
            lblUserName.setFont(f16);
            lblPlayer2Name.setFont(f16);
            lblPlayer3Name.setFont(f16);
            lblPlayer4Name.setFont(f16);

            actionsBox.getChildren().addAll(lblLastAction, lblAction);

            // Adding all components into panes
            pInteraction.getChildren().addAll(lblPlayerScore, lblPlayerSection, lblCardSection, imgCard, rbPlayer2,
                    rbPlayer3, rbPlayer4, cbCardValues, btConfirmAction, btQuit);
            pVisual.getChildren().addAll(background, imgCardBack1, imgCardBack2, imgCardBack3, btReady, lblUserName,
                    lblPlayer2Name, lblPlayer3Name, lblPlayer4Name);
//            pTextLog.getChildren().addAll(lblAction);

            overallPane.setTop(actionsBox);
            overallPane.setCenter(pVisual);
            overallPane.setRight(pInteraction);

            // overallPane background color change
            overallPane.setBackground(
                    new Background(new BackgroundFill(Color.rgb(243, 229, 192), CornerRadii.EMPTY, Insets.EMPTY)));

            // pInteraction Alignment
            translate(-20, -230, lblPlayerSection);
            translate(-20, -185, rbPlayer2);
            translate(-20, -155, rbPlayer3);
            translate(-20, -125, rbPlayer4);
            translate(-20, -75, lblCardSection);
            translate(-20, -40, cbCardValues);
            translate(-20, 35, imgCard);
            translate(-20, 110, btConfirmAction);
            translate(-20, 160, btQuit);
            translate(-20, 210, lblPlayerScore);

            // pVisual Alignment
            StackPane.setAlignment(lblUserName, Pos.BOTTOM_CENTER);
            translate(-30, -40, lblUserName);
            lblPlayer3Name.setRotate(180);
            translate(-30, -190, lblPlayer3Name);
            lblPlayer4Name.setRotate(270);
            translate(325, 0, lblPlayer4Name);
            lblPlayer2Name.setRotate(90);
            translate(-375.5, 0, lblPlayer2Name);

            translate(-27, 0, imgCardBack1);
            translate(-25, -2, imgCardBack2);
            translate(-23, -4, imgCardBack3);
            translate(-27, 0, btReady);

            // Label actions set-up
            // Click and hold on the label to re-orient the label to read the player's name
            lblPlayer3Name.setOnMousePressed(e -> lblPlayer3Name.setRotate(0));
            lblPlayer3Name.setOnMouseReleased(e -> lblPlayer3Name.setRotate(180));
            lblPlayer4Name.setOnMousePressed(e -> lblPlayer4Name.setRotate(0));
            lblPlayer4Name.setOnMouseReleased(e -> lblPlayer4Name.setRotate(270));
            lblPlayer2Name.setOnMousePressed(e -> lblPlayer2Name.setRotate(0));
            lblPlayer2Name.setOnMouseReleased(e -> lblPlayer2Name.setRotate(90));

            playerScore.addListener((observable, oldValue, newValue) ->
                    lblPlayerScore.setText("Your Score: " + newValue));

            btReady.setOnAction(event -> {
                writeInt(os, 1);

                btReady.setVisible(false);
                btReady.setDisable(true);

                updateAction("You're ready! Waiting for other players to get ready..");
            });

            // pVisual background set-up
            background.setImage(new Image(new FileInputStream("GUIGraphic/tableTexture.jpg")));
            translate(-20, 0, background);

            // pVisual text background set-up (Used to see text / testing)
            lblUserName.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            lblPlayer2Name
                    .setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            lblPlayer3Name
                    .setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            lblPlayer4Name
                    .setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

            // Create Scene and set-up stage
            Scene scene = new Scene(overallPane, 1024, 532);
            playingStage.setScene(scene);
            playingStage.setTitle("Go Fish!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Creates the first box that a player would see
    public void startGameGUI() {
        // Creates a temporary StackPane
        StackPane startPane = new StackPane();

        // Creates components
        Label lblMessage1 = new Label("Welcome");
        Label lblMessage2 = new Label("Please enter your name");

        Button btConfirm = new Button("Play the Game!");

        // Stage modifications
        startStage.initStyle(StageStyle.UNDECORATED);

        // Setting font sizes
        lblMessage1.setFont(f18);
        lblMessage2.setFont(f18);
        tfUserName.setFont(f16);
        btConfirm.setFont(f18);

        // Shrinking the text field's width
        tfUserName.setMaxWidth(192);

        // Setting up btConfirm functionality (Read from tfPlayerName and set the string
        // playerName to that)
        btConfirm.setDefaultButton(true);

        // Adds all components into the stack pane
        startPane.getChildren().addAll(lblMessage1, lblMessage2, tfUserName, btConfirm);

        // Translating all components
        translate(0, -48, lblMessage1);
        translate(0, -24, lblMessage2);
        translate(0, 16, tfUserName);
        translate(0, 64, btConfirm);

        Scene startScene = new Scene(startPane, 384, 192);
        startStage.setScene(startScene);
        startStage.setTitle("Welcome Player!");
        startStage.show();

        new Thread(() -> {
            try {
                btConfirm.setOnAction(e -> {
                    // Checks if the user entered a valid name or not
                    if (!tfUserName.getText().equals("")) {
                        // Sets the player name to what was entered
                        writeString(os, tfUserName.getText());

                        Platform.runLater(() -> {
                            updateUserName(tfUserName.getText());
                            updateAction("Welcome to Go Fish, " + userName + "! Click Ready to start.");
                        });

                        try {
                            is.readInt();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Closes this stage and shows the stage for the actual game
                        startStage.close();
                        playingStage.show();

                        updateInfo();
                    } else {
                        // Changes label to warn user of entering a name
                        Platform.runLater(() -> {
                            lblMessage1.setVisible(false);
                            lblMessage2.setText("You MUST enter a name!");
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    // Created to translate a GameClient component in the x and y axis at the same time
    public void translate(double x, double y, Node node) {
        node.setTranslateX(x);
        node.setTranslateY(y);
    }

    private boolean gameStarted = false;

    public void updateInfo() {
        new Thread(() -> {
            try {
                while (true) {
                    if (!gameStarted) {
                        String msg = is.readUTF();
                        System.out.println("msg = " + msg);
                        if (msg.equals("start")) {
                            gameStarted = true;

                            Platform.runLater(() -> {
                                updateAction("Game has started!");
                                updateOtherHands();
                            });

                            updateHand();
                        } else {
//                            System.out.println("game has not started");
                            playerList = msg;
                            System.out.println("players = " + playerList);
                            String[] othersName = playerList.split(" ");

                            if (playerList.equals(""))
                                btReady.setDisable(true);
                            else {
                                btReady.setDisable(false);
                                Platform.runLater(() -> {
                                    updateAction("A new player has joined!");
                                    updateGameName(othersName);
                                });
                            }

                            Thread.sleep(200);
                        }
                    } else {
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateHand() {
        new Thread(() -> {
            try {
                while (gameStarted) {
//                    System.out.println("new turn");
                    Platform.runLater(this::updateHand_GUI);
                    Thread.sleep(200);

                    String thisTurn = is.readUTF();
                    System.out.println("This turn = " + thisTurn);
                    System.out.println("my name = " + userName);

                    if (thisTurn.equals(userName)) {
                        Platform.runLater(() -> {
                            updateAction("It's your turn!");
                            btConfirmAction.setDisable(false);
                        });

                        String[] result = is.readUTF().split(" ");
                        System.out.println("result = " + Arrays.toString(result));
                        int numRecv = Integer.parseInt(result[0]);
                        String recv = result[1];
                        int matched = Integer.parseInt(result[2]);

                        if (numRecv == 0) {
//                            System.out.println("go fish: " + result[1]);
                            Platform.runLater(() -> updateAction("Go Fish! You got a " + result[1] + "."));
                        } else {
//                            System.out.println("received: " + recv + " " + recvRank);
                            Platform.runLater(() -> updateAction(String.format("You got %s %s's from %s!",
                                    numRecv, recv, ((RadioButton) rbPlayers.getSelectedToggle()).getText())));
                        }

                        if (matched == 1) {
                            Platform.runLater(() -> {
                                playerScore.set(playerScore.add(1).get());
                                updateAction(lblAction.getText() + " That made a match!");
                            });
                        }
                    } else {
                        Platform.runLater(() -> {
                            updateAction("Waiting for your turn...");
                            btConfirmAction.setDisable(true);
                        });
                        System.out.println("int = " + is.readInt()); // wait for signal from server
                    }

                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Updates the String, userName, and the label, lblUserName

    private ArrayList<ImageView> cardImages = new ArrayList<>();

    // Updates the default values of the cards with the user's true hand

    public void updateHand_GUI() {
        System.out.println("update my hand");

        String hand = "";

        try {
            hand = is.readUTF(); // read hand from server
//            writeInt(os, 1); // signal the server
            System.out.println("my hand = " + hand);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] handArr = hand.split(" ");

        pVisual.getChildren().removeAll(cardImages);
        cardImages.clear();

        // pVisual Card setup
        for (int i = 0; i < handArr.length; i++) {
            ImageView userCard = new ImageView();

            try {
                userCard.setImage(new Image(new FileInputStream("card/" + handArr[i] + ".png")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            cardImages.add(userCard);
            translate(13 * i - 55, 125, userCard);
        }

        pVisual.getChildren().addAll(cardImages);

        // convert cards in hand to their actual string representation
        TreeSet<String> cards = new TreeSet<>();
        for (String card : handArr) {
            cards.add(Game.toRank(Integer.parseInt(card)));
        }

        cbCardValues.getSelectionModel().clearSelection();
        cbCardValues.getItems().setAll(cards);
        cbCardValues.getSelectionModel().selectFirst();
    }

    public void updateOtherHands() {
        System.out.println("update other hands");

        // pVisual Opponent Card setup
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 6; j++) {
                ImageView userCard = new ImageView();

                if (i == 1) {
                    try {
                        userCard.setImage(new Image(new FileInputStream("card/b1fh.png")));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    pVisual.getChildren().add(userCard);

                    translate(-300, 13 * j - 50, userCard);
                } else if (i == 2) {
                    try {
                        userCard.setImage(new Image(new FileInputStream("card/b1fv.png")));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    pVisual.getChildren().add(userCard);

                    translate(13 * j - 55, -125, userCard);
                } else {
                    try {
                        userCard.setImage(new Image(new FileInputStream("card/b1fh.png")));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    pVisual.getChildren().add(userCard);

                    translate(250, 13 * j - 50, userCard);
                }
            }
        }
    }

    public void updateUserName(String newName) {
        userName = newName;
        lblUserName.setText(newName);
    }

    public void updateGameName(String[] playerNames) {
        rbPlayer2.setDisable(true);
        rbPlayer3.setDisable(true);
        rbPlayer4.setDisable(true);

        if (playerNames.length >= 1) {
            lblPlayer2Name.setText(playerNames[0]);
            rbPlayer2.setText(playerNames[0]);
            rbPlayer2.setDisable(false);
        }

        if (playerNames.length >= 2) {
            lblPlayer3Name.setText(playerNames[1]);
            rbPlayer3.setText(playerNames[1]);
            rbPlayer3.setDisable(false);
        }

        if (playerNames.length >= 3) {
            lblPlayer4Name.setText(playerNames[2]);
            rbPlayer4.setText(playerNames[2]);
            rbPlayer4.setDisable(false);
        }
    }

    public void updateAction(String msg) {
        if (!msg.equals(lblAction.getText())) {
            lblLastAction.setText(lblAction.getText());
            lblAction.setText(msg);
        }
    }

}