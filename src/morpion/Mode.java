package morpion;

/**
 * Represent the game modes
 * @author Tommy 'Motyak'
 *
 */
enum Mode {
	P_VS_AI(0),
	P_VS_P(1);
	
	private final int value;
	
	private Mode(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
	
	public static Mode get(int value) {
		for(Mode j : Mode.values())
			if(j.getValue() == value)
				return j;
		return null;
	}
}
