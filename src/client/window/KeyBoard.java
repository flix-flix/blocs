package client.window;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class KeyBoard {

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	// =========================================================================================================================

	public void wheelRotation(MouseWheelEvent e) {
	}

	// =========================================================================================================================

	public void leftClickPressed(MouseEvent e) {
	}

	public void rightClickPressed(MouseEvent e) {
	}

	public void leftClickReleased() {
	}

	public void rightClickReleased() {
	}

	// =========================================================================================================================

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDraged(MouseEvent e) {
	}
}
