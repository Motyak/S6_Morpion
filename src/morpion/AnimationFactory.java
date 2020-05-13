package morpion;

import java.util.HashMap;
import java.util.List;

import Mk.Pair;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

class AnimationFactory {
	
	final private int OPENING_MENU_DURATION = 200;
	final private int CLOSING_MENU_DURATION = OPENING_MENU_DURATION;
	final private int MOVING_CUP_DURATION = 1000;
	final private int DRAWING_WINNING_ROW_DURATION = 500;
	
	private HashMap<Row, Label> mapRowStartingSquare;
	private View view;
	
	private ParallelTransition openingMenu = null;
	private ParallelTransition closingMenu = null;
	private SequentialTransition movingCupWinnerX = null;
	private SequentialTransition movingCupWinnerO = null;
	private PathTransition drawingWinningRow;
	
	public AnimationFactory(View view) {
		this.view = view;
		
		List<Label> sq = view.getGrid().getSquares();
		this.mapRowStartingSquare = new HashMap<>();
		this.mapRowStartingSquare.put(Row.HORIZONTAL_1, sq.get(0));
		this.mapRowStartingSquare.put(Row.HORIZONTAL_2, sq.get(3));
		this.mapRowStartingSquare.put(Row.HORIZONTAL_3, sq.get(6));
		this.mapRowStartingSquare.put(Row.VERTICAL_1, sq.get(0));
		this.mapRowStartingSquare.put(Row.VERTICAL_2, sq.get(1));
		this.mapRowStartingSquare.put(Row.VERTICAL_3, sq.get(2));
		this.mapRowStartingSquare.put(Row.DIAGONAL_1, sq.get(0));
		this.mapRowStartingSquare.put(Row.DIAGONAL_2, sq.get(2));
	}
	
	public ParallelTransition getOpeningMenu() { 
		if(this.openingMenu == null)
			this.createOpeningMenu();
		return openingMenu; 
	}
	
	public ParallelTransition getClosingMenu() { 
		if(this.closingMenu == null)
			this.createClosingMenu();
		return closingMenu; 
	}
	
	public SequentialTransition getMovingCupWinnerX() { 
		if(this.movingCupWinnerX == null)
			this.createMovingCupWinnerX();
		return this.movingCupWinnerX; 
	}
	
	public SequentialTransition getMovingCupWinnerO() { 
		if(this.movingCupWinnerO == null)
			this.createMovingCupWinnerO();
		return this.movingCupWinnerO; 
	}
	
	public PathTransition getDrawingWinningRow(Row row) {
		this.createDrawingWinningRow(row);
		return this.drawingWinningRow;
	}
	
	private void createOpeningMenu() {
		TranslateTransition ttMenu = new TranslateTransition(new Duration(OPENING_MENU_DURATION), this.view.getPaneMenu());
		TranslateTransition ttGrid = new TranslateTransition(new Duration(OPENING_MENU_DURATION), this.view.getPaneGrid());
		TranslateTransition ttTurn = new TranslateTransition(new Duration(OPENING_MENU_DURATION), this.view.getPaneTurn());
		TranslateTransition ttImgMenuIcon = new TranslateTransition(new Duration(OPENING_MENU_DURATION), this.view.getMenu().getImgMenuArrowIcon());
		RotateTransition rtImgMenuArrow = new RotateTransition(new Duration(OPENING_MENU_DURATION), this.view.getMenu().getImgMenuArrow());
		ParallelTransition transition = new ParallelTransition(
				ttMenu, ttGrid, ttTurn, ttImgMenuIcon, rtImgMenuArrow,
				this.createMinWidthAnim(this.view.getPaneGrid(), 595.0, OPENING_MENU_DURATION),
				this.createMinWidthAnim(this.view.getTurn().getLblX(), 300.0, OPENING_MENU_DURATION),
				this.createMinWidthAnim(this.view.getTurn().getLblO(), 300.0, OPENING_MENU_DURATION),
				this.createMinWidthAnim(this.view.getPaneTurn(), 595.0, OPENING_MENU_DURATION)
		);
		ttMenu.setToX(0.0);
		ttGrid.setToX(0.0);
		ttTurn.setToX(-1.0);
		ttImgMenuIcon.setToX(-23.0);
		rtImgMenuArrow.setToAngle(180.0);
		
		this.openingMenu = transition;
	}
	
