package morpion;

enum Case {
	VIDE(0) {
		@Override
		public String toString() {
			return "";
		}
	},
	X(3),
	O(5);
	
	
	private final int value;
	private Case(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
}
