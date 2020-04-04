package morpion;

public class Controller {
	private Ihm ihm;
	private Ent ent;
	
	Controller(Ihm ihm, Ent ent) {
		this.ihm = ihm;
		this.ent = ent;
	}
	
	//debug
	public void afficherGrille()
	{
		this.ent.afficherGrille();
	}
	
	public void entToIhm() {
		Case[] grille = this.ent.getGrille();
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.ihm.writeCase(i, grille[i]);
		this.ihm.setTourDeJeu(this.ent.getTourJeu());
	}
	
	public void ihmToEnt() {
		
	}
	
	public void writeCase(int id) {
		Case c = this.ent.getGrille()[id];
		if(c == Case.VIDE)
		{
			this.ent.getGrille()[id] = Case.valueOf(this.ent.getTourJeu().toString());
			this.incrementerTourDeJeu();
			this.entToIhm();
			if(this.finDePartie())
			{
				this.ent.clearGrille();
				this.ent.setTourJeu(Joueur.values()[0]);
				this.entToIhm();
			}
		}
	}
	
	private boolean finDePartie()
	{
		Case[] grille = this.ent.getGrille();
		//si la grille est pleine, pour l'instant
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
		{
			if(grille[i] == Case.VIDE)
				return false;
		}
		return true;
	}
	
	private void incrementerTourDeJeu()
	{
		Joueur j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
	}
}
