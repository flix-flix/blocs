package window;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface KeyBoard {

	public default void keyPressed(KeyEvent e) {
	}

	public default void keyReleased(KeyEvent e) {
	}

	// =========================================================================================================================

	public default void wheelRotation(MouseWheelEvent e) {
	}

	// =========================================================================================================================

	public default void leftClickPressed(MouseEvent e) {
	}

	public default void rightClickPressed(MouseEvent e) {
	}

	public default void leftClickReleased() {
	}

	public default void rightClickReleased() {
	}

	// =========================================================================================================================

	public default void mouseMoved(MouseEvent e) {
	}

	public default void mouseDraged(MouseEvent e) {
	}

	// =========================================================================================================================

	public default void mouseExited(MouseEvent e) {
	}
}
