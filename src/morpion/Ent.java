package morpion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Mk.Math;
import Mk.Pair;

public class Ent {
	public static final int GRID_DIM = 3;
	public static final int GRID_SIZE = Ent.GRID_DIM * Ent.GRID_DIM; 
	
	private Grille grille;
	private Player tourJeu;
	private Mode mode;
	private Difficulty diff;
	
	Ent() {
		this.grille = new Grille();
		this.tourJeu = Player.values()[0];
		this.mode = Mode.P_VS_AI;
		this.setDiff(Difficulty.NORMAL);
	}

	public Grille getGrille() {return grille;}
	
	public void setGrille(Grille grille) {this.grille = grille;}
	
	public Player getTourJeu() {return tourJeu;}
	
	public void setTourJeu(Player tourJeu) {this.tourJeu = tourJeu;}

	public Mode getMode() { return mode; }

	public void setMode(Mode mode) { this.mode = mode; }

	public Difficulty getDiff() { return diff; }

	public void setDiff(Difficulty diff) { this.diff = diff; }
	
	static class Grille {
		
		private Square[][] cases;
		private int dim;
		
		public Grille()
		{
			this.dim = Ent.GRID_DIM;
			this.cases = new Square[this.dim][this.dim];
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					this.cases[i][j] = Square.VIDE;
		}
		
		public Grille(Grille grille)
		{
			this.dim = grille.getDim();
			this.cases = new Square[this.dim][this.dim];
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					this.cases[i][j] = grille.at(i, j);
		}
		
		public int getDim() { return this.dim; }
		
		public Square at(int i, int j) { return this.cases[i][j]; }
		
		public Square at(int id) { return this.cases[id / this.dim][id % this.dim]; }
		
		public void set(int i, int j, Square c) { this.cases[i][j] = c; }
		
		public void set(int id, Square c) { this.cases[id / this.dim][id % this.dim] = c; }
		
		public void clear()
		{
			for(int i = 0 ; i < this.dim ; ++i)
				Arrays.fill(this.cases[i], Square.VIDE);
		}
		
		public boolean is_filled()
		{
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					if(this.cases[i][j] == Square.VIDE)
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

		public Pair<Player,Row> finDePartie() 
		{
			List<Pair<Integer,Row>> sumsRanges = new ArrayList<>();
			for(int i = 0 ; i < this.dim ; ++i) 
			{
				List<Pair<Integer,Row>> sumLineCol = Arrays.asList(new Pair<>(0, null), new Pair<>(0, null));
				for(int j = 0 ; j < this.dim ; ++j) 
				{
					sumLineCol.set(0, new Pair<>(sumLineCol.get(0).first + this.cases[i][j].getValue(), Row.get(10 + i)));
					sumLineCol.set(1, new Pair<>(sumLineCol.get(1).first + this.cases[j][i].getValue(), Row.get(20 + i)));
				}
				sumsRanges.addAll(sumLineCol);
			}
			List<Pair<Integer,Row>> sumDiags = Arrays.asList(new Pair<>(0, null), new Pair<>(0, null));
			for(int i = 0 ; i < this.dim ; ++i)
			{
				sumDiags.set(0, new Pair<>(sumDiags.get(0).first + this.cases[i][i].getValue(), Row.DIAGONAL_1));
				sumDiags.set(1, new Pair<>(sumDiags.get(1).first + this.cases[i][this.dim - 1 - i].getValue(), Row.DIAGONAL_2));
			}
			sumsRanges.addAll(sumDiags);
			sumsRanges.sort((Pair<Integer,Row> i1, Pair<Integer,Row> i2) -> Math.square(i2.first).compareTo(Math.square(i1.first)));
			
			int bestRange = sumsRanges.get(0).first;
			Player vainqueur = Player.get(bestRange / this.dim);
			Row ligneGagnante = sumsRanges.get(0).second;
			
			return new Pair<>(vainqueur, ligneGagnante);
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


