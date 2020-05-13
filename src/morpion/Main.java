package morpion;
	
import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


/**
 * Represent the application launcher
 * @author Tommy 'Motyak'
 *
 */
public class Main extends Application {
	
	public static Thread learningThread;
	public static Thread configThread;
	public static Stage rulesDialog;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("View.fxml"));
			Scene scene = new Scene(root,890,890);	//900x900 real size
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("XO");
			primaryStage.getIcons().add(new Image(new File(RES.APP_ICON).toURI().toString()));
			primaryStage.show();
			
			this.createRulesDialog(primaryStage);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		if(Main.configThread != null)
			Main.configThread.interrupt();
	}
	
	/**
	 * Create the rules dialog
	 * @param parent the parent stage
	 */
	private void createRulesDialog(Stage parent)
	{
		Main.rulesDialog = new Stage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("RulesDialog.fxml"));
			Scene scene = new Scene(root,810,810);
			Main.rulesDialog.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Main.rulesDialog.initOwner(parent);
		Main.rulesDialog.initModality(Modality.APPLICATION_MODAL);
		Main.rulesDialog.initStyle(StageStyle.UTILITY);
	}
}
