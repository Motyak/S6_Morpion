package morpion;

/**
 * Represent a row in a grid
 * @author Tommy 'Motyak'
 *
 */
enum Row {
	HORIZONTAL_1(10),
	HORIZONTAL_2(11),
	HORIZONTAL_3(12),
	VERTICAL_1(20),
	VERTICAL_2(21),
	VERTICAL_3(22),
	DIAGONAL_1(30),
	DIAGONAL_2(31);
	
	private final int value;
	
	private Row(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
	
	public static Row get(int value) {
		for(Row j : Row.values())
			if(j.getValue() == value)
				return j;
		return null;
	}
}
