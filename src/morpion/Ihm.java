package morpion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Ihm {
	private Controller ctrl;
	private List<Button> btns;
	
//	panels
	@FXML private AnchorPane panelMenu;
	@FXML private VBox panelJeu;
	@FXML private GridPane panelGrille;
	@FXML private HBox panelTourJeu;
	
//	cases du morpion
	@FXML private Button btnCase0;
	@FXML private Button btnCase1;
	@FXML private Button btnCase2;
	@FXML private Button btnCase3;
	@FXML private Button btnCase4;
	@FXML private Button btnCase5;
	@FXML private Button btnCase6;
	@FXML private Button btnCase7;
	@FXML private Button btnCase8;
	
//	tour de jeu
	@FXML private Label lblX;
	@FXML private Label lblO;
	
	
	@FXML private void initialize() {
		this.ctrl = new Controller(this, new Ent());
		
		this.btns = new ArrayList<>(Arrays.asList(
			btnCase0, btnCase1, btnCase2, 
			btnCase3, btnCase4, btnCase5, 
			btnCase6, btnCase7, btnCase8
		));
		
		this.ctrl.entToIhm();
		
		for(Button b : this.btns)
			b.setOnAction(this.caseOnClick);
		
		

		
	}
	
	public void writeCase(int id, Case c)
	{
		this.btns.get(id).setText(c.toString());
	}
	
	public void setTourDeJeu(Joueur j)
	{
		if(j == Joueur.X)
		{
			this.lblX.setTextFill(Color.BLACK);
			this.lblO.setTextFill(Color.GREY);
		}
		else if(j == Joueur.O)
		{
			this.lblX.setTextFill(Color.GREY);
			this.lblO.setTextFill(Color.BLACK);
		}
	}

	
//	les events handlers
	private EventHandler<ActionEvent> caseOnClick = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			Button btn = (Button)event.getSource();
			int btnId = Integer.valueOf(btn.getId().substring(btn.getId().length() - 1));
			
			Ihm.this.ctrl.proposerCoup(btnId);
		}
	};
}
