package morpion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Ihm {
	private Controller ctrl;
	
//	panels
	@FXML private AnchorPane panelMenu;
	@FXML private VBox panelJeu;
	@FXML private GridPane panelGrille;
	@FXML private HBox panelTourJeur;
	
//	cases du morpion
	@FXML private Button btnCase0_0;
	@FXML private Button btnCase1_0;
	@FXML private Button btnCase2_0;
	@FXML private Button btnCase0_1;
	@FXML private Button btnCase1_1;
	@FXML private Button btnCase2_1;
	@FXML private Button btnCase0_2;
	@FXML private Button btnCase1_2;
	@FXML private Button btnCase2_2;
	
//	tour de jeu
	@FXML private Label lblX;
	@FXML private Label lblO;
	
	
	@FXML private void initialize() {
//		initialisation du controller
		this.ctrl = new Controller(this, new Ent());
		
		this.btnCase0_0.setText("X");
	}
	
//	les events handlers
}
