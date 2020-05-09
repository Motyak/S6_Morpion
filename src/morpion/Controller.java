package morpion;

import Mk.Pair;
import Mk.TextFile;
import javafx.animation.Animation;
import java.io.IOException;

class Controller {
	private View ihm;
	private Ent ent;
	
	private Ai ai;

	Controller(View ihm, Ent ent) throws Exception 
	{
		this.ihm = ihm;
		this.ent = ent;
		this.ai = new Ai(this.ent.getDiff());
		
		this.launchConfiguring(this);
		this.launchLearning(this.ai);
	}
	
	public void entToIhm() 
	{
		Ent.Grid grid = this.ent.getGrille();
		View.Grid gridView = this.ihm.getGrid();
		
		for(int i = 0 ; i < Ent.GRID_SIZE ; ++i)
			gridView.writeCase(i, grid.at(i));
		this.ihm.getTurn().setTurn(this.ent.getTurn());
		
		this.ihm.getMenu().setModeJeu(this.ent.getMode());
		this.ihm.getMenu().lockDiff((this.ent.getMode() == Mode.P_VS_P));
	}
	
	public boolean proposerCoup(int id) throws IOException
	{
		Ent.Grid grille = this.ent.getGrille();
		
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
	
	public void launchLearning(Ai ai) throws IOException 
	{
//		this.ai.reset();
		if(Main.learningThread != null) {
			Main.learningThread.interrupt();
			while(Main.learningThread.isAlive())
				;
		}
		
		Main.learningThread = new Thread(new TaskLearning<>(ai));
		Main.learningThread.setDaemon(true);
		Main.learningThread.start();
	}
	
	public void launchConfiguring(Controller ctrl)
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
	
	public void editConfigFile()
	{
		try {
			this.ai.editConfigFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		
		this.launchLearning(this.ai);
		
		System.out.println("Actual difficulty : " + this.ent.getDiff().getValue());
	}
	
	public void showRulesDialog()
	{
		Main.rulesDialog.showAndWait();
	}
	
	public Ai getAi() { return this.ai; }
	
	public Player getTurn()
	{
		return this.ent.getTurn();
	}
	
	public Mode getMode()
	{
		return this.ent.getMode();
	}
	
	public boolean emptySquare(int id)
	{
		return this.ent.getGrille().at(id) == Square.EMPTY;
	}
	
	public void renewGame()
	{
		this.ent.getGrille().clear();
		this.ai.data.reset();
		this.ent.setTurn(Player.values()[0]);
		this.entToIhm();
	}
	
	private void playMove(int id)
	{
		Ent.Grid grille = this.ent.getGrille();
		
		Ent.Grid save = new Ent.Grid(grille);
		grille.set(id, Square.valueOf(this.ent.getTurn().toString()));
		if(this.ent.getTurn() == Player.X)
			this.ai.data.coupsX.add(new Ai.Data.Coup(save, new Ent.Grid(grille)));
		else
			this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Ent.Grid(grille)));
		this.incrementTurn();
		this.entToIhm();
	}
	
	private boolean checkOnWin() throws IOException
	{
		Ent.Grid grille = this.ent.getGrille();
		Pair<Player,Row> p = grille.finDePartie();
		
		Player vainqueur = p.first;
		boolean partieTerminee = (vainqueur != null) || grille.isFilled();
		if(partieTerminee)
		{
			if(vainqueur != null)
			{
				System.out.println("Le vainqueur est " + vainqueur.toString());
				this.ihm.getTurn().setTurn(vainqueur);
				Animation animLigne = this.ihm.getGrid().getWinningRowAnim(p.second, 500);
				Animation animCup = this.ihm.getGrid().getCupAnim(vainqueur, 1000);
				this.ihm.setWinningRowAnimOccuring(true);
				animLigne.play();
				animCup.play();
				animCup.setOnFinished(e -> {
					grille.clear();
					this.ent.setTurn(Player.values()[0]);
					this.entToIhm();
					this.ihm.getGrid().clearCanvas();
					this.ihm.setWinningRowAnimOccuring(false);
				});
				TextFile.stringToFile(this.ai.data.getCoups(vainqueur), 
						Ai.DATA_DIRPATH + Ai.MOVES_FILENAME, true);
				this.ai.learn();
			}
			else {				
				System.out.println("Aucun gagnant");
				grille.clear();
				this.ent.setTurn(Player.values()[0]);
				this.entToIhm();
			}
			this.ai.data.reset();
			return true;
		}
		return false;
	}
	
	private int aiPlays()
	{
		Ent.Grid grille = this.ent.getGrille();
		
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
