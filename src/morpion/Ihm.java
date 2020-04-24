package morpion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;//
import javafx.scene.layout.GridPane;//

//obligé de mettre en public pour lier le doc FXML
public class Ihm {
	private Controller ctrl;
	
//	les controllers des sous-vues
	@FXML private Grille panelGrilleController;
	@FXML private TourJeu panelTourJeuController;
	@FXML private Menu panelMenuController;
	
//	panels
//	@FXML private GridPane panelGrille;
//	@FXML private GridPane panelTourJeu;
//	@FXML private AnchorPane panelMenu;
	
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
		private List<Label> cases;
		
//		cases du morpion
		@FXML private Label lblCase0; @FXML private Label lblCase1; @FXML private Label lblCase2;
		@FXML private Label lblCase3; @FXML private Label lblCase4; @FXML private Label lblCase5;
		@FXML private Label lblCase6; @FXML private Label lblCase7; @FXML private Label lblCase8;
		
		@FXML private void initialize() {
			this.cases = new ArrayList<>(Arrays.asList(
					lblCase0, lblCase1, lblCase2, 
					lblCase3, lblCase4, lblCase5, 
					lblCase6, lblCase7, lblCase8
			));
			for(Label c : this.cases)
			{
				c.setOnMouseClicked(this::handleMouseEventOnCase);
				c.setOnMouseEntered(this::handleMouseEventOnCase);
				c.setOnMouseExited(this::handleMouseEventOnCase);
			}
				
		}
		
		public void injectMainController(Ihm ihm)
		{
			this.ihm = ihm;
		}
		
		public void writeCase(int id, Case c)
		{
			this.cases.get(id).setText(c.toString());
		}
		
		private void handleMouseEventOnCase(MouseEvent event) {
			Controller ctrl = Grille.this.ihm.getCtrl();
			Label lbl = (Label)event.getSource();
			int lblId = Integer.valueOf(lbl.getId().substring(lbl.getId().length() - 1));
			String eventType = event.getEventType().toString();
			
			if(eventType.equals("MOUSE_CLICKED")) {
				try {
					if(Grille.this.ihm.getCtrl().proposerCoup(lblId))
						lbl.setOpacity(1.0);
				} catch (IOException e) { e.printStackTrace(); }
			}
			else if(ctrl.caseVide(lblId)) {
				if(eventType.equals("MOUSE_ENTERED")) {
					Joueur j = ctrl.getJoueurCourant();
					lbl.setOpacity(0.3);
					lbl.setText(j.toString());
				}
				else if(eventType.equals("MOUSE_EXITED")) {
					lbl.setText(Case.VIDE.toString());
					lbl.setOpacity(1.0);
				}
			}
		}
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
				this.lblX.setStyle(CSS.STYLE_LABEL_TOUR_JEU_REMPLI);
				this.lblO.setStyle(CSS.STYLE_LABEL_TOUR_JEU_VIDE);
			}
			else if(j == Joueur.O)
			{
				this.lblX.setStyle(CSS.STYLE_LABEL_TOUR_JEU_VIDE);
				this.lblO.setStyle(CSS.STYLE_LABEL_TOUR_JEU_REMPLI);
			}
		}
	}
	
	public static class Menu {
		private Ihm ihm;
		private List<ToggleButton> btnsModeJeu;
		
		@FXML private Button btnEditConfig;
		@FXML private ToggleButton btnModeJeu0;
		@FXML private ToggleButton btnModeJeu1;
		@FXML private Slider slDiff;
		@FXML private Label lblArrowUp;
		@FXML private Label lblArrowDown;
		@FXML private Button btnRegles;
		
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
			
			this.slDiff.valueProperty().addListener((obs, oldVal, newVal) -> this.slDiff.setValue(Math.round(newVal.doubleValue())));
			this.slDiff.setOnMouseReleased(this.slDiffOnMouseReleased);
			
			this.lblArrowUp.setOnMouseClicked(this.lblArrowUpOnClick);
			this.lblArrowDown.setOnMouseClicked(this.lblArrowDownOnClick);
			
			this.btnRegles.setOnAction(this.btnReglesOnClick);
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
		private EventHandler<ActionEvent> btnReglesOnClick = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Menu.this.ihm.getCtrl().showDialogRegles();
			}
		};
		
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
		
		private EventHandler<? super MouseEvent> slDiffOnMouseReleased = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Difficulte diff = Difficulte.values()[(int)Menu.this.slDiff.getValue()];
				try {
					Menu.this.ihm.getCtrl().changerDiff(diff);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		private EventHandler<? super MouseEvent> lblArrowUpOnClick = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				int value = (int)Menu.this.slDiff.getValue() + 1;

				if(value < Difficulte.values().length) {
					Menu.this.slDiff.setValue(value);
					Menu.this.slDiffOnMouseReleased.handle(event);
				}
			}
		};
		
		private EventHandler<? super MouseEvent> lblArrowDownOnClick = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				int value = (int)Menu.this.slDiff.getValue() - 1;

				if(value >= 0) {
					Menu.this.slDiff.setValue(value);
					Menu.this.slDiffOnMouseReleased.handle(event);
				}
			}
		};
			
	}

}
