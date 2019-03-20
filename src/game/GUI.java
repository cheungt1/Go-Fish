package game;

import java.io.FileInputStream;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application
{	
	Font f16 = new Font("System", 16);
	Font f18 = new Font("System", 18);
	Font f20 = new Font("System", 20);

	String playerName = "Player 1";
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		startGameGUI();
		
		//Pane initialization
		StackPane overallPane = new StackPane();
		StackPane pInteraction = new StackPane();
		StackPane pVisual = new StackPane();
		StackPane pTextLog = new StackPane();
		
		//Background Image initialization
		ImageView background = new ImageView();
		
		//Label initialization
		Label lblPlayerSection = new Label("Available Players");
		Label lblCardSection =  new Label("Select a Card Value");
		Label lblPlayer2Name = new Label("Player 2");
		Label lblPlayer3Name = new Label("Player 3");
		Label lblPlayer4Name = new Label("Player 4");
		Label lblRecentAction = new Label("Test Text log llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll");
		
		//Button initialization
		Button btConfirmAction = new Button("Ask for that card");
		Button btQuit = new Button("Leave Game");
		
		//RadioButton initialization
		RadioButton rbPlayer2 = new RadioButton("Player 2");
		RadioButton rbPlayer3 = new RadioButton("Player 3");
		RadioButton rbPlayer4 = new RadioButton("Player 4");
		
		ToggleGroup rbPlayers = new ToggleGroup();
		
		rbPlayer2.setToggleGroup(rbPlayers);
		rbPlayer3.setToggleGroup(rbPlayers);
		rbPlayer4.setToggleGroup(rbPlayers);
		
		//ComboBox initialization
		ComboBox<String> cbCardValues = new ComboBox<String>();
		
		cbCardValues.getItems().addAll("Ace", "2", "3", "4", "5", "6", "7", "8", "9", "Jack", "Queen", "King");
		
		//The following two blocks of code are from: https://stackoverflow.com/questions/45144853/javafx-combobox-displayed-item-font-size?rq=1
		cbCardValues.setCellFactory(l -> new ListCell<String>() {

	        @Override
	        protected void updateItem(String item, boolean empty) {
	        	super.updateItem(item, empty); 
	            if(empty || item==null){
	                setStyle("-fx-font-size:16");
	            } else {
	                setStyle("-fx-font-size:16");
	                setText(item.toString());
	            }
	        }

	    });
		
		cbCardValues.setButtonCell(new ListCell<String>(){

	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty); 
	            if(empty || item==null){
	                setStyle("-fx-font-size:16");
	            } else {
	                setStyle("-fx-font-size:16");
	                setText(item.toString());
	            }
	        }

	    });
		//End of code from outside help
		
		//Setting up btQuit functionality, ending game
		btQuit.setOnAction(e -> 
		{
			//Create temporary stage
			Stage confirmStage = new Stage();
			
			//Create a StackPane for the temporary stage
			StackPane pConfirm = new StackPane();
			
			//Create a buttons for user decision
			Button btYes = new Button("Yes, I want to leave");
			Button btNo = new Button("No, let me back in the game");
			
			//Creates a label to prompt a decision from user
			Label lblConfirm = new Label("Do you want to leave this game?");
			
			//Sets font size for all components
			lblConfirm.setFont(f18);
			btYes.setFont(f16);
			btNo.setFont(f16);
			
			//Creates actions for the buttons
			btYes.setOnAction(f ->
			{
				confirmStage.close();
				primaryStage.close();
			});
			
			btNo.setOnAction(f ->
			{
				confirmStage.close();
			});
			
			//Adds all components onto the pane, pConfirm
			pConfirm.getChildren().addAll(lblConfirm, btYes, btNo);
			
			//Translates the components
			pConfirm.setAlignment(Pos.CENTER);
			translate(-128, 0, btYes);
			translate(128, 0, btNo);
			translate(0, -64, lblConfirm);
			
			
			//Creates a scene for the stage, confirmStage, and show it
			Scene confirmScene = new Scene(pConfirm, 512, 256);
			confirmStage.setScene(confirmScene);
			confirmStage.setTitle("Are you sure you wanna quit?");
			confirmStage.show();
			
		});
		
		//Setting font sizes
		lblPlayerSection.setFont(f20);
		lblCardSection.setFont(f20);
		rbPlayer2.setFont(f18);
		rbPlayer3.setFont(f18);
		rbPlayer4.setFont(f18);
		btConfirmAction.setFont(f18);
		btQuit.setFont(f18);
		lblRecentAction.setFont(f16);
		lblPlayer2Name.setFont(f16);
		lblPlayer3Name.setFont(f16);
		lblPlayer4Name.setFont(f16);
		
		//Adding all components into panes
		pInteraction.getChildren().addAll(lblPlayerSection, lblCardSection, rbPlayer2, rbPlayer3, rbPlayer4, cbCardValues, btConfirmAction, btQuit);
		pVisual.getChildren().addAll(background, lblPlayer2Name, lblPlayer3Name, lblPlayer4Name);
		pTextLog.getChildren().addAll(lblRecentAction);
		
		overallPane.getChildren().addAll(pVisual, pTextLog, pInteraction);
		
		//pVisual.setBackground(new Background(new BackgroundFill(Color.rgb(125, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		
		//overallPane Alignment
		pInteraction.setAlignment(Pos.BASELINE_RIGHT);
		pVisual.setAlignment(Pos.CENTER);
		pTextLog.setAlignment(Pos.TOP_LEFT);
		
		//pInteraction Alignment
		translate(-29, 50, lblPlayerSection);
		translate(-54.5, 85, rbPlayer2);
		translate(-54.5, 115, rbPlayer3);
		translate(-54.5, 145, rbPlayer4);
		translate(-20, 175, lblCardSection);
		translate(-54.5, 210, cbCardValues);
		translate(-25, 350, btConfirmAction);
		translate(-37.5, 400, btQuit);
		
		//pVisual Alignment
		translate(-110, -190, lblPlayer2Name);
		lblPlayer3Name.setRotate(90);
		translate(237.5, 0, lblPlayer3Name);
		lblPlayer4Name.setRotate(270);
		translate(-465.5, 0, lblPlayer4Name);
		
		//pVisual background set-up
		background.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\Desktop\\tableTextureTest.jpg")));
		translate(-112.5, 15, background);
		
		//pVisual text background set-up
		lblPlayer2Name.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));
		lblPlayer3Name.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));
		lblPlayer4Name.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));
		
		//pTextLog does not need any alignment
		
		Scene scene = new Scene(overallPane, 1024, 512);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Go Fish!");
		primaryStage.show();
	}
	
	public void startGameGUI()
	{
		Stage startStage = new Stage();
		
		Scene startScene = new Scene();
	}
	
	public void translate(double x, double y, Node node)
	{
		node.setTranslateX(x);
		node.setTranslateY(y);
	}
}
