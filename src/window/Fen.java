package window;

import java.awt.Cursor;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import mainMenu.MainMenu;

public class Fen extends JFrame {
	private static final long serialVersionUID = 5348701813574947310L;

	// ============= Cursor ===================
	public static Cursor cursorInvisible = Toolkit.getDefaultToolkit().createCustomCursor(
			new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");

	// ============= Display ===================
	private Displayable display;

	private MainMenu mainMenu;

	// =========================================================================================================================

	public Fen() {
		this.setTitle("Blocs");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1200, 1040);
		this.setLocation(0, 0);
		// this.setExtendedState(MAXIMIZED_BOTH);

		// ======================================

		this.setLayout(null);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());

		setDisplay(mainMenu = new MainMenu(this));

		// =========================================================================================================================

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == 1)
					display.getKeyBoard().leftClickReleased();
				else if (e.getButton() == 3)
					display.getKeyBoard().rightClickReleased();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1)
					display.getKeyBoard().leftClickPressed(e);
				else if (e.getButton() == 3)
					display.getKeyBoard().rightClickPressed(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				display.getKeyBoard().mouseMoved(e);
			}

			public void mouseDragged(MouseEvent e) {
				display.getKeyBoard().mouseDraged(e);
			}
		});

		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				display.getKeyBoard().wheelRotation(e);
			}
		});

		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				display.updateSize(getContentPane().getWidth(), getContentPane().getHeight());
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		this.setVisible(true);
	}

	// =========================================================================================================================

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED)
				display.getKeyBoard().keyPressed(e);
			else if (e.getID() == KeyEvent.KEY_RELEASED)
				display.getKeyBoard().keyReleased(e);
			// else if (e.getID() == KeyEvent.KEY_TYPED);

			return false;
		}
	}

	// =========================================================================================================================

	public void returnToMainMenu() {
		setDisplay(mainMenu);
		updateCursor();
	}

	public void setDisplay(Displayable display) {
		if (this.display != null)
			this.display.stop();

		this.display = display;
		display.resume();
		display.updateSize(getContentPane().getWidth(), getContentPane().getHeight());
		this.setContentPane(display.getContentPane());
	}

	// =========================================================================================================================
	// Cursor

	public void updateCursor() {
		if (display == null)
			return;
		setCursor(display.getCursor());
	}
}
