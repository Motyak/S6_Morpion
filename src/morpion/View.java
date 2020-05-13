package morpion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import javafx.animation.RotateTransition;
import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * Is responsible for handling events, calling the controller use cases and playing animations
 * @author Tommy 'Motyak'
 *
 */
public class View {
	private Controller ctrl;
	private AnimationFactory animFactory;
	
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
		
		this.animFactory = new AnimationFactory(this);
		
		this.paneMenu.setOnMouseEntered(this::handleMouseHoverOnMenu);
		this.paneMenu.setOnMouseExited(this::handleMouseHoverOnMenu);
	}
	
	public Controller getCtrl() { return this.ctrl; }
	public Grid getGrid() { return this.paneGridController; }
	public Turn getTurn() { return this.paneTurnController; }
	public Menu getMenu() { return this.paneMenuController; }
	public GridPane getPaneGrid() { return this.paneGrid; }
	public GridPane getPaneTurn() { return this.paneTurn; }
	public GridPane getPaneMenu() { return this.paneMenu; }
	public boolean getWinningRowAnimOccuring() { return this.winningRowAnimOccuring; }
	public void setWinningRowAnimOccuring(boolean occuring) { this.winningRowAnimOccuring = occuring; }
	
	private void playOpeningMenuAnim()
	{
		this.menuOpened = true;
		this.paneMenuController.imgMenuArrowIcon.setImage(new Image(new File(RES.GAMEPAD_ICON).toURI().toString()));
		this.animFactory.getOpeningMenu().play();
	}
	
	private void playClosingMenuAnim()
	{
		this.menuOpened = false;
		this.paneMenuController.imgMenuArrowIcon.setImage(new Image(new File(RES.GEAR_ICON).toURI().toString()));
		this.animFactory.getClosingMenu().play();
	}
	
	//events handlers
	private void handleMouseHoverOnMenu(MouseEvent event) {
		String evtType = event.getEventType().toString();
		if(evtType.equals("MOUSE_ENTERED") && !this.menuOpened)
			this.playOpeningMenuAnim();
		else if(evtType.equals("MOUSE_EXITED") && this.menuOpened)
			this.playClosingMenuAnim();
	}

	/**
	 * Represent the grid in the view
	 * @author Tommy 'Motyak'
	 *
	 */
	public static class Grid {
		private View ihm;
		private List<Label> squares;
		
		@FXML private Canvas canvasGrid;
		@FXML private ImageView imgRenew;
		@FXML private ImageView imgCup;
		
//		cases du morpion
		@FXML private Label lblSquare0; @FXML private Label lblSquare1; @FXML private Label lblSquare2;
		@FXML private Label lblSquare3; @FXML private Label lblSquare4; @FXML private Label lblSquare5;
		@FXML private Label lblSquare6; @FXML private Label lblSquare7; @FXML private Label lblSquare8;
		
		@FXML private void initialize() {
			this.squares = new ArrayList<>(Arrays.asList(
					lblSquare0, lblSquare1, lblSquare2, 
					lblSquare3, lblSquare4, lblSquare5, 
					lblSquare6, lblSquare7, lblSquare8
			));
			for(Label c : this.squares)
			{
				c.setOnMouseClicked(this::handleMouseEventOnSquare);
				c.setOnMouseEntered(this::handleMouseEventOnSquare);
				c.setOnMouseExited(this::handleMouseEventOnSquare);
			}
			this.imgRenew.setOnMouseClicked(this::handleMouseEventOnRenew);
			this.imgRenew.setOnMouseEntered(this::handleMouseEventOnRenew);
			this.imgRenew.setOnMouseExited(this::handleMouseEventOnRenew);
				
		}
		
		public List<Label> getSquares() { return this.squares; }
		public Canvas getCanvasGrid() { return this.canvasGrid; }
		public ImageView getImgCup() { return this.imgCup; }
		
		/**
		 * inject the main view to the grid sub-view
		 * @param ihm the main interface/view
		 */
		public void injectMainController(View ihm)
		{
			this.ihm = ihm;
		}
		
		/**
		 * Update the grid view from a grid
		 * @param grid a grid
		 */
		public void setGrid(Ent.Grid grid)
		{
			for(int i = 0 ; i < Ent.GRID_SIZE ; ++i)
				this.squares.get(i).setText(grid.at(i).toString());
		}
		
		/**
		 * 
		 * @param row a row considered being the winning row
		 * @return the matching winning row animation
		 */
		public Animation getWinningRowAnim(Row row)
		{
			return this.ihm.animFactory.getDrawingWinningRow(row);
		}
		
		/**
		 * @param winner a player considered being the winner of a game
		 * @return the matching cup animation
		 */
		public Animation getCupAnim(Player winner)
		{
			if(winner == Player.X)
				return this.ihm.animFactory.getMovingCupWinnerX();
			else if(winner == Player.O)
				return this.ihm.animFactory.getMovingCupWinnerO();
			return null;
		}
		
		public void clearCanvas()
		{
			this.canvasGrid.getGraphicsContext2D().clearRect(0, 0, 
					this.canvasGrid.getWidth(), this.canvasGrid.getHeight());
		}
		
//		events handlers
		private void handleMouseEventOnSquare(MouseEvent event) {
			Controller ctrl = Grid.this.ihm.getCtrl();
			Label lbl = (Label)event.getSource();
			int lblId = Integer.parseInt(lbl.getId().substring(lbl.getId().length() - 1));
			String eventType = event.getEventType().toString();
			
			if(eventType.equals("MOUSE_CLICKED")) {
				if(!this.ihm.getWinningRowAnimOccuring())
				{
					try {
						if(Grid.this.ihm.getCtrl().submitMove(lblId))
							lbl.setOpacity(1.0);
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			else if(ctrl.isEmptySquare(lblId)) {
				if(eventType.equals("MOUSE_ENTERED")) {
					Player j = ctrl.getCurrentPlayer();
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
	
	/**
	 * Represent the turn in the view
	 * @author Tommy 'Motyak'
	 *
	 */
	public static class Turn {
		@FXML private Label lblX;
		@FXML private Label lblO;
		@FXML private ImageView imgOpponent;
		
		@FXML private void initialize() {
			this.setTurn(Player.X);
		}
		
		public Label getLblX() { return this.lblX; }
		public Label getLblO() { return this.lblO; }
		
		
		/**
		 * Update the turn view from a turn
		 * @param p the current player, the player whose turn it is to play
		 */
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
		
		/**
		 * Adapt the opponent image based on the game mode
		 * @param mode a mode
		 */
		public void setOpponent(Mode mode)
		{
			if(mode == Mode.P_VS_AI)
				this.imgOpponent.setImage(new Image(new File(RES.OPPONENT_AI).toURI().toString()));
			else if(mode == Mode.P_VS_P)
				this.imgOpponent.setImage(new Image(new File(RES.OPPONENT_PERSON).toURI().toString()));
		}
	}
	
	/**
	 * Represent the menu in the view
	 * @author Tommy 'Motyak'
	 *
	 */
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
				iv.setOnMouseEntered(this::handleMouseEventOnImgs);
				iv.setOnMouseExited(this::handleMouseEventOnImgs);
			}
			
			this.slDiff.valueProperty().addListener((obs, oldVal, newVal) -> this.slDiff.setValue(Math.round(newVal.doubleValue())));
			this.slDiff.setOnMouseReleased(this::handleMouseEventOnSlider);
			this.imgArrowUp.setOnMouseClicked(this::handleMouseEventOnUpArrow);
			this.imgArrowDown.setOnMouseClicked(this::handleMouseEventOnDownArrow);
			
			this.imgRules.setOnMouseClicked(event -> Menu.this.ihm.getCtrl().showRules());
			this.imgEditConfig.setOnMouseClicked(event -> Menu.this.ihm.getCtrl().editAiConf());
		}
		
		public ImageView getImgMenuArrowIcon() { return this.imgMenuArrowIcon; }
		public ImageView getImgMenuArrow() { return this.imgMenuArrow; }
		
		/**
		 * Inject the main view to the menu sub-view
		 * @param ihm the main view
		 */
		public void injectMainController(View ihm)
		{
			this.ihm = ihm;
		}
		
		/**
		 * Update the view game mode from a game mode
		 * @param mode a game mode
		 */
		public void setMode(Mode mode)
		{
			if(mode == Mode.P_VS_AI) {
				this.imgGameMode0.setImage(new Image(new File(RES.P_VS_AI_PRESSED).toURI().toString()));
				this.imgGameMode1.setImage(new Image(new File(RES.P_VS_P_UNPRESSED).toURI().toString()));
			}
			else if(mode == Mode.P_VS_P) {
				this.imgGameMode0.setImage(new Image(new File(RES.P_VS_AI_UNPRESSED).toURI().toString()));
				this.imgGameMode1.setImage(new Image(new File(RES.P_VS_P_PRESSED).toURI().toString()));
			}
			this.ihm.paneTurnController.setOpponent(mode);
		}
		
		/**
		 * Lock the difficulty access from the view
		 * @param lock if should be lock
		 */
		public void lockDiff(boolean lock)
		{
			this.subpanelDiff.setDisable(lock);
		}
		
//		les events handlers
		private void handleMouseEventOnMode(MouseEvent event) {
			String evtType = event.getEventType().toString();
			ImageView iv = (ImageView)event.getSource();
			int idMode = Integer.valueOf(iv.getId().substring(iv.getId().length() - 1));
			Mode mode = Mode.get(idMode);
			boolean isActualMode = Menu.this.ihm.getCtrl().isActualMode(mode);

			if(evtType.equals("MOUSE_CLICKED"))
				Menu.this.ihm.getCtrl().changeMode(mode);
			
			else if(evtType.equals("MOUSE_ENTERED") && !isActualMode)
				iv.setImage(new Image(new File(RES.getHover(mode)).toURI().toString()));
			
			else if(evtType.equals("MOUSE_EXITED") && !isActualMode)
				iv.setImage(new Image(new File(RES.getUnpressed(mode)).toURI().toString()));
		}
		
		private void handleMouseEventOnSlider(MouseEvent event) {
			Difficulty diff = Difficulty.values()[(int)Menu.this.slDiff.getValue()];
			try {
				Menu.this.ihm.getCtrl().changeDiff(diff);
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
		
		private void handleMouseEventOnImgs(MouseEvent event) {
			ImageView iv = (ImageView)event.getSource();
			String eventType = event.getEventType().toString();
			
			if(eventType.equals("MOUSE_ENTERED"))
				iv.setEffect(new DropShadow(2.0, javafx.scene.paint.Color.BLACK));
			else if(eventType.equals("MOUSE_EXITED"))
				iv.setEffect(null);
		}
	}

}
