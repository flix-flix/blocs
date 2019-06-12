package client.keys;

import java.util.EnumSet;

public enum Key {

	FORWARD(90), BACKWARD(83), RIGHT(68), LEFT(81), UP(32), DOWN(16), SPRINT(17),

	PAUSE(27), DEVLOP(114),

	// =========================================================================================================================
	// Dialog

	KEY_ENTER(10), KEY_DEL(8), KEY_SUPPR(127), KEY_LEFT(37), KEY_UP(38), KEY_RIGHT(39), KEY_DOWN(40),

	KEY_END(35), KEY_START(36), KEY_PAGE_UP(33), KEY_PAGE_DOWN(34),

	// =========================================================================================================================

	ACCESS_1(49), ACCESS_2(50), ACCESS_3(51), ACCESS_4(52), ACCESS_5(53), ACCESS_6(54), ACCESS_7(55), ACCESS_8(
			56), ACCESS_9(57),

	KEY_TAB(9),

	// =========================================================================================================================
	// Extended keycode

	KEY_POWER(16777394);// ²

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
}
