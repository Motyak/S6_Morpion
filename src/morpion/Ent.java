package morpion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Mk.Math;
import Mk.Pair;

public class Ent {
	public static final int GRID_DIM = 3;
	public static final int GRID_SIZE = Ent.GRID_DIM * Ent.GRID_DIM; 
	
	private Grid grid;
	private Player turn;
	private Mode mode;
	private Difficulty diff;
	
	Ent() {
		this.grid = new Grid();
		this.turn = Player.values()[0];
		this.mode = Mode.P_VS_AI;
		this.setDiff(Difficulty.NORMAL);
	}

	public Grid getGrille() {return grid;}
	
	public void setGrille(Grid grid) {this.grid = grid;}
	
	public Player getTurn() {return this.turn;}
	
	public void setTurn(Player turn) {this.turn = turn;}

	public Mode getMode() { return mode; }

	public void setMode(Mode mode) { this.mode = mode; }

	public Difficulty getDiff() { return diff; }

	public void setDiff(Difficulty diff) { this.diff = diff; }
	
	static class Grid {
		
		private Square[][] squares;
		private int dim;
		
		public Grid()
		{
			this.dim = Ent.GRID_DIM;
			this.squares = new Square[this.dim][this.dim];
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					this.squares[i][j] = Square.EMPTY;
		}
		
		public Grid(Grid grid)
		{
			this.dim = grid.getDim();
			this.squares = new Square[this.dim][this.dim];
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					this.squares[i][j] = grid.at(i, j);
		}
		
		public int getDim() { return this.dim; }
		
		public Square at(int i, int j) { return this.squares[i][j]; }
		
		public Square at(int id) { return this.squares[id / this.dim][id % this.dim]; }
		
		public void set(int i, int j, Square s) { this.squares[i][j] = s; }
		
		public void set(int id, Square s) { this.squares[id / this.dim][id % this.dim] = s; }
		
		public void clear()
		{
			for(int i = 0 ; i < this.dim ; ++i)
				Arrays.fill(this.squares[i], Square.EMPTY);
		}
		
		public boolean isFilled()
		{
			for(int i = 0 ; i < this.dim ; ++i)
				for(int j = 0 ; j < this.dim ; ++j)
					if(this.squares[i][j] == Square.EMPTY)
						return false;
			return true;
		}
		
		public void print()
		{	
			int j;
			System.out.println("--------------------");
			for(int i = 0 ; i < this.dim ; ++i)
			{
				for(j = 0 ; j < this.dim - 1 ; ++j)
					System.out.print(this.squares[i][j] + "\t");
				System.out.print(this.squares[i][j] + "\n\n");
			}
			System.out.println("--------------------");
		}

		public Pair<Player,Row> finDePartie() 
		{
			List<Pair<Integer,Row>> sumsRows = new ArrayList<>();
			for(int i = 0 ; i < this.dim ; ++i) 
			{
				List<Pair<Integer,Row>> sumLineCol = Arrays.asList(new Pair<>(0, null), new Pair<>(0, null));
				for(int j = 0 ; j < this.dim ; ++j) 
				{
					sumLineCol.set(0, new Pair<>(sumLineCol.get(0).first + this.squares[i][j].getValue(), Row.get(10 + i)));
					sumLineCol.set(1, new Pair<>(sumLineCol.get(1).first + this.squares[j][i].getValue(), Row.get(20 + i)));
				}
				sumsRows.addAll(sumLineCol);
			}
			List<Pair<Integer,Row>> sumDiags = Arrays.asList(new Pair<>(0, null), new Pair<>(0, null));
			for(int i = 0 ; i < this.dim ; ++i)
			{
				sumDiags.set(0, new Pair<>(sumDiags.get(0).first + this.squares[i][i].getValue(), Row.DIAGONAL_1));
				sumDiags.set(1, new Pair<>(sumDiags.get(1).first + this.squares[i][this.dim - 1 - i].getValue(), Row.DIAGONAL_2));
			}
			sumsRows.addAll(sumDiags);
			sumsRows.sort((Pair<Integer,Row> i1, Pair<Integer,Row> i2) -> Math.square(i2.first).compareTo(Math.square(i1.first)));
			
			int bestRange = sumsRows.get(0).first;
			Player winner = Player.get(bestRange / this.dim);
			Row winningRow = sumsRows.get(0).second;
			
			return new Pair<>(winner, winningRow);
		}
		
		@Override
		public String toString() {
			StringBuilder res = new StringBuilder("");
			int j;
			for(int i = 0 ; i < this.dim ; ++i)
				for(j = 0 ; j < this.dim ; ++j)
					res.append(this.squares[i][j].getValue() + ",");
			res.deleteCharAt(res.length() - 1);
			return res.toString();
		}
	}
}


