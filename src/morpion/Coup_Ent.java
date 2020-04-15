package morpion;

public class Coup_Ent {
	public Grille avant;
	public Grille apres;
	
	public Coup_Ent(Grille avant, Grille apres)
	{
		this.avant = avant;
		this.apres = apres;
	}
	
	public int getNumCaseJouee()
	{
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			if(avant.at(i) != apres.at(i))
				return i;
		return -1;
	}
	
	@Override
	public String toString() {
		int numCase = this.getNumCaseJouee();
		int i = 0;
		int j = 0;
		String res = "";
		for(; i < Ent.DIM_GRILLE ; ++i)
		{
			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE - 1 ; ++j)
			{
				if(j == numCase)
					res += "[" + this.apres.at(j) + "]\t";
				else
					res += this.apres.at(j) + "\t";
			}
			if(j == numCase)
				res += "[" + this.apres.at(j) + "]\n\n";
			else
				res += this.apres.at(j) + "\n\n";
		}
		return res;
	}
	
//	public static void main(String[] args)
//	{
//		Grille g1 = new Grille();
//		g1.set(0, Case.X);
//		g1.set(4, Case.X);
//		g1.set(7, Case.X);
//		g1.set(1, Case.O);
//		g1.set(3, Case.O);
//		Grille g2 = new Grille(g1);
//		g2.set(8, Case.O);
//		Coup_Ent coup = new  Coup_Ent(g1, g2);
//		System.out.println(coup);
//	}
}