	private void createClosingMenu() {
		TranslateTransition ttMenu = new TranslateTransition(new Duration(CLOSING_MENU_DURATION), this.view.getPaneMenu());
		TranslateTransition ttGrid = new TranslateTransition(new Duration(CLOSING_MENU_DURATION), this.view.getPaneGrid());
		TranslateTransition ttTurn = new TranslateTransition(new Duration(CLOSING_MENU_DURATION), this.view.getPaneTurn());
		TranslateTransition ttImgMenuIcon = new TranslateTransition(new Duration(CLOSING_MENU_DURATION), this.view.getMenu().getImgMenuArrowIcon());
		RotateTransition rtImgMenuArrow = new RotateTransition(new Duration(CLOSING_MENU_DURATION), this.view.getMenu().getImgMenuArrow());
		ParallelTransition transition = new ParallelTransition(
				ttMenu, ttGrid, ttTurn, ttImgMenuIcon, rtImgMenuArrow,
				this.createMinWidthAnim(this.view.getPaneGrid(), 846.0, CLOSING_MENU_DURATION),
				this.createMinWidthAnim(this.view.getTurn().getLblX(), 424.0, CLOSING_MENU_DURATION),
				this.createMinWidthAnim(this.view.getTurn().getLblO(), 424.0, CLOSING_MENU_DURATION),
				this.createMinWidthAnim(this.view.getPaneTurn(), 848.0, CLOSING_MENU_DURATION)
		);
		ttMenu.setToX(-250.0);
		ttGrid.setToX(-250.0);
		ttTurn.setToX(-252.0);
		ttImgMenuIcon.setToX(0.0);
		rtImgMenuArrow.setToAngle(0.0);
		
		this.closingMenu = transition;
	}
	
	private void createMovingCupWinnerX() {
		TranslateTransition ttUp = new TranslateTransition(new Duration(MOVING_CUP_DURATION * 0.3), this.view.getGrid().getImgCup());
		TranslateTransition ttDown = new TranslateTransition(new Duration(MOVING_CUP_DURATION * 0.3), this.view.getGrid().getImgCup());
		SequentialTransition anim = new SequentialTransition(ttUp,new PauseTransition(new Duration(MOVING_CUP_DURATION * 0.4)) , ttDown);
		ttUp.setFromX(180.0);
		ttUp.setByY(-110.0);
		ttDown.setByY(110.0);
		this.movingCupWinnerX = anim;
	}
	
	private void createMovingCupWinnerO() {
		TranslateTransition ttUp = new TranslateTransition(new Duration(MOVING_CUP_DURATION * 0.3), this.view.getGrid().getImgCup());
		TranslateTransition ttDown = new TranslateTransition(new Duration(MOVING_CUP_DURATION * 0.3), this.view.getGrid().getImgCup());
		SequentialTransition anim = new SequentialTransition(ttUp,new PauseTransition(new Duration(MOVING_CUP_DURATION * 0.4)) , ttDown);
		ttUp.setFromX(605.0);
		ttUp.setByY(-110.0);
		ttDown.setByY(110.0);
		this.movingCupWinnerO = anim;
	}
	
	private void createDrawingWinningRow(Row row) {
		Label startingSquare = this.mapRowStartingSquare.get(row);
		int rowValue = row.getValue();

		double xFrom = startingSquare.getBoundsInParent().getMinX();
		double yFrom = startingSquare.getBoundsInParent().getMinY();
		double xTo = xFrom;
		double yTo = yFrom;
		
//		horizontal row
		if(rowValue >= 10 && rowValue <= 12) {
			xTo += 580.0;
			yFrom += 97.0;
			yTo = yFrom;
		}
//		vertical row
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
		
		GraphicsContext gc = this.view.getGrid().getCanvasGrid().getGraphicsContext2D();
		
		Path path = new Path();
		path.setStroke(Color.RED);
		path.setStrokeWidth(10.0);
		path.getElements().addAll(new MoveTo(xFrom, yFrom), new LineTo(xTo, yTo));
		
		Circle pen = new Circle(0, 0, 10);
		PathTransition pt = new PathTransition(new Duration(DRAWING_WINNING_ROW_DURATION), path, pen);
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
		this.drawingWinningRow = pt;
	}
	
	private Timeline createMinWidthAnim(Region reg, double minWidth, int duration) {
		Timeline tl = new Timeline();
		tl.getKeyFrames().add(new KeyFrame(Duration.millis(duration), 
				new KeyValue(reg.minWidthProperty(), minWidth)));
		return tl;
	}
	
}
