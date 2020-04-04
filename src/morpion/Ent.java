package morpion;

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
}


