package utilsBlocks.help;

public interface Tip<T extends Enum<?> & Tip<T>> {

	String getPath();

	Tip<T>[] _values();

	// =========================================================================================================================

	@SuppressWarnings("unchecked")
	default Tip<T> next() {
		return next((T) this);
	}

	@SuppressWarnings("unchecked")
	default Tip<T> previous() {
		return previous((T) this);
	}

	// =========================================================================================================================

	public static <T extends Enum<?> & Tip<T>> Tip<T> next(T tip) {
		if (tip.ordinal() + 1 == tip._values().length)
			return tip._values()[0];
		return tip._values()[tip.ordinal() + 1];
	}

	public static <T extends Enum<?> & Tip<T>> Tip<T> previous(T tip) {
		if (tip.ordinal() == 0)
			return tip._values()[tip._values().length - 1];
		return tip._values()[tip.ordinal() - 1];
	}
}
