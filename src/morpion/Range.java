package morpion;

enum Range {
	HORIZONTALE_1(10),
	HORIZONTALE_2(11),
	HORIZONTALE_3(12),
	VERTICALE_1(20),
	VERTICALE_2(21),
	VERTICALE_3(22),
	DIAGONALE_1(30),
	DIAGONALE_2(31);
	
	private final int value;
	
	private Range(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
	
	public static Range get(int value) {
		for(Range j : Range.values())
			if(j.getValue() == value)
				return j;
		return null;
	}
}
