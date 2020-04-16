package morpion;

import Mk.TextFile;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

public class Controller {
	private Ihm ihm;
	private Ent ent;
	private Ai ai;

	
	Controller(Ihm ihm, Ent ent) throws Exception {
		this.ihm = ihm;
		this.ent = ent;
		this.ai = new Ai(this.ent.getDiff());
	}
	
	public void entToIhm() {
		Grille grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.ihm.writeCase(i, grille.at(i));
		this.ihm.setTourDeJeu(this.ent.getTourJeu());
	}
	
	public void proposerCoup(int id) throws IOException {
		
		Case c = this.ent.getGrille().at(id);
		
		if(c == Case.VIDE)
		{
			Grille save = new Grille(this.ent.getGrille());
			this.ent.getGrille().set(id, Case.valueOf(this.ent.getTourJeu().toString()));
			if(this.ent.getTourJeu() == Joueur.X)
				this.ai.data.coupsX.add(new Ai.Data.Coup(save, new Grille(this.ent.getGrille())));
			else
				this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Grille(this.ent.getGrille())));
			this.incrementerTourDeJeu();
			this.entToIhm();
			Joueur vainqueur = this.ent.getGrille().finDePartie();
			boolean partieTerminee = (vainqueur != null) || this.ent.getGrille().is_filled();
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
				
				
				
				this.ent.getGrille().clear();
				this.ai.data.reset();
				this.ent.setTourJeu(Joueur.values()[0]);
				this.entToIhm();
				return;
			}
			
			
			
			if(this.ent.getMode() == Mode.P_VS_AI)
			{
				save = new Grille(this.ent.getGrille());
				this.aiPlays();
				this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Grille(this.ent.getGrille())));
				this.incrementerTourDeJeu();
				this.entToIhm();
				vainqueur = this.ent.getGrille().finDePartie();
				partieTerminee = (vainqueur != null) || this.ent.getGrille().is_filled();
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
					
					
					
					this.ent.getGrille().clear();
					this.ai.data.reset();
					this.ent.setTourJeu(Joueur.values()[0]);
					this.entToIhm();
				}
			}
		}	
	}
	
	private void aiPlays()
	{
		Grille grille = this.ent.getGrille();
		double[] input = this.grilleToDoubles(grille);
		int[] output = this.sortedOutput(this.ai.model.forwardPropagation(input));
		int i = 0;
		while(grille.at(output[i]) != Case.VIDE)
			++i;
		grille.set(output[i], Case.O);
	}
	
	private void incrementerTourDeJeu()
	{
		Joueur j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
	}
	
	private int[] sortedOutput(double[] output)
	{
		int[] sortedIndexes = new int[output.length];
		TreeMap<Double,Integer> map = new TreeMap<Double,Integer>(Collections.reverseOrder());
		for(int i = 0 ; i < output.length ; ++i)
		    map.put( output[i], i );
		

		Collection<Integer> values = map.values();
		int i = 0;
		for(Integer value : values)
		{
			sortedIndexes[i] = value;
			++i;
		}

		return sortedIndexes;
	}
	
	private double[] grilleToDoubles(Grille grille)
	{
		double[] res = new double[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			res[i] = (double)grille.at(i).getValue();
		return res;
	}
}
