package morpion;

enum Difficulte {
	FACILE(1),
	NORMAL(2),
	DIFFICILE(3);
	
	private final int value;
	private Difficulte(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
}
