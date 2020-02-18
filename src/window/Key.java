package window;

import data.id.ItemTableClient;

public enum Key {
	// Z, Q, S, D, Space, Shift, Ctrl
	FORWARD(90), LEFT(81), BACKWARD(83), RIGHT(68), UP(32), DOWN(16), SPEED_BOOST(17),

	// Esc, F3
	PAUSE(27), DEVLOP(114),

	// =========================================================================================================================
	// Dialog

	// !
	DIALOG(517),

	VALID(10), DEL(8), SUPPR(127), ARROW_LEFT(37), ARROW_UP(38), ARROW_RIGHT(39), ARROW_DOWN(40),

	END(35), START(36), PAGE_UP(33), PAGE_DOWN(34),

	// =========================================================================================================================

	// 1-9
	ACCESS_1(49), ACCESS_2(50), ACCESS_3(51), ACCESS_4(52), ACCESS_5(53), ACCESS_6(54), ACCESS_7(55), ACCESS_8(
			56), ACCESS_9(57),

	// Tabulation
	KEY_TAB(9);

	// =========================================================================================================================

	public int code;

	Key(int code) {
		this.code = code;
	}

	public static Key get(int code) {
		for (Key key : values())
			if (key.code == code)
				return key;
		return null;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return ItemTableClient.getKey(this);
	}
}
