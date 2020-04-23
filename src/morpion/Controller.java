package morpion;

import Mk.TextFile;

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
	
	public void lancerApprentissage() throws IOException 
	{
//		this.ai.reset();
		if(Main.learningThread != null) {
			Main.learningThread.interrupt();
			while(Main.learningThread.isAlive())
				;
		}
		
		Main.learningThread = new Thread(new Apprentissage_Task<>(this.ai));
		Main.learningThread.setDaemon(true);
		Main.learningThread.start();
	}
	
	public void editConfigFile() throws IOException
	{
		this.ai.editConfigFile();
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
		
		this.lancerApprentissage();
		
		System.out.println("Difficulte actuelle : " + this.ent.getDiff().getValue());
	}
	
	public void showDialogRegles()
	{
		Main.dialogRegles.showAndWait();
	}
	
	public Joueur getJoueurCourant()
	{
		return this.ent.getTourJeu();
	}
	
	public boolean caseVide(int id)
	{
		return this.ent.getGrille().at(id) == Case.VIDE;
	}
	
	private void renewGame()
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
		this.incrementerTourDeJeu();
		this.entToIhm();
	}
	
	private boolean verifierFinDePartie() throws IOException
	{
		Ent.Grille grille = this.ent.getGrille();
		
		Joueur vainqueur = grille.finDePartie();
		boolean partieTerminee = (vainqueur != null) || grille.is_filled();
		if(partieTerminee)
		{
			if(vainqueur != null)
			{
				System.out.println("Le vainqueur est " + vainqueur.toString());
				TextFile.stringToFile(this.ai.data.getCoups(vainqueur), 
						Ai.DATA_DIRPATH + Ai.COUPS_FILENAME, true);
				this.ai.learn();
			}
			else
				System.out.println("Aucun gagnant");
			
			grille.clear();
			this.ai.data.reset();
			this.ent.setTourJeu(Joueur.values()[0]);
			this.entToIhm();
			
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
