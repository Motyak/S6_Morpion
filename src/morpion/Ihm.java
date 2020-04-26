package morpion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Mk.Pair;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

//obligé de mettre en public pour lier le doc FXML
public class Ihm {
	private Controller ctrl;
	
//	les controllers des sous-vues
	@FXML private Grille panelGrilleController;
	@FXML private TourJeu panelTourJeuController;
	@FXML private Menu panelMenuController;
	
//	panels
	@FXML private GridPane panelGrille;
	@FXML private GridPane panelTourJeu;
	@FXML private GridPane panelMenu;
	
	private boolean panelMenuOpened = false;
	
	@FXML private void initialize() throws Exception {
		this.ctrl = new Controller(this, new Ent());
		this.panelGrilleController.injectMainController(this);
		this.panelMenuController.injectMainController(this);
		
		this.panelMenu.setOnMouseEntered(this::handleMouseHoverOnMenu);
		this.panelMenu.setOnMouseExited(this::handleMouseHoverOnMenu);
		
		this.ctrl.entToIhm();
		Controller.lancerConfigThread(this.ctrl.getAi());
		Controller.lancerApprentissage(this.ctrl.getAi());
	}
	
	public Controller getCtrl() { return this.ctrl; }
	public Grille getGrille() { return this.panelGrilleController; }
	public TourJeu getTourJeu() { return this.panelTourJeuController; }
	public Menu getMenu() { return this.panelMenuController; }

	private Timeline createMinWidthAnim(Region reg, double minWidth, int duration)
	{
		Timeline tl = new Timeline();
		tl.getKeyFrames().add(new KeyFrame(Duration.millis(duration), 
				new KeyValue(reg.minWidthProperty(), minWidth)));
		return tl;
	}
	
	private void openMenuAnim(int duration)
	{
		this.panelMenuOpened = true;
		TranslateTransition ttMenu = new TranslateTransition(new Duration(duration), this.panelMenu);
		TranslateTransition ttGrille = new TranslateTransition(new Duration(duration), this.panelGrille);
		TranslateTransition ttTourJeu = new TranslateTransition(new Duration(duration), this.panelTourJeu);
		ParallelTransition transition = new ParallelTransition(
				ttMenu, ttGrille, ttTourJeu, 
				this.createMinWidthAnim(this.panelGrille, 595.0, duration),
				this.createMinWidthAnim(this.panelTourJeuController.lblX, 300.0, duration),
				this.createMinWidthAnim(this.panelTourJeuController.lblO, 300.0, duration),
				this.createMinWidthAnim(this.panelTourJeu, 595.0, duration)
		);
		ttMenu.setToX(0.0);
		ttGrille.setToX(0.0);
		ttTourJeu.setToX(-1.0);
		this.panelMenuController.imgMenuArrow.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		
		transition.play();
	}
	
	private void closeMenuAnim(int duration)
	{
		this.panelMenuOpened = false;
		TranslateTransition ttMenu = new TranslateTransition(new Duration(duration), this.panelMenu);
		TranslateTransition ttGrille = new TranslateTransition(new Duration(duration), this.panelGrille);
		TranslateTransition ttTourJeu = new TranslateTransition(new Duration(duration), this.panelTourJeu);
		
		ParallelTransition transition = new ParallelTransition(
				ttMenu, ttGrille, ttTourJeu, 
				this.createMinWidthAnim(this.panelGrille, 846.0, duration),
				this.createMinWidthAnim(this.panelTourJeuController.lblX, 424.0, duration),
				this.createMinWidthAnim(this.panelTourJeuController.lblO, 424.0, duration),
				this.createMinWidthAnim(this.panelTourJeu, 848.0, duration)
		);
		ttMenu.setToX(-250.0);
		ttGrille.setToX(-250.0);
		ttTourJeu.setToX(-252.0);
		this.panelMenuController.imgMenuArrow.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		
		transition.play();
	}
	
	//events handlers
	private void handleMouseHoverOnMenu(MouseEvent event) {
		String evtType = event.getEventType().toString();
		if(evtType.equals("MOUSE_ENTERED") && !this.panelMenuOpened)
			this.openMenuAnim(200);
		else if(evtType.equals("MOUSE_EXITED") && this.panelMenuOpened)
			this.closeMenuAnim(200);
	}

	public static class Grille {
		private Ihm ihm;
		private List<Label> cases;
		
		private HashMap<Range, Label> mapRangeCaseDepart;
		
		@FXML private Canvas canvasGrille;
		@FXML private ImageView imgRenew;
		
//		cases du morpion
		@FXML private Label lblCase0; @FXML private Label lblCase1; @FXML private Label lblCase2;
		@FXML private Label lblCase3; @FXML private Label lblCase4; @FXML private Label lblCase5;
		@FXML private Label lblCase6; @FXML private Label lblCase7; @FXML private Label lblCase8;
		
