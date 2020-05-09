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
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
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
import javafx.util.Duration;

public class View {
	private Controller ctrl;
	
//	sub-views controllers
	@FXML private Grid paneGridController;
	@FXML private Turn paneTurnController;
	@FXML private Menu paneMenuController;
	
//	panes
	@FXML private GridPane paneGrid;
	@FXML private GridPane paneTurn;
	@FXML private GridPane paneMenu;
	
	private boolean menuOpened = false;
	private boolean winningRowAnimOccuring = false;
	
	@FXML private void initialize() throws Exception {
		this.ctrl = new Controller(this, new Ent());
		this.paneGridController.injectMainController(this);
		this.paneMenuController.injectMainController(this);
		
		this.paneMenu.setOnMouseEntered(this::handleMouseHoverOnMenu);
		this.paneMenu.setOnMouseExited(this::handleMouseHoverOnMenu);
		
		this.ctrl.entToIhm();
		Controller.launchConfiguring(this.ctrl.getAi());
		Controller.launchLearning(this.ctrl.getAi());
	}
	
	public Controller getCtrl() { return this.ctrl; }
	public Grid getGrid() { return this.paneGridController; }
	public Turn getTurn() { return this.paneTurnController; }
	public Menu getMenu() { return this.paneMenuController; }
	public boolean getWinningRowAnimOccuring() { return this.winningRowAnimOccuring; }
	public void setWinningRowAnimOccuring(boolean occuring) { this.winningRowAnimOccuring = occuring; }

	private Timeline createMinWidthAnim(Region reg, double minWidth, int duration)
	{
		Timeline tl = new Timeline();
		tl.getKeyFrames().add(new KeyFrame(Duration.millis(duration), 
				new KeyValue(reg.minWidthProperty(), minWidth)));
		return tl;
	}
	
	private void playOpeningMenuAnim(int duration)
	{
		this.menuOpened = true;
		TranslateTransition ttMenu = new TranslateTransition(new Duration(duration), this.paneMenu);
		TranslateTransition ttGrid = new TranslateTransition(new Duration(duration), this.paneGrid);
		TranslateTransition ttTurn = new TranslateTransition(new Duration(duration), this.paneTurn);
		TranslateTransition ttImgMenuIcon = new TranslateTransition(new Duration(duration), this.paneMenuController.imgMenuArrowIcon);
		RotateTransition rtImgMenuArrow = new RotateTransition(new Duration(duration), this.paneMenuController.imgMenuArrow);
		ParallelTransition transition = new ParallelTransition(
				ttMenu, ttGrid, ttTurn, ttImgMenuIcon, rtImgMenuArrow,
				this.createMinWidthAnim(this.paneGrid, 595.0, duration),
				this.createMinWidthAnim(this.paneTurnController.lblX, 300.0, duration),
				this.createMinWidthAnim(this.paneTurnController.lblO, 300.0, duration),
				this.createMinWidthAnim(this.paneTurn, 595.0, duration)
		);
		ttMenu.setToX(0.0);
		ttGrid.setToX(0.0);
		ttTurn.setToX(-1.0);
		ttImgMenuIcon.setToX(-23.0);
		rtImgMenuArrow.setToAngle(180.0);
		this.paneMenuController.imgMenuArrowIcon.setImage(new Image(new File(RES.GAMEPAD_ICON).toURI().toString()));
		
		transition.play();
	}
	
	private void playClosingMenuAnim(int duration)
	{
		this.menuOpened = false;
		TranslateTransition ttMenu = new TranslateTransition(new Duration(duration), this.paneMenu);
		TranslateTransition ttGrid = new TranslateTransition(new Duration(duration), this.paneGrid);
		TranslateTransition ttTurn = new TranslateTransition(new Duration(duration), this.paneTurn);
		TranslateTransition ttImgMenuIcon = new TranslateTransition(new Duration(duration), this.paneMenuController.imgMenuArrowIcon);
		RotateTransition rtImgMenuArrow = new RotateTransition(new Duration(duration), this.paneMenuController.imgMenuArrow);
		ParallelTransition transition = new ParallelTransition(
				ttMenu, ttGrid, ttTurn, ttImgMenuIcon, rtImgMenuArrow,
				this.createMinWidthAnim(this.paneGrid, 846.0, duration),
				this.createMinWidthAnim(this.paneTurnController.lblX, 424.0, duration),
				this.createMinWidthAnim(this.paneTurnController.lblO, 424.0, duration),
				this.createMinWidthAnim(this.paneTurn, 848.0, duration)
		);
		ttMenu.setToX(-250.0);
		ttGrid.setToX(-250.0);
		ttTurn.setToX(-252.0);
		ttImgMenuIcon.setToX(0.0);
		rtImgMenuArrow.setToAngle(0.0);
		this.paneMenuController.imgMenuArrowIcon.setImage(new Image(new File(RES.GEAR_ICON).toURI().toString()));
		
		transition.play();
	}
	
