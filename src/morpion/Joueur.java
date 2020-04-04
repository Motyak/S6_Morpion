package morpion;

enum Joueur {
	X,
	O {
		@Override
		public Joueur next() {
			return Joueur.X;
		}
	};
	
	public Joueur next() {
		return values()[ordinal() + 1];
	}
}
