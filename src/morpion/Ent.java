package morpion;

public class Ent {
	public static final int DIM_GRILLE = 3;
	public static final int TAILLE_GRILLE = Ent.DIM_GRILLE * Ent.DIM_GRILLE; 
	
	private Grille grille;
	private Joueur tourJeu;
	private Mode mode;
	private Difficulte diff;
	
	Ent() {
		this.grille = new Grille();
		this.tourJeu = Joueur.values()[0];
		this.mode = Mode.P_VS_AI;
		this.setDiff(Difficulte.NORMAL);
	}

	public Grille getGrille() {return grille;}
	
	public void setGrille(Grille grille) {this.grille = grille;}
	
	public Joueur getTourJeu() {return tourJeu;}
	
	public void setTourJeu(Joueur tourJeu) {this.tourJeu = tourJeu;}

	public Mode getMode() { return mode; }

	public void setMode(Mode mode) { this.mode = mode; }

	public Difficulte getDiff() { return diff; }

	public void setDiff(Difficulte diff) { this.diff = diff; }	
}


