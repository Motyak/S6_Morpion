package morpion;

import java.util.Arrays;

public class Controller {
	private Ihm ihm;
	private Ent ent;
	
	Controller(Ihm ihm, Ent ent) {
		this.ihm = ihm;
		this.ent = ent;
	}
	
	public void entToIhm() {
		Case[] grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.ihm.writeCase(i, grille[i]);
		this.ihm.setTourDeJeu(this.ent.getTourJeu());
	}
	
//	public void ihmToEnt() {
//		
//	}
	
	public void proposerCoup(int id) {
		Case c = this.ent.getGrille()[id];
		if(c == Case.VIDE)
		{
			this.ent.getGrille()[id] = Case.valueOf(this.ent.getTourJeu().toString());
			this.incrementerTourDeJeu();
			this.entToIhm();
			this.afficherGrille();	//debug
			if(this.grilleComplete())
			{
				this.clearGrille();
				this.ent.setTourJeu(Joueur.values()[0]);
				this.entToIhm();
			}
		}
	}
	
	private void incrementerTourDeJeu()
	{
		Joueur j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
	}
	
	public void clearGrille()
	{
		Case[] grille = this.ent.getGrille();
		
		Arrays.fill(grille, Case.VIDE);
		
		System.out.println("RESET");
	}
	
	public boolean grilleComplete()
	{
		Case[] grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
		{
			if(grille[i] == Case.VIDE)
				return false;
		}
		return true;
	}
	
	private Joueur finDePartie()
	{
		;
		return null;
	}
	
	//debug
	public void afficherGrille()
	{
		Case[] grille = this.ent.getGrille();
		
		int i = 0;
		int j = 0;
		System.out.println("--------------------");
		for(; i < Ent.DIM_GRILLE ; ++i)
		{
			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE - 1 ; ++j)
			{
				System.out.print(grille[j] + "\t");
			}
			System.out.print(grille[j] + "\n\n");
		}
		System.out.println("--------------------");
	}
}
