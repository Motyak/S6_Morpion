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
		Ihm.Grille grilleIhm = this.ihm.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			grilleIhm.writeCase(i, grille.at(i));
		this.ihm.getTourJeu().setTourDeJeu(this.ent.getTourJeu());
		
		this.ihm.getMenu().setModeJeu(this.ent.getMode());
		this.ihm.getMenu().lockDiff((this.ent.getMode() == Mode.P_VS_P));
	}
	
	public boolean proposerCoup(int id) throws IOException
	{
		Ent.Grille grille = this.ent.getGrille();
		
		if(grille.at(id) == Case.VIDE)
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
	
	public static void lancerApprentissage(Ai ai) throws IOException 
	{
//		this.ai.reset();
		if(Main.learningThread != null) {
			Main.learningThread.interrupt();
			while(Main.learningThread.isAlive())
				;
		}
		
		Main.learningThread = new Thread(new Apprentissage_Task<>(ai));
		Main.learningThread.setDaemon(true);
		Main.learningThread.start();
	}
	
	public static void lancerConfigThread(Ai ai)
	{
		if(Main.configThread != null) {
			Main.configThread.interrupt();
			while(Main.configThread.isAlive())
				;
		}
		
		Main.configThread = new Thread(new Config_Task<>(ai));
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
	
	public void changerDiff(Difficulte diff) throws Exception
	{
		if(this.ent.getDiff() == diff)
			return;
		
		this.ent.setDiff(diff);
		this.ai.changeDiff(diff);
		this.renewGame();
		
		Controller.lancerApprentissage(this.ai);
		
		System.out.println("Difficulte actuelle : " + this.ent.getDiff().getValue());
	}
	
	public void showDialogRegles()
	{
		Main.dialogRegles.showAndWait();
	}
	
	public Ai getAi() { return this.ai; }
	
	public Joueur getJoueurCourant()
	{
		return this.ent.getTourJeu();
	}
	
	public Mode getModeJeu()
	{
		return this.ent.getMode();
	}
	
	public boolean caseVide(int id)
	{
		return this.ent.getGrille().at(id) == Case.VIDE;
	}
	
	public void renewGame()
	{
		this.ent.getGrille().clear();
		this.ai.data.reset();
		this.ent.setTourJeu(Joueur.values()[0]);
		this.entToIhm();
	}
	
	private void jouerCoup(int id)
	{
		Ent.Grille grille = this.ent.getGrille();
		
		Ent.Grille save = new Ent.Grille(grille);
		grille.set(id, Case.valueOf(this.ent.getTourJeu().toString()));
		if(this.ent.getTourJeu() == Joueur.X)
			this.ai.data.coupsX.add(new Ai.Data.Coup(save, new Ent.Grille(grille)));
		else
			this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Ent.Grille(grille)));
		this.incrementerTourDeJeu();//
		this.entToIhm();
	}
	
	private boolean verifierFinDePartie() throws IOException
	{
		Ent.Grille grille = this.ent.getGrille();
		Pair<Joueur,Range> p = grille.finDePartie();
		
		Joueur vainqueur = p.first;
		boolean partieTerminee = (vainqueur != null) || grille.is_filled();
		if(partieTerminee)
		{
			if(vainqueur != null)
			{
				System.out.println("Le vainqueur est " + vainqueur.toString());
				this.ihm.getTourJeu().setTourDeJeu(vainqueur);
				Animation animLigne = this.ihm.getGrille().animLigneGagnante(p.second, 500);
				Animation animCup = this.ihm.getGrille().animCup(vainqueur, 1000);
				animLigne.play();
				animCup.play();
				animCup.setOnFinished(e -> {
					grille.clear();
					this.ent.setTourJeu(Joueur.values()[0]);
					this.entToIhm();
					this.ihm.getGrille().clearCanvas();
				});
				TextFile.stringToFile(this.ai.data.getCoups(vainqueur), 
						Ai.DATA_DIRPATH + Ai.COUPS_FILENAME, true);
				this.ai.learn();
			}
			else {				
				System.out.println("Aucun gagnant");
				grille.clear();
				this.ent.setTourJeu(Joueur.values()[0]);
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
		while(grille.at(output[i]) != Case.VIDE)
			++i;

		return output[i];
	}
	
	private void incrementerTourDeJeu()
	{
		Joueur j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
	}
	
	private double[] grilleToDoubles(Ent.Grille grille)
	{
		double[] res = new double[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.DIM_GRILLE ; ++i)
			for(int j = 0 ; j < Ent.DIM_GRILLE ; ++j)
				res[Ent.DIM_GRILLE * i + j] = grille.at(i, j).getValue();
		
		return res;
	}
}
