package morpion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Mk.Math;

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
	
	static class Grille {
		
		private Case[][] cases;
		private int dim;
		
		public Grille()
		{
			this.dim = Ent.DIM_GRILLE;
			this.cases = new Case[this.dim][this.dim];
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					this.cases[i][j] = Case.VIDE;
		}
		
		public Grille(Grille grille)
		{
			this.dim = grille.getDim();
			this.cases = new Case[this.dim][this.dim];
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					this.cases[i][j] = grille.at(i, j);
		}
		
		public int getDim() { return this.dim; }
		
		public Case at(int i, int j) { return this.cases[i][j]; }
		
		public Case at(int id) { return this.cases[id / this.dim][id % this.dim]; }
		
		public void set(int i, int j, Case c) { this.cases[i][j] = c; }
		
		public void set(int id, Case c) { this.cases[id / this.dim][id % this.dim] = c; }
		
		public void clear()
		{
			for(int i = 0 ; i < this.dim ; ++i)
				Arrays.fill(this.cases[i], Case.VIDE);
		}
		
		public boolean is_filled()
		{
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					if(this.cases[i][j] == Case.VIDE)
						return false;
			return true;
		}
		
		public void afficher()
		{	
			int j;
			System.out.println("--------------------");
			for(int i = 0 ; i < this.dim ; ++i)
			{
				for(j = 0 ; j < this.dim - 1 ; ++j)
					System.out.print(this.cases[i][j] + "\t");
				System.out.print(this.cases[i][j] + "\n\n");
			}
			System.out.println("--------------------");
		}

		public Joueur finDePartie() 
		{
			List<Integer> sumsRanges = new ArrayList<>();
			for(int i = 0 ; i < this.dim ; ++i) 
			{
				List<Integer> sumLineCol = Arrays.asList(0, 0);
				for(int j = 0 ; j < this.dim ; ++j) 
				{
					sumLineCol.set(0, sumLineCol.get(0) + this.cases[i][j].getValue());
					sumLineCol.set(1, sumLineCol.get(1) + this.cases[j][i].getValue());
				}
				sumsRanges.addAll(sumLineCol);
			}
			List<Integer> sumDiags = Arrays.asList(0, 0);
			for(int i = 0 ; i < this.dim ; ++i)
			{
				sumDiags.set(0, sumDiags.get(0) + this.cases[i][i].getValue());
				sumDiags.set(1, sumDiags.get(1) + this.cases[i][this.dim - 1 - i].getValue());
			}
			sumsRanges.addAll(sumDiags);
			sumsRanges.sort((Integer i1, Integer i2) -> Math.square(i2).compareTo(Math.square(i1)));
			int bestRange = sumsRanges.get(0);
			return Joueur.get(bestRange / this.dim);
		}
		
		@Override
		public String toString() {
			StringBuilder res = new StringBuilder("");
			int j;
			for(int i = 0 ; i < this.dim ; ++i)
				for(j = 0 ; j < this.dim ; ++j)
					res.append(this.cases[i][j].getValue() + ",");
			res.deleteCharAt(res.length() - 1);
			return res.toString();
		}
	}
}


