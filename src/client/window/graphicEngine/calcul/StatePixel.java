package client.window.graphicEngine.calcul;

public enum StatePixel {
	// Initial
	EMPTY(true, true, false),
	// Painted
	FILL,
	// Black line
	CONTOUR,

	// Full transparency (needed to intercept target)
	INVISIBLE(true, false, false),
	// Colored Transparency
	TRANSPARENT(true, false, true),

	// Bloc of map with status preview
	PREVIEW(true, false, true),
	// Next bloc added preview
	// (Needed to be able to select bloc beyond this preview)
	PREVIEW_THROUGHT(true, true, true),

	// Background
	SKY(false, true, false);

	public boolean isDrawable, targetableThrought, isTransparent;

	private StatePixel(boolean isEmpty, boolean targetableThrought, boolean isTransparent) {
		this.isDrawable = isEmpty;
		this.targetableThrought = targetableThrought;
		this.isTransparent = isTransparent;
	}

	private StatePixel() {
		this(false, false, false);
	}
}
