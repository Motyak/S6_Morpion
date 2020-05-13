package morpion;

import Mk.Pair;
import Mk.TextFile;
import javafx.animation.Animation;
import java.io.IOException;

class Controller {
	private View view;
	private Ent ent;
	
	private Ai ai;

	Controller(View view, Ent ent) throws Exception 
	{
		this.view = view;
		this.ent = ent;
		this.ai = new Ai(this.ent.getDiff());
		
		this.launchConfiguring(this);
		this.launchLearning();
	}
	
	public void launchLearning() throws IOException 
	{
//		this.ai.reset();
		if(Main.learningThread != null) {
			Main.learningThread.interrupt();
			while(Main.learningThread.isAlive())
				;
		}
		
		Main.learningThread = new Thread(new TaskLearning<>(this.ai));
		Main.learningThread.setDaemon(true);
		Main.learningThread.start();
	}
	
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
	
	public void changeMode(Mode mode)
	{
		if(this.ent.getMode() == mode)
			return;
		
		this.ent.setMode(mode);
		this.renewGame();
		System.out.println("Actual game mode : " + this.ent.getMode());
	}
	
	public void changeDiff(Difficulty diff) throws Exception
	{
		if(this.ent.getDiff() == diff)
			return;
		
		this.ent.setDiff(diff);
		this.ai.changeDiff(diff);
		this.renewGame();
		
		this.launchLearning();
		
		System.out.println("Actual difficulty : " + this.ent.getDiff().getValue());
	}
	
	public void showRules()
	{
		Main.rulesDialog.showAndWait();
	}
	
	public void renewGame()
	{
		this.ent.getGrid().clear();
		this.ai.data.reset();
		this.ent.setTurn(Player.values()[0]);
		this.updateView();
	}
	
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
	
	public boolean isActualMode(Mode mode)
	{
		return this.ent.getMode() == mode;
	}
	
	public boolean isEmptySquare(int id)
	{
		return this.ent.getGrid().at(id) == Square.EMPTY;
	}
	
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
	
	private void updateView() 
	{
		Ent.Grid grid = this.ent.getGrid();
		View.Grid gridView = this.view.getGrid();
		
		gridView.setGrid(grid);
		this.view.getTurn().setTurn(this.ent.getTurn());
		
		this.view.getMenu().setMode(this.ent.getMode());
		this.view.getMenu().lockDiff((this.ent.getMode() == Mode.P_VS_P));
	}
	
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
	
	private double[] gridToDoubles(Ent.Grid grille)
	{
		double[] res = new double[Ent.GRID_SIZE];
		for(int i = 0 ; i < Ent.GRID_DIM ; ++i)
			for(int j = 0 ; j < Ent.GRID_DIM ; ++j)
				res[Ent.GRID_DIM * i + j] = grille.at(i, j).getValue();
		
		return res;
	}
}