		@FXML private void initialize() {
			this.mapRangeCaseDepart = new HashMap<>();
			this.mapRangeCaseDepart.put(Range.HORIZONTALE_1, Grille.this.lblCase0);
			this.mapRangeCaseDepart.put(Range.HORIZONTALE_2, Grille.this.lblCase3);
			this.mapRangeCaseDepart.put(Range.HORIZONTALE_3, Grille.this.lblCase6);
			this.mapRangeCaseDepart.put(Range.VERTICALE_1, Grille.this.lblCase0);
			this.mapRangeCaseDepart.put(Range.VERTICALE_2, Grille.this.lblCase1);
			this.mapRangeCaseDepart.put(Range.VERTICALE_3, Grille.this.lblCase2);
			this.mapRangeCaseDepart.put(Range.DIAGONALE_1, Grille.this.lblCase0);
			this.mapRangeCaseDepart.put(Range.DIAGONALE_2, Grille.this.lblCase2);
			
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
			this.imgRenew.setOnMouseClicked(this::handleMouseEventOnRenew);
			this.imgRenew.setOnMouseEntered(this::handleMouseEventOnRenew);
			this.imgRenew.setOnMouseExited(this::handleMouseEventOnRenew);
				
		}
		
		public void injectMainController(Ihm ihm)
		{
			this.ihm = ihm;
		}
		
		public void writeCase(int id, Case c)
		{
			this.cases.get(id).setText(c.toString());
		}
		
		public Animation animLigneGagnante(Range ligne, int duration)
		{
			Label caseDepart = this.mapRangeCaseDepart.get(ligne);
			int range = ligne.getValue();

			double xDepart = caseDepart.getBoundsInParent().getMinX();
			double yDepart = caseDepart.getBoundsInParent().getMinY();
			double xArrivee = xDepart;
			double yArrivee = yDepart;
			
//			ligne horizontale
			if(range >= 10 && range <= 12) {
				xArrivee += 580.0;
				yDepart += 97.0;
				yArrivee = yDepart;
			}
//			ligne verticale
			else if(range >= 20 && range <= 22) {
				xDepart += 97.0;
				xArrivee = xDepart;
				yArrivee += 580.0;
			}
			else if(ligne == Range.DIAGONALE_1) {
				xArrivee += 580.0;
				yArrivee += 580.0;
			}
			else if(ligne == Range.DIAGONALE_2) {
				xDepart = caseDepart.getBoundsInParent().getMaxX();
				xArrivee = xDepart - 580.0;
				yArrivee += 580.0;
			}
			
			GraphicsContext gc = this.canvasGrille.getGraphicsContext2D();
			
			Path path = new Path();
			path.setStroke(Color.RED);
			path.setStrokeWidth(10.0);
			path.getElements().addAll(new MoveTo(xDepart, yDepart), new LineTo(xArrivee, yArrivee));
			
			Circle pen = new Circle(0, 0, 10);
			PathTransition pt = new PathTransition(new Duration(duration), path, pen);
			pt.currentTimeProperty().addListener(new ChangeListener<Duration>() {
				Pair<Double,Double> oldLocation = null;
				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
	                if( oldValue == Duration.ZERO)
	                    return;
	                double x = pen.getTranslateX();
	                double y = pen.getTranslateY();
	                if( oldLocation == null) {
	                    oldLocation = new Pair<>(0.0, 0.0);
	                    oldLocation.first = x;
	                    oldLocation.second = y;
	                    return;
	                }
	                gc.setStroke(Color.RED);
	                gc.setLineWidth(8);
	                gc.strokeLine(oldLocation.first, oldLocation.second, x, y);
	                oldLocation.first = x;
	                oldLocation.second = y;
				}
			});
			return pt;
		}
		
