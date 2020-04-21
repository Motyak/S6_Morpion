package morpion;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	public static Thread learningThread;
	public static Stage dialogRegles;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("View.fxml"));
			Scene scene = new Scene(root,630,450);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			
			this.createDialogRegles(primaryStage);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		if(Main.learningThread != null)
			Main.learningThread.interrupt();
	}
	
	private void createDialogRegles(Stage parent)
	{
		Main.dialogRegles = new Stage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("DialogRegles.fxml"));
			Scene scene = new Scene(root,400,400);
			Main.dialogRegles.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Main.dialogRegles.initOwner(parent);
		Main.dialogRegles.initModality(Modality.APPLICATION_MODAL);
		Main.dialogRegles.initStyle(StageStyle.UTILITY);	//mettre UNDECORATED quand on aura fini le fxml
	}
}
