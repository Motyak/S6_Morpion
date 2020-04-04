package morpion;

enum Case {
	X,
	O,
	VIDE {
		@Override
		public String toString() {
			return "";
		}
	}
}
