package morpion;

enum Square {
	EMPTY(0) {
		@Override
		public String toString() {
			return "";
		}
	},
	X(-1),
	O(1);
	
	
	private final int value;
	private Square(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
}
