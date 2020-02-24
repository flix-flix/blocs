package utilsBlocks;

import java.awt.Color;

import utilsBlocks.help.Tip;

public class UtilsBlocks {

	public final static Color RED = new Color(157, 44, 44);
	public final static Color GREEN = new Color(12, 126, 28);

	// =========================================================================================================================

	public static <T extends Enum<?> & Tip<T>> void val(T tip) {
		tip.ordinal();
		tip._values();
	}



	// public static <T extends Enum<?> & Tip<T>> T next(T tip) {
	// if (tip.ordinal() + 1 == tip._values().length)
	// return tip._values()[0];
	// return tip._values()[tip.ordinal() + 1];
	// }
	//
	// public static <T extends Enum<?> & Tip<T>> T previous(T tip) {
	// if (tip.ordinal() == 0)
	// return tip._values()[tip._values().length - 1];
	// return tip._values()[tip.ordinal() - 1];
	// }
}
