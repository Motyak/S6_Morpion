package morpion;

enum Difficulty {
	EASY("Easy"),
	NORMAL("Normal"),
	HARD("Hard");
	
	private final String value;
	private Difficulty(String value) {
		this.value = value;
	}
	
	public String getValue() { return this.value; }
}
