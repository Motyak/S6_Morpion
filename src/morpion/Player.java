package morpion;

enum Player {
	X(-1),
	O(1) {
		@Override
		public Player next() {
			return Player.values()[0];
		}
	};
	
	private final int value;
	
	public Player next() {
		return values()[ordinal() + 1];
	}
	
	public static Player get(int value) {
		for(Player j : Player.values())
			if(j.getValue() == value)
				return j;
		return null;
	}
	
	private Player(int value) {
		this.value = value;
	}
	
	public int getValue() { return this.value; }
}
