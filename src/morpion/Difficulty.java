package morpion;

/**
 * Represent the different level of difficulty in the game
 * @author Tommy 'Motyak'
 *
 */
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
