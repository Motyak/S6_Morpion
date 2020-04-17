package morpion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Mk.Math;

class Grille {
	
	private Case[][] cases;
	private int dim;
	
//	public static void main(String[] args) {
//		Grille grille = new Grille();
//		
//		grille.set(0, 0, Case.O);
//		grille.set(0, 1, Case.X);
//		grille.set(0, 2, Case.VIDE);
//		grille.set(1, 0, Case.X);
//		grille.set(1, 1, Case.O);
//		grille.set(1, 2, Case.VIDE);
//		grille.set(2, 0, Case.X);
//		grille.set(2, 1, Case.VIDE);
//		grille.set(2, 2, Case.O);
//		
//		grille.afficher();
//		
//		Joueur vainqueur = grille.finDePartie();
//		if(vainqueur == null)
//			System.out.println("pas de vainqueur");
//		else
//			System.out.println("vainqueur : " + vainqueur);
//
//		
//	}
	
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
	
	public void set(int i, int j, Case c) { this.cases[i][j] = c; }
	
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
		System.out.println(sumsRanges);
		int bestRange = sumsRanges.get(0);
		return Joueur.get(bestRange / this.dim);
	}
	
//	public Joueur finDePartie()
//	{
//		Joueur j = this.checkLines();
//		if(j == null)
//			j = this.checkColumns();
//		if(j == null)
//			j = this.checkDiags();
//		return j;
//	}
//	
//	private int sumOfRange(List<Case> cases)
//	{
//		int sum = 0;
//		for(Case c : cases)
//			sum += c.getValue();
//		
//		return sum;
//	}
//	
//	private Joueur checkLines()
//	{
//		List<Case> line = new ArrayList<>();
//		
//		int i = 0;
//		int j;
//	
//		for(; i < Ent.DIM_GRILLE ; ++i)
//		{
//			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE ; ++j)
//				line.add(this.cases[j]);
//			int sum = this.sumOfRange(line);
//			if(sum == Ent.DIM_GRILLE * Case.X.getValue())
//				return Joueur.X;
//			else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
//				return Joueur.O;
//			line.clear();
//		}
//		return null;
//	}
//	
//	private Joueur checkColumns()
//	{
//		List<Case> column = new ArrayList<>();
//		
//		int i = 0;
//		int j;
//	
//		for(; i < Ent.DIM_GRILLE ; ++i)	//+0, +1, +2
//		{
//			for(j = i ; j <= 2 * Ent.DIM_GRILLE + i ; j+=Ent.DIM_GRILLE)
//				column.add(this.cases[j]);
//			int sum = this.sumOfRange(column);
//			if(sum == Ent.DIM_GRILLE * Case.X.getValue())
//				return Joueur.X;
//			else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
//				return Joueur.O;
//			column.clear();
//		}
//		return null;
//	}
//	
//	private Joueur checkDiags()
//	{
//		List<Case> diag = new ArrayList<>();
//		
//		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; i += 4)
//			diag.add(this.cases[i]);
//		int sum = this.sumOfRange(diag);
//		if(sum == Ent.DIM_GRILLE * Case.X.getValue())
//			return Joueur.X;
//		else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
//			return Joueur.O;
//		diag.clear();
//		for(int i = 2 ; i <= 2 * Ent.DIM_GRILLE ; i += 2)
//			diag.add(this.cases[i]);
//		sum = this.sumOfRange(diag);
//		if(sum == Ent.DIM_GRILLE * Case.X.getValue())
//			return Joueur.X;
//		else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
//			return Joueur.O;
//		
//		return null;
//	}

}
