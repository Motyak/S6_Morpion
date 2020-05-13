package morpion;

import Mk.Pair;
import Mk.TextFile;
import javafx.animation.Animation;
import java.io.IOException;

/**
 * Is responsible for the realisation of the application use cases (and so the synchronization between the entity and the view)
 * @author Tommy 'Motyak'
 *
 */
class Controller {
	private View view;
	private Ent ent;
	
	private Ai ai;

	/**
	 * @param view the main interface
	 * @param ent the entity(ies)
	 * @throws IOException in case there is a problem writing files in the user directory or writing/reading the ai model file
	 * @throws ClassNotFoundException in case the serialized object doesn't match the ai model class
	 */
	Controller(View view, Ent ent) throws ClassNotFoundException, IOException
	{
		this.view = view;
		this.ent = ent;
		this.ai = new Ai(this.ent.getDiff());
		
		this.launchConfiguring(this);
		this.launchLearning();
	}
	
	/**
	 * Launch the learning task in a thread
	 */
	public void launchLearning()
	{
		if(Main.learningThread != null) {
			Main.learningThread.interrupt();
			while(Main.learningThread.isAlive())
				;
		}
		
		Main.learningThread = new Thread(new TaskLearning<>(this.ai));
		Main.learningThread.setDaemon(true);
		Main.learningThread.start();
	}
	