	//events handlers
	private void handleMouseHoverOnMenu(MouseEvent event) {
		String evtType = event.getEventType().toString();
		if(evtType.equals("MOUSE_ENTERED") && !this.menuOpened)
			this.playOpeningMenuAnim(200);
		else if(evtType.equals("MOUSE_EXITED") && this.menuOpened)
			this.playClosingMenuAnim(200);
	}

	public static class Grid {
		private View ihm;
		private List<Label> squares;
		
		private HashMap<Row, Label> mapRowStartingSquare;
		
		@FXML private Canvas canvasGrid;
		@FXML private ImageView imgRenew;
		@FXML private ImageView imgCup;
		
//		cases du morpion
		@FXML private Label lblSquare0; @FXML private Label lblSquare1; @FXML private Label lblSquare2;
		@FXML private Label lblSquare3; @FXML private Label lblSquare4; @FXML private Label lblSquare5;
		@FXML private Label lblSquare6; @FXML private Label lblSquare7; @FXML private Label lblSquare8;
		
		@FXML private void initialize() {
			this.mapRowStartingSquare = new HashMap<>();
			this.mapRowStartingSquare.put(Row.HORIZONTAL_1, Grid.this.lblSquare0);
			this.mapRowStartingSquare.put(Row.HORIZONTAL_2, Grid.this.lblSquare3);
			this.mapRowStartingSquare.put(Row.HORIZONTAL_3, Grid.this.lblSquare6);
			this.mapRowStartingSquare.put(Row.VERTICAL_1, Grid.this.lblSquare0);
			this.mapRowStartingSquare.put(Row.VERTICAL_2, Grid.this.lblSquare1);
			this.mapRowStartingSquare.put(Row.VERTICAL_3, Grid.this.lblSquare2);
			this.mapRowStartingSquare.put(Row.DIAGONAL_1, Grid.this.lblSquare0);
			this.mapRowStartingSquare.put(Row.DIAGONAL_2, Grid.this.lblSquare2);
			
			this.squares = new ArrayList<>(Arrays.asList(
					lblSquare0, lblSquare1, lblSquare2, 
					lblSquare3, lblSquare4, lblSquare5, 
					lblSquare6, lblSquare7, lblSquare8
			));
			for(Label c : this.squares)
			{
				c.setOnMouseClicked(this::handleMouseEventOnCase);
				c.setOnMouseEntered(this::handleMouseEventOnCase);
				c.setOnMouseExited(this::handleMouseEventOnCase);
			}
			this.imgRenew.setOnMouseClicked(this::handleMouseEventOnRenew);
			this.imgRenew.setOnMouseEntered(this::handleMouseEventOnRenew);
			this.imgRenew.setOnMouseExited(this::handleMouseEventOnRenew);
				
		}
		
		public void injectMainController(View ihm)
		{
			this.ihm = ihm;
		}
		
		public void writeCase(int id, Square c)
		{
			this.squares.get(id).setText(c.toString());
		}
		
