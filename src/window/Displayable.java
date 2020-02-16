package window;

import java.awt.Cursor;

import javax.swing.JPanel;

public interface Displayable {

	public JPanel getContentPane();

	public void updateSize(int x, int y);

	public Cursor getCursor();

	public KeyBoard getKeyBoard();

	public void stop();

	public default void resume() {
	}
}
