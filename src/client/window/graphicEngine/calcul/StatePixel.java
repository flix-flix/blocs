package client.window.graphicEngine.calcul;

public enum StatePixel {
	EMPTY(true), FILL, TRANSPARENT(true), GLASS(true), LIQUID;

	boolean isDrawable;

	private StatePixel(boolean isDrawable) {
		this.isDrawable = isDrawable;
	}

	private StatePixel() {
		this(false);
	}
}
