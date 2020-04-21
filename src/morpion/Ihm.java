package morpion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;

//obligé de mettre en public pour lier le doc FXML
public class Ihm {
	private Controller ctrl;
	
//	les controllers des sous-vues
	@FXML private Grille panelGrilleController;
	@FXML private TourJeu panelTourJeuController;
	@FXML private Menu panelMenuController;
	
//	panels
	@FXML private GridPane panelGrille;
	@FXML private HBox panelTourJeu;
	@FXML private AnchorPane panelMenu;
	
	@FXML private void initialize() throws Exception {
		this.ctrl = new Controller(this, new Ent());
		this.panelGrilleController.injectMainController(this);
		this.panelMenuController.injectMainController(this);
		
		this.ctrl.entToIhm();
		this.ctrl.lancerApprentissage();
	}
	
	public Controller getCtrl() { return this.ctrl; }
	public Grille getGrille() { return this.panelGrilleController; }
	public TourJeu getTourJeu() { return this.panelTourJeuController; }
	public Menu getMenu() { return this.panelMenuController; }

	public static class Grille {
		private Ihm ihm;
		private List<Button> btns;
		
//		cases du morpion
		@FXML private Button btnCase0;
		@FXML private Button btnCase1;
		@FXML private Button btnCase2;
		@FXML private Button btnCase3;
		@FXML private Button btnCase4;
		@FXML private Button btnCase5;
		@FXML private Button btnCase6;
		@FXML private Button btnCase7;
		@FXML private Button btnCase8;
		
		@FXML private void initialize() {
			this.btns = new ArrayList<>(Arrays.asList(
			btnCase0, btnCase1, btnCase2, 
			btnCase3, btnCase4, btnCase5, 
			btnCase6, btnCase7, btnCase8
		));
			
			for(Button b : this.btns)
				b.setOnAction(this.caseOnClick);
		}
		
		public void injectMainController(Ihm ihm)
		{
			this.ihm = ihm;
		}
		
		public void writeCase(int id, Case c)
		{
			this.btns.get(id).setText(c.toString());
		}
		
		private EventHandler<ActionEvent> caseOnClick = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Button btn = (Button)event.getSource();
				int btnId = Integer.valueOf(btn.getId().substring(btn.getId().length() - 1));
				
				try {
					Grille.this.ihm.getCtrl().proposerCoup(btnId);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	public static class TourJeu {
		@FXML private Label lblX;
		@FXML private Label lblO;
		
		@FXML private void initialize() {
			this.setTourDeJeu(Joueur.X);
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
	}
	
	public static class Menu {
		private Ihm ihm;
		private List<ToggleButton> btnsModeJeu;
		
		@FXML private Button btnEditConfig;
		@FXML private ToggleButton btnModeJeu0;
		@FXML private ToggleButton btnModeJeu1;
		
		@FXML private void initialize() {
			this.btnsModeJeu = Arrays.asList(this.btnModeJeu0, this.btnModeJeu1);
			
			for(ToggleButton b : this.btnsModeJeu)
			{
				b.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseEvent) {
						if(b.isSelected())	mouseEvent.consume();
					}
				});
				
				b.setOnAction(this.btnModeJeuOnClick);
			}

			this.btnEditConfig.setOnAction(this.btnEditConfigOnClick);
		}
		
		public void injectMainController(Ihm ihm)
		{
			this.ihm = ihm;
		}
		
		public void setModeJeu(Mode mode)
		{
			List<ToggleButton> btns = this.btnsModeJeu;
			for(ToggleButton b : btns)
				b.setSelected(false);
			btns.get(mode.getValue()).setSelected(true);
		}
		
//		les events handlers
		private EventHandler<ActionEvent> btnEditConfigOnClick = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					Menu.this.ihm.getCtrl().editConfigFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		private EventHandler<ActionEvent> btnModeJeuOnClick = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ToggleButton btn = (ToggleButton)event.getSource();
				int idBtn = Integer.valueOf(btn.getId().substring(btn.getId().length() - 1));
				Mode mode = Mode.get(idBtn);

				Menu.this.ihm.getCtrl().changerModeJeu(mode);
			}
		};
	}

}
