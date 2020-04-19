package morpion;

enum Difficulte {
	FACILE("Facile"),
	NORMAL("Normal"),
	DIFFICILE("Difficile");
	
	private final String value;
	private Difficulte(String value) {
		this.value = value;
	}
	
	public String getValue() { return this.value; }
}
