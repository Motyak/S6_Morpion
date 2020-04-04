package morpion;

import java.util.Arrays;

public class Ent {
	public static final int DIM_GRILLE = 3;
	public static final int TAILLE_GRILLE = Ent.DIM_GRILLE * Ent.DIM_GRILLE;
	 
	 
	private Case[] grille;
	private Joueur tourJeu;
	
	Ent() {
		this.grille = new Case[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.grille[i] = Case.VIDE;
		
		this.tourJeu = Joueur.values()[0];
	}

	public Case[] getGrille() {return grille;}
	
	public void setGrille(Case[] grille) {this.grille = grille;}
	
	public Joueur getTourJeu() {return tourJeu;}
	
	public void setTourJeu(Joueur tourJeu) {this.tourJeu = tourJeu;}
	
	public void afficherGrille()
	{
		int i = 0;
		int j = 0;
		System.out.println("--------------------");
		for(; i < Ent.DIM_GRILLE ; ++i)
		{
			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE - 1 ; ++j)
			{
				System.out.print(this.grille[j] + "\t");
			}
			System.out.print(this.grille[j] + "\n\n");
		}
		System.out.println("--------------------");
	}
	
	public void clearGrille()
	{
		Arrays.fill(this.grille, Case.VIDE);
	}
}


