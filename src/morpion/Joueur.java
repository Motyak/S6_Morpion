package morpion;

enum Joueur {
	X(-1),
	O(1) {
		@Override
		public Joueur next() {
			return Joueur.values()[0];
		}
	};
	
	private final int value;
	
	public Joueur next() {
		return values()[ordinal() + 1];
	}
	
	public static Joueur get(int value) {
		for(Joueur j : Joueur.values())
			if(j.getValue() == value)
				return j;
		return null;
	}
	
	
	private Joueur(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
}
