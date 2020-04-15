package morpion;

import java.util.ArrayList;
import java.util.List;

public class Partie_Ent {
	public List<Coup_Ent> coupsX;
	public List<Coup_Ent> coupsY; 
	
	public Partie_Ent()
	{
		this.coupsX = new ArrayList<>();
		this.coupsY = new ArrayList<>();
	}
	
//	public void add(Joueur j, Coup_Ent coup)
//	{
//		this.coups.add(coup);
//	}
//	
//	public List<Coup_Ent> getCoups()
//	{
//		return this.coups;
//	}
//	
	public void reset()
	{
		this.coupsX.clear();
		this.coupsY.clear();
	}
	
	@Override
	public String toString() {
		String res = "";
		for(int i = 0 ; i < this.coupsX.size() ; ++i)
		{
			res += this.coupsX.get(i).toString();
			if(i < this.coupsY.size())
				res += this.coupsY.get(i).toString();
			res += "--------------------\n";
		}
			
		return res;
	}

	public void afficherCoups(Joueur j)
	{
		List<Coup_Ent> coups = null;
		if(j == Joueur.X)
			coups = this.coupsX;
		else if(j == Joueur.O)
			coups = this.coupsY;
		
		for(int i = 0 ; i < coups.size() ; ++i)
		{
			System.out.println(coups.get(i));
			System.out.println("--------------------\n");
		}
	}
	
//	public static void main(String[] args) {
//		Partie_Ent p = new Partie_Ent();
//		Grille g1 = new Grille();
//		Grille g2 = new Grille();
//		
//		g2.set(2, Case.X);
//		Grille g3 = new Grille(g2);
//		g3.set(6, Case.O);
//		p.coupsX.add(new Coup_Ent(g1, g2));
//		p.coupsX.add(new Coup_Ent(g2, g3));
//		Coup_Ent c = new Coup_Ent(g2, g3);
//		System.out.println(p);
//	}
	
}