		public Animation getWinningRowAnim(Row row, int duration)
		{
			Label startingSquare = this.mapRowStartingSquare.get(row);
			int rowValue = row.getValue();

			double xFrom = startingSquare.getBoundsInParent().getMinX();
			double yFrom = startingSquare.getBoundsInParent().getMinY();
			double xTo = xFrom;
			double yTo = yFrom;
			
//			horizontal row
			if(rowValue >= 10 && rowValue <= 12) {
				xTo += 580.0;
				yFrom += 97.0;
				yTo = yFrom;
			}
//			vertical row
			else if(rowValue >= 20 && rowValue <= 22) {
				xFrom += 97.0;
				xTo = xFrom;
				yTo += 580.0;
			}
			else if(row == Row.DIAGONAL_1) {
				xTo += 580.0;
				yTo += 580.0;
			}
			else if(row == Row.DIAGONAL_2) {
				xFrom = startingSquare.getBoundsInParent().getMaxX();
				xTo = xFrom - 580.0;
				yTo += 580.0;
			}
			
			GraphicsContext gc = this.canvasGrid.getGraphicsContext2D();
			
			Path path = new Path();
			path.setStroke(Color.RED);
			path.setStrokeWidth(10.0);
			path.getElements().addAll(new MoveTo(xFrom, yFrom), new LineTo(xTo, yTo));
			
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
		
		public Animation getCupAnim(Player winner, int duration)
		{
			TranslateTransition ttUp = new TranslateTransition(new Duration(duration * 0.3), this.imgCup);
			TranslateTransition ttDown = new TranslateTransition(new Duration(duration * 0.3), this.imgCup);
			SequentialTransition anim = new SequentialTransition(ttUp,new PauseTransition(new Duration(duration * 0.4)) , ttDown);
			if(winner == Player.X) 
				ttUp.setFromX(180.0);
			else if(winner == Player.O) 
				ttUp.setFromX(605.0);
			ttUp.setByY(-110.0);
			ttDown.setByY(110.0);
			
			return anim;
		}
		
		public void clearCanvas()
		{
			this.canvasGrid.getGraphicsContext2D().clearRect(0, 0, 
					this.canvasGrid.getWidth(), this.canvasGrid.getHeight());
		}
		
//		events handlers
		private void handleMouseEventOnCase(MouseEvent event) {
			Controller ctrl = Grid.this.ihm.getCtrl();
			Label lbl = (Label)event.getSource();
			int lblId = Integer.parseInt(lbl.getId().substring(lbl.getId().length() - 1));
			String eventType = event.getEventType().toString();
			
			if(eventType.equals("MOUSE_CLICKED")) {
				if(!this.ihm.getWinningRowAnimOccuring())
				{
					try {
						if(Grid.this.ihm.getCtrl().proposerCoup(lblId))
							lbl.setOpacity(1.0);
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			else if(ctrl.caseVide(lblId)) {
				if(eventType.equals("MOUSE_ENTERED")) {
					Player j = ctrl.getJoueurCourant();
					lbl.setOpacity(0.3);
					lbl.setText(j.toString());
				}
				else if(eventType.equals("MOUSE_EXITED")) {
					lbl.setText(Square.EMPTY.toString());
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
	
	public static class Turn {
		@FXML private Label lblX;
		@FXML private Label lblO;
		
		@FXML private void initialize() {
			this.setTurn(Player.X);
		}
		
		public void setTurn(Player p)
		{
			if(p == Player.X)
			{
				this.lblX.getStyleClass().clear(); this.lblX.getStyleClass().add("filled-turn-label");
				this.lblO.getStyleClass().clear(); this.lblO.getStyleClass().add("empty-turn-label");
			}
			else if(p == Player.O)
			{
				this.lblX.getStyleClass().clear(); this.lblX.getStyleClass().add("empty-turn-label");
				this.lblO.getStyleClass().clear(); this.lblO.getStyleClass().add("filled-turn-label");
			}
		}
	}
	
	public static class Menu {
		private View ihm;
		private List<ImageView> imgsGameMode;
		private List<ImageView> imgsButtons;
		
		@FXML private ImageView imgGameMode0;
		@FXML private ImageView imgGameMode1;
		@FXML private Slider slDiff;
		@FXML private ImageView imgArrowUp;
		@FXML private ImageView imgArrowDown;
		@FXML private ImageView imgRules;
		@FXML private ImageView imgEditConfig;
		@FXML private ImageView imgMenuArrow;
		@FXML private ImageView imgMenuArrowIcon;
		@FXML private HBox subpanelDiff;
		
		@FXML private void initialize() {
			this.imgsGameMode = Arrays.asList(this.imgGameMode0, this.imgGameMode1);
			this.imgsButtons = Arrays.asList(this.imgRules, this.imgEditConfig);
			
			for(ImageView iv : this.imgsGameMode) {
				iv.setOnMouseClicked(this::handleMouseEventOnMode);
				iv.setOnMouseEntered(this::handleMouseEventOnMode);
				iv.setOnMouseExited(this::handleMouseEventOnMode);
			}
			for(ImageView iv : this.imgsButtons) {
				iv.setOnMouseEntered(this::handleMouseHoverOnImgs);
				iv.setOnMouseExited(this::handleMouseUnhoverOnImgs);
			}
			
			this.slDiff.valueProperty().addListener((obs, oldVal, newVal) -> this.slDiff.setValue(Math.round(newVal.doubleValue())));
			this.slDiff.setOnMouseReleased(this::handleMouseEventOnSlider);
			this.imgArrowUp.setOnMouseClicked(this::handleMouseEventOnUpArrow);
			this.imgArrowDown.setOnMouseClicked(this::handleMouseEventOnDownArrow);
			
			this.imgRules.setOnMouseClicked(event -> Menu.this.ihm.getCtrl().showDialogRegles());
			this.imgEditConfig.setOnMouseClicked(event -> Menu.this.ihm.getCtrl().editConfigFile());
		}
		
		public void injectMainController(View ihm)
		{
			this.ihm = ihm;
		}
		
		public void setModeJeu(Mode mode)
		{
			if(mode == Mode.P_VS_AI) {
				this.imgGameMode0.setImage(new Image(new File(RES.P_VS_AI_PRESSED).toURI().toString()));
				this.imgGameMode1.setImage(new Image(new File(RES.P_VS_P_UNPRESSED).toURI().toString()));
			}
			else if(mode == Mode.P_VS_P) {
				this.imgGameMode0.setImage(new Image(new File(RES.P_VS_AI_UNPRESSED).toURI().toString()));
				this.imgGameMode1.setImage(new Image(new File(RES.P_VS_P_PRESSED).toURI().toString()));
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
			Difficulty diff = Difficulty.values()[(int)Menu.this.slDiff.getValue()];
			try {
				Menu.this.ihm.getCtrl().changerDiff(diff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void handleMouseEventOnUpArrow(MouseEvent event) {
			int value = (int)Menu.this.slDiff.getValue() + 1;
			if(value < Difficulty.values().length) {
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
