package morpion;

public class Ent {
	 public static final int TAILLE_GRILLE = 9;
	 
	private Case[] grille;
	private Joueur tourJeu;
	
	Ent() {
		this.grille = new Case[Ent.TAILLE_GRILLE];
		for(Case c : grille)
			c = Case.VIDE;
		
		this.tourJeu = Joueur.X;
	}

	public Case[] getGrille() {return grille;}
	
	public void setGrille(Case[] grille) {this.grille = grille;}
	
	public Joueur getTourJeu() {return tourJeu;}
	
	public void setTourJeu(Joueur tourJeu) {this.tourJeu = tourJeu;}
}


