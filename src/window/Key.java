package window;

import java.util.EnumSet;

import data.id.ItemTableClient;

public enum Key {

	FORWARD(90), BACKWARD(83), RIGHT(68), LEFT(81), UP(32), DOWN(16), SPEED_BOOST(17),

	PAUSE(27), DEVLOP(114),

	// =========================================================================================================================
	// Dialog

	DIALOG(517),

	VALID(10), DEL(8), SUPPR(127), ARROW_LEFT(37), ARROW_UP(38), ARROW_RIGHT(39), ARROW_DOWN(40),

	END(35), START(36), PAGE_UP(33), PAGE_DOWN(34),

	// =========================================================================================================================

	ACCESS_1(49), ACCESS_2(50), ACCESS_3(51), ACCESS_4(52), ACCESS_5(53), ACCESS_6(54), ACCESS_7(55), ACCESS_8(
			56), ACCESS_9(57),

	KEY_TAB(9);

	// =========================================================================================================================
	// ² Extended keycode : 16_777_394

	public static EnumSet<Key> keys;

	static {
		keys = EnumSet.allOf(Key.class);
	}

	public int code;

	Key(int t) {
		code = t;
	}

	public static Key get(int code) {
		for (Key key : keys)
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