		public void clearCanvas()
		{
			this.canvasGrille.getGraphicsContext2D().clearRect(0, 0, 
					this.canvasGrille.getWidth(), this.canvasGrille.getHeight());
		}
		
//		events handlers
		private void handleMouseEventOnCase(MouseEvent event) {
			Controller ctrl = Grille.this.ihm.getCtrl();
			Label lbl = (Label)event.getSource();
			int lblId = Integer.parseInt(lbl.getId().substring(lbl.getId().length() - 1));
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
		
		private void handleMouseEventOnRenew(MouseEvent event) {
			String eventType = event.getEventType().toString();
			RotateTransition rt = new RotateTransition(new Duration(100), this.imgRenew);
			
			if(eventType.equals("MOUSE_CLICKED")) {
				rt.setByAngle(180.0);
				this.ihm.getCtrl().renewGame();
			}
			else if(eventType.equals("MOUSE_ENTERED" )) {
				rt.setByAngle(45.0);
			}
			else if(eventType.equals("MOUSE_EXITED")) {
				rt.setFromAngle(225.0);
				rt.setByAngle(-45.0);
			}
			
			rt.play();
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
				this.lblX.getStyleClass().clear(); this.lblX.getStyleClass().add("label-tour-rempli");
				this.lblO.getStyleClass().clear(); this.lblO.getStyleClass().add("label-tour-vide");
			}
			else if(j == Joueur.O)
			{
				this.lblX.getStyleClass().clear(); this.lblX.getStyleClass().add("label-tour-vide");
				this.lblO.getStyleClass().clear(); this.lblO.getStyleClass().add("label-tour-rempli");
			}
		}
	}
	
	public static class Menu {
		private Ihm ihm;
		private List<ImageView> imgsModeJeu;
		private List<ImageView> imgsBoutons;
		
		@FXML private ImageView imgModeJeu0;
		@FXML private ImageView imgModeJeu1;
		@FXML private Slider slDiff;
		@FXML private ImageView imgArrowUp;
		@FXML private ImageView imgArrowDown;
		@FXML private ImageView imgRegles;
		@FXML private ImageView imgEditConfig;
		@FXML private ImageView imgMenuArrow;
		@FXML private HBox subpanelDiff;
		
		@FXML private void initialize() {
			this.imgsModeJeu = Arrays.asList(this.imgModeJeu0, this.imgModeJeu1);
			this.imgsBoutons = Arrays.asList(this.imgRegles, this.imgEditConfig);
			
			for(ImageView iv : this.imgsModeJeu) {
				iv.setOnMouseClicked(this::handleMouseEventOnMode);
				iv.setOnMouseEntered(this::handleMouseEventOnMode);
				iv.setOnMouseExited(this::handleMouseEventOnMode);
			}
			for(ImageView iv : this.imgsBoutons) {
				iv.setOnMouseEntered(this::handleMouseHoverOnImgs);
				iv.setOnMouseExited(this::handleMouseUnhoverOnImgs);
			}
			
			this.slDiff.valueProperty().addListener((obs, oldVal, newVal) -> this.slDiff.setValue(Math.round(newVal.doubleValue())));
			this.slDiff.setOnMouseReleased(this::handleMouseEventOnSlider);
			this.imgArrowUp.setOnMouseClicked(this::handleMouseEventOnUpArrow);
			this.imgArrowDown.setOnMouseClicked(this::handleMouseEventOnDownArrow);
			
			this.imgRegles.setOnMouseClicked(event -> Menu.this.ihm.getCtrl().showDialogRegles());
			this.imgEditConfig.setOnMouseClicked(event -> Menu.this.ihm.getCtrl().editConfigFile());
		}
		
		public void injectMainController(Ihm ihm)
		{
			this.ihm = ihm;
		}
		
		public void setModeJeu(Mode mode)
		{
			if(mode == Mode.P_VS_AI) {
				this.imgModeJeu0.setImage(new Image(new File(RES.P_VS_AI_PRESSED).toURI().toString()));
				this.imgModeJeu1.setImage(new Image(new File(RES.P_VS_P_UNPRESSED).toURI().toString()));
			}
			else if(mode == Mode.P_VS_P) {
				this.imgModeJeu0.setImage(new Image(new File(RES.P_VS_AI_UNPRESSED).toURI().toString()));
				this.imgModeJeu1.setImage(new Image(new File(RES.P_VS_P_PRESSED).toURI().toString()));
			}
		}
		
		public void lockDiff(boolean lock)
		{
			if(lock)
				this.subpanelDiff.setDisable(true);
			else
				this.subpanelDiff.setDisable(false);
		}
		
//		les events handlers
		private void handleMouseEventOnMode(MouseEvent event) {
			String evtType = event.getEventType().toString();
			ImageView iv = (ImageView)event.getSource();
			int idMode = Integer.valueOf(iv.getId().substring(iv.getId().length() - 1));
			Mode mode = Mode.get(idMode);
			boolean modeActuel = mode == Menu.this.ihm.getCtrl().getModeJeu();

			if(evtType.equals("MOUSE_CLICKED"))
				Menu.this.ihm.getCtrl().changerModeJeu(mode);
			
			else if(evtType.equals("MOUSE_ENTERED") && !modeActuel)
				iv.setImage(new Image(new File(RES.getHover(mode)).toURI().toString()));
			
			else if(evtType.equals("MOUSE_EXITED") && !modeActuel)
				iv.setImage(new Image(new File(RES.getUnpressed(mode)).toURI().toString()));
		}
		
		private void handleMouseEventOnSlider(MouseEvent event) {
			Difficulte diff = Difficulte.values()[(int)Menu.this.slDiff.getValue()];
			try {
				Menu.this.ihm.getCtrl().changerDiff(diff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void handleMouseEventOnUpArrow(MouseEvent event) {
			int value = (int)Menu.this.slDiff.getValue() + 1;
			if(value < Difficulte.values().length) {
				Menu.this.slDiff.setValue(value);
				Menu.this.handleMouseEventOnSlider(event);
			}
		}
		
		private void handleMouseEventOnDownArrow(MouseEvent event) {
			int value = (int)Menu.this.slDiff.getValue() - 1;
			if(value >= 0) {
				Menu.this.slDiff.setValue(value);
				Menu.this.handleMouseEventOnSlider(event);
			}
		}
		
		private void handleMouseHoverOnImgs(MouseEvent event) {
			ImageView iv = (ImageView)event.getSource();
			iv.setEffect(new DropShadow(2.0, javafx.scene.paint.Color.BLACK));
		}
		
		private void handleMouseUnhoverOnImgs(MouseEvent event) {
			ImageView iv = (ImageView)event.getSource();
			iv.setEffect(null);
		}
	}

}
