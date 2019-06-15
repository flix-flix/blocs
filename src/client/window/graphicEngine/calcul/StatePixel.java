package client.window.graphicEngine.calcul;

public enum StatePixel {
	// Initial
	EMPTY(true),
	// Painted
	FILL,
	// Full transparency (needed to intercept target)
	INVISIBLE(true),
	// Colored Transparency
	TRANSPARENT,
	// Black line
	CONTOUR,
	// Background
	SKY;

	public boolean isEmpty;

	private StatePixel(boolean isDrawable) {
		this.isEmpty = isDrawable;
	}

	private StatePixel() {
		this(false);
	}
}
