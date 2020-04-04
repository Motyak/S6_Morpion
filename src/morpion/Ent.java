package morpion;

public class Ent {
	public static final int DIM_GRILLE = 3;
	public static final int TAILLE_GRILLE = Ent.DIM_GRILLE * Ent.DIM_GRILLE; 
	
	private Case[] grille;
	private Joueur tourJeu;
	private Mode mode;
	
	Ent() {
		this.grille = new Case[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.grille[i] = Case.VIDE;
		
		this.tourJeu = Joueur.values()[0];
		this.mode = Mode.P_VS_AI;
	}

	public Case[] getGrille() {return grille;}
	
	public void setGrille(Case[] grille) {this.grille = grille;}
	
	public Joueur getTourJeu() {return tourJeu;}
	
	public void setTourJeu(Joueur tourJeu) {this.tourJeu = tourJeu;}

	public Mode getMode() { return mode; }

	public void setMode(Mode mode) { this.mode = mode; }	
}