	/**
	 * Check if the submitted move is possible, if so, play it and check on win
	 * @param id the id of the square in the grid (0,1,2, 3,4,5, 6,7,8)
	 * @return true if the move is possible, false otherwise
	 * @throws IOException in case the file storing the moves cannot be found
	 */
	public boolean submitMove(int id) throws IOException
	{
		Ent.Grid grille = this.ent.getGrid();
		
		if(grille.at(id) == Square.EMPTY)
		{
			this.playMove(id);
			if(this.checkOnWin())
				return true;
			
			if(this.ent.getMode() == Mode.P_VS_AI)
			{
				this.playMove(this.aiPlays());
				this.checkOnWin();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Change the game mode
	 * @param mode the mode to apply
	 */
	public void changeMode(Mode mode)
	{
		if(this.ent.getMode() == mode)
			return;
		
		this.ent.setMode(mode);
		this.renewGame();
		System.out.println("Actual game mode : " + this.ent.getMode());
	}
	
	/**
	 * Change the game difficulty
	 * @param diff the difficulty to apply
	 * @throws IOException in case the file storing the configuration cannot be found
	 * @throws ClassNotFoundException in case the serialized object doesn't match the ai model class
	 */
	public void changeDiff(Difficulty diff) throws ClassNotFoundException, IOException
	{
		if(this.ent.getDiff() == diff)
			return;
		
		this.ent.setDiff(diff);
		this.ai.changeDiff(diff);
		this.renewGame();
		
		this.launchLearning();
		
		System.out.println("Actual difficulty : " + this.ent.getDiff().getValue());
	}
	
	/**
	 * Show the rules dialog
	 */
	public void showRules()
	{
		Main.rulesDialog.showAndWait();
	}
	
	/**
	 * Renew the game (clear the grid, reset ai data,..)
	 */
	public void renewGame()
	{
		this.ent.getGrid().clear();
		this.ai.data.reset();
		this.ent.setTurn(Player.values()[0]);
		this.updateView();
	}
	
	/**
	 * Edit the AI configuration file using system's default text editor
	 */
	public void editAiConf()
	{
		try {
			this.ai.editConfigFile();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public Player getCurrentPlayer()
	{
		return this.ent.getTurn();
	}
	
	/**
	 * Check if a mode if the actual mode
	 * @param mode a mode
	 * @return true if the entered mode is the actual mode, false otherwise
	 */
	public boolean isActualMode(Mode mode)
	{
		return this.ent.getMode() == mode;
	}
	
	/**
	 * Check if a square in the grid is empty
	 * @param id the id of the square in the grid
	 * @return true if the considered square is empty, false otherwise
	 */
	public boolean isEmptySquare(int id)
	{
		return this.ent.getGrid().at(id) == Square.EMPTY;
	}
	
	/**
	 * Launch the configuring task in a thread
	 * @param ctrl the main controller
	 */
	private void launchConfiguring(Controller ctrl)
	{
		if(Main.configThread != null) {
			Main.configThread.interrupt();
			while(Main.configThread.isAlive())
				;
		}
		
		Main.configThread = new Thread(new TaskConfiguring<>(ctrl));
		Main.configThread.setDaemon(true);
		Main.configThread.start();
	}
	
	/**
	 * update the view based on the ent data
	 */
	private void updateView() 
	{
		Ent.Grid grid = this.ent.getGrid();
		View.Grid gridView = this.view.getGrid();
		
		gridView.setGrid(grid);
		this.view.getTurn().setTurn(this.ent.getTurn());
		
		this.view.getMenu().setMode(this.ent.getMode());
		this.view.getMenu().lockDiff((this.ent.getMode() == Mode.P_VS_P));
	}
	
	/**
	 * Play a move in the grid
	 * @param id the id of the square in the grid in which to play
	 */
	private void playMove(int id)
	{
		Ent.Grid grille = this.ent.getGrid();
		
		Ent.Grid save = new Ent.Grid(grille);
		grille.set(id, Square.valueOf(this.ent.getTurn().toString()));
		if(this.ent.getTurn() == Player.X)
			this.ai.data.Xmoves.add(new Ai.Data.Move(save, new Ent.Grid(grille)));
		else
			this.ai.data.Omoves.add(new Ai.Data.Move(save, new Ent.Grid(grille)));
		this.incrementTurn();
		this.updateView();
	}
	
	/**
	 * Check if the game is ended, if so, check if there is a winner and deal with it
	 * @return true if the game ended, false otherwise 
	 * @throws IOException in case the moves file doesn't exist or cannot be written
	 */
	private boolean checkOnWin() throws IOException
	{
		Ent.Grid grid = this.ent.getGrid();
		Pair<Player,Row> p = grid.calculateOutcome();
		
		Player winner = p.first;
		boolean gameEnded = (winner != null) || grid.isFilled();
		if(gameEnded)
		{
			if(winner != null)
			{
				System.out.println("Winner is " + winner.toString());
				this.view.getTurn().setTurn(winner);
				Animation rowAnim = this.view.getGrid().getWinningRowAnim(p.second);
				Animation cupAnim = this.view.getGrid().getCupAnim(winner);
				this.view.setWinningRowAnimOccuring(true);
				rowAnim.play();
				cupAnim.play();
				cupAnim.setOnFinished(e -> {
					grid.clear();
					this.ent.setTurn(Player.values()[0]);
					this.updateView();
					this.view.getGrid().clearCanvas();
					this.view.setWinningRowAnimOccuring(false);
				});
				TextFile.stringToFile(this.ai.data.getMoves(winner), 
						Ai.DATA_DIRPATH + Ai.MOVES_FILENAME, true);
				this.ai.learn();
			}
			else {				
				System.out.println("No winner");
				grid.clear();
				this.ent.setTurn(Player.values()[0]);
				this.updateView();
			}
			this.ai.data.reset();
			return true;
		}
		return false;
	}
	
	/**
	 * Make the ai play generating a move
	 * @return the id of the square in the grid played by the ai
	 */
	private int aiPlays()
	{
		Ent.Grid grille = this.ent.getGrid();
		
		double[] input = this.gridToDoubles(grille);
		int[] output = this.ai.genOutput(input);
		int i = 0;
		while(grille.at(output[i]) != Square.EMPTY)
			++i;

		return output[i];
	}
	
	private void incrementTurn()
	{
		Player j = this.ent.getTurn();
		j = j.next();
		this.ent.setTurn(j);
	}
	
	/**
	 * Convert a grid to a set of decimals
	 * @param grille a grid
	 * @return a grid in a group of decimals format
	 */
	private double[] gridToDoubles(Ent.Grid grille)
	{
		double[] res = new double[Ent.GRID_SIZE];
		for(int i = 0 ; i < Ent.GRID_DIM ; ++i)
			for(int j = 0 ; j < Ent.GRID_DIM ; ++j)
				res[Ent.GRID_DIM * i + j] = grille.at(i, j).getValue();
		
		return res;
	}
}
