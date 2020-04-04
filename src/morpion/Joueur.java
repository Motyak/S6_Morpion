package morpion;

enum Joueur {
	X,
	O {
		@Override
		public Joueur next() {
			return Joueur.values()[0];
		}
	};
	
	public Joueur next() {
		return values()[ordinal() + 1];
	}
}
