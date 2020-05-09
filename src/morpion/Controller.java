package morpion;

import Mk.Pair;
import Mk.TextFile;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;

import java.io.IOException;

class Controller {
	private Ihm ihm;
	private Ent ent;
	
	private Ai ai;

	Controller(Ihm ihm, Ent ent) throws Exception 
	{
		this.ihm = ihm;
		this.ent = ent;
		this.ai = new Ai(this.ent.getDiff());
	}
	
	public void entToIhm() 
	{
		Ent.Grille grille = this.ent.getGrille();
		Ihm.Grid grilleIhm = this.ihm.getGrid();
		
		for(int i = 0 ; i < Ent.GRID_SIZE ; ++i)
			grilleIhm.writeCase(i, grille.at(i));
		this.ihm.getTurn().setTurn(this.ent.getTourJeu());
		
		this.ihm.getMenu().setModeJeu(this.ent.getMode());
		this.ihm.getMenu().lockDiff((this.ent.getMode() == Mode.P_VS_P));
	}
	
	public boolean proposerCoup(int id) throws IOException
	{
		Ent.Grille grille = this.ent.getGrille();
		
		if(grille.at(id) == Square.VIDE)
		{
			this.jouerCoup(id);
			if(this.verifierFinDePartie())
				return true;
			
			if(this.ent.getMode() == Mode.P_VS_AI)
			{
				this.jouerCoup(this.aiPlays());
				this.verifierFinDePartie();
			}
			return true;
		}
		return false;
	}
	
	public static void launchLearning(Ai ai) throws IOException 
	{
//		this.ai.reset();
		if(Main.learningThread != null) {
			Main.learningThread.interrupt();
			while(Main.learningThread.isAlive())
				;
		}
		
		Main.learningThread = new Thread(new LearningTask<>(ai));
		Main.learningThread.setDaemon(true);
		Main.learningThread.start();
	}
	
	public static void launchConfiguring(Ai ai)
	{
		if(Main.configThread != null) {
			Main.configThread.interrupt();
			while(Main.configThread.isAlive())
				;
		}
		
		Main.configThread = new Thread(new ConfiguringTask<>(ai));
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
	
	public void changerModeJeu(Mode mode)
	{
		if(this.ent.getMode() == mode)
			return;
		
		this.ent.setMode(mode);
		this.renewGame();
		System.out.println("Mode de jeu actuel : " + this.ent.getMode());
	}
	
	public void changerDiff(Difficulty diff) throws Exception
	{
		if(this.ent.getDiff() == diff)
			return;
		
		this.ent.setDiff(diff);
		this.ai.changeDiff(diff);
		this.renewGame();
		
		Controller.launchLearning(this.ai);
		
		System.out.println("Difficulte actuelle : " + this.ent.getDiff().getValue());
	}
	
	public void showDialogRegles()
	{
		Main.rulesDialog.showAndWait();
	}
	
	public Ai getAi() { return this.ai; }
	
	public Player getJoueurCourant()
	{
		return this.ent.getTourJeu();
	}
	
	public Mode getModeJeu()
	{
		return this.ent.getMode();
	}
	
	public boolean caseVide(int id)
	{
		return this.ent.getGrille().at(id) == Square.VIDE;
	}
	
	public void renewGame()
	{
		this.ent.getGrille().clear();
		this.ai.data.reset();
		this.ent.setTourJeu(Player.values()[0]);
		this.entToIhm();
	}
	
	private void jouerCoup(int id)
	{
		Ent.Grille grille = this.ent.getGrille();
		
		Ent.Grille save = new Ent.Grille(grille);
		grille.set(id, Square.valueOf(this.ent.getTourJeu().toString()));
		if(this.ent.getTourJeu() == Player.X)
			this.ai.data.coupsX.add(new Ai.Data.Coup(save, new Ent.Grille(grille)));
		else
			this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Ent.Grille(grille)));
		this.incrementerTourDeJeu();//
		this.entToIhm();
	}
	
	private boolean verifierFinDePartie() throws IOException
	{
		Ent.Grille grille = this.ent.getGrille();
		Pair<Player,Row> p = grille.finDePartie();
		
		Player vainqueur = p.first;
		boolean partieTerminee = (vainqueur != null) || grille.is_filled();
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
					this.ent.setTourJeu(Player.values()[0]);
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
				this.ent.setTourJeu(Player.values()[0]);
				this.entToIhm();
			}
			this.ai.data.reset();
			return true;
		}
		return false;
	}
	
	private int aiPlays()
	{
		Ent.Grille grille = this.ent.getGrille();
		
		double[] input = this.grilleToDoubles(grille);
		int[] output = this.ai.genOutput(input);
		int i = 0;
		while(grille.at(output[i]) != Square.VIDE)
			++i;

		return output[i];
	}
	
	private void incrementerTourDeJeu()
	{
		Player j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
	}
	
	private double[] grilleToDoubles(Ent.Grille grille)
	{
		double[] res = new double[Ent.GRID_SIZE];
		for(int i = 0 ; i < Ent.GRID_DIM ; ++i)
			for(int j = 0 ; j < Ent.GRID_DIM ; ++j)
				res[Ent.GRID_DIM * i + j] = grille.at(i, j).getValue();
		
		return res;
	}
}
