import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUI extends Application
{
	String playerName = "Player 1";
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		//Pane initialization
		StackPane overallPane = new StackPane();
		Pane pInteraction = new Pane();
		Pane pVisual = new Pane();
		Pane pTextLog = new Pane();
		
		//Label initialization
		Label lblPlayerSection = new Label("Available Players");
		Label lblCardSection =  new Label("Select a Card Value");
		Label lblPlayer2Name = new Label("Player 2");
		Label lblPlayer3Name = new Label("Player 3");
		Label lblPlayer4Name = new Label("Player 4");
		Label lblRecentAction1 = new Label("");
		Label lblRecentAction2 = new Label("");
		Label lblRecentAction3 = new Label("");
		
		//Button initialization
		Button btConfirmAction = new Button("Ask for that card");
		
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
		
		//Adding all components into panes
		pInteraction.getChildren().addAll(lblPlayerSection, lblCardSection, rbPlayer2, rbPlayer3, rbPlayer4, cbCardValues, btConfirmAction);
		pVisual.getChildren().addAll(lblPlayer2Name, lblPlayer3Name, lblPlayer4Name);
		pTextLog.getChildren().addAll(lblRecentAction1, lblRecentAction2, lblRecentAction3);
		
		overallPane.getChildren().addAll(pInteraction, pVisual, pTextLog);
		
		//overallPane Alignment
		StackPane.setAlignment(pInteraction, Pos.BASELINE_RIGHT);
		StackPane.setAlignment(pVisual, Pos.CENTER);
		StackPane.setAlignment(pTextLog, Pos.TOP_CENTER);
		
		/*
		//pInteraction Alignment
		StackPane.setAlignment(lblPlayerSection, Pos.TOP_CENTER);
		StackPane.setAlignment(rbPlayer2, Pos.CENTER);
		StackPane.setAlignment(rbPlayer3, Pos.CENTER);
		StackPane.setAlignment(rbPlayer4, Pos.CENTER);
		StackPane.setAlignment(lblCardSection, Pos.BASELINE_CENTER);
		StackPane.setAlignment(cbCardValues, Pos.BASELINE_CENTER);
		StackPane.setAlignment(btConfirmAction, Pos.BOTTOM_CENTER);
		*/
		
		Scene scene = new Scene(overallPane, 1024, 512);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Go Fish!");
		primaryStage.show();
	}
}
