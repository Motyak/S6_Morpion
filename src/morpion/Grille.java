package morpion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grille {
	
	private Case[] cases;
	
	public Grille(int dimension)
	{
		int taille = dimension * dimension;
		this.cases = new Case[taille];
		for(int i = 0 ; i < taille ; ++i)
			this.cases[i] = Case.VIDE;
	}
	
	public Case at(int index)
	{
		return this.cases[index];
	}
	
	public void set(int index, Case c)
	{
		this.cases[index] = c;
	}
	
	public void clear()
	{
		Arrays.fill(this.cases, Case.VIDE);
		System.out.println("RESET");
	}
	
	public boolean is_filled()
	{
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			if(this.cases[i] == Case.VIDE)
				return false;
		return true;
	}
	
	public void afficher()
	{	
		int i = 0;
		int j = 0;
		System.out.println("--------------------");
		for(; i < Ent.DIM_GRILLE ; ++i)
		{
			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE - 1 ; ++j)
			{
				System.out.print(this.cases[j] + "\t");
			}
			System.out.print(this.cases[j] + "\n\n");
		}
		System.out.println("--------------------");
	}
	
	public Joueur finDePartie()
	{
		Joueur j = this.checkLines();
		if(j == null)
			j = this.checkColumns();
		if(j == null)
			j = this.checkDiags();
		return j;
	}
	
	private int sumOfRange(List<Case> cases)
	{
		int sum = 0;
		for(Case c : cases)
			sum += c.getValue();
		
		return sum;
	}
	
	private Joueur checkLines()
	{
		List<Case> line = new ArrayList<>();
		
		int i = 0;
		int j;
	
		for(; i < Ent.DIM_GRILLE ; ++i)
		{
			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE ; ++j)
				line.add(this.cases[j]);
			int sum = this.sumOfRange(line);
			if(sum == Ent.DIM_GRILLE * Case.X.getValue())
				return Joueur.X;
			else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
				return Joueur.O;
			line.clear();
		}
		return null;
	}
	
	private Joueur checkColumns()
	{
		List<Case> column = new ArrayList<>();
		
		int i = 0;
		int j;
	
		for(; i < Ent.DIM_GRILLE ; ++i)	//+0, +1, +2
		{
			for(j = i ; j <= 2 * Ent.DIM_GRILLE + i ; j+=Ent.DIM_GRILLE)
				column.add(this.cases[j]);
			int sum = this.sumOfRange(column);
			if(sum == Ent.DIM_GRILLE * Case.X.getValue())
				return Joueur.X;
			else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
				return Joueur.O;
			column.clear();
		}
		return null;
	}
	
	private Joueur checkDiags()
	{
		List<Case> diag = new ArrayList<>();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; i += 4)
			diag.add(this.cases[i]);
		int sum = this.sumOfRange(diag);
		if(sum == Ent.DIM_GRILLE * Case.X.getValue())
			return Joueur.X;
		else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
			return Joueur.O;
		diag.clear();
		for(int i = 2 ; i <= 2 * Ent.DIM_GRILLE ; i += 2)
			diag.add(this.cases[i]);
		sum = this.sumOfRange(diag);
		if(sum == Ent.DIM_GRILLE * Case.X.getValue())
			return Joueur.X;
		else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
			return Joueur.O;
		
		return null;
	}

}
