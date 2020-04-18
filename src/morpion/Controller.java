package morpion;

import Mk.Pair;
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
		Grille grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.ihm.writeCase(i, grille.at(i));
		this.ihm.setTourDeJeu(this.ent.getTourJeu());
	}
	
	public void proposerCoup(int id) throws IOException 
	{
		Grille grille = this.ent.getGrille();
		
		if(grille.at(id) == Case.VIDE)
		{
			this.jouerCoup(id);
			
			if(this.verifierFinDePartie())
				return;
			
			if(this.ent.getMode() == Mode.P_VS_AI)
			{
				this.jouerCoup(this.aiPlays());
				this.verifierFinDePartie();
			}
		}	
	}
	
	public void lancerApprentissage() 
	{
		Main.learningThread = new Thread(new Apprentissage_Task<>(this.ai));
		Main.learningThread.setDaemon(true);
		Main.learningThread.start();
	}
	
	private void jouerCoup(int id)
	{
		Grille grille = this.ent.getGrille();
		
		Grille save = new Grille(grille);
		grille.set(id, Case.valueOf(this.ent.getTourJeu().toString()));
		if(this.ent.getTourJeu() == Joueur.X)
			this.ai.data.coupsX.add(new Ai.Data.Coup(save, new Grille(grille)));
		else
			this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Grille(grille)));
		this.incrementerTourDeJeu();
		this.entToIhm();
	}
	
	private boolean verifierFinDePartie() throws IOException
	{
		Grille grille = this.ent.getGrille();
		
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
		Grille grille = this.ent.getGrille();
		
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
	
	private double[] grilleToDoubles(Grille grille)
	{
		double[] res = new double[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.DIM_GRILLE ; ++i)
			for(int j = 0 ; j < Ent.DIM_GRILLE ; ++j)
				res[Ent.DIM_GRILLE * i + j] = grille.at(i, j).getValue();
		
		return res;
	}
}
