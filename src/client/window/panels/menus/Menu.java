package client.window.panels.menus;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import client.window.Game;

public abstract class Menu extends JPanel {
	private static final long serialVersionUID = -5458848328043427804L;

	protected Game game;

	// =========================================================================================================================

	public Menu() {
		this.setLayout(null);
		this.setOpaque(false);

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				click(e);
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

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				resize();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	public Menu(Game game) {
		this();
		this.game = game;
	}

	// =========================================================================================================================

	public void click(MouseEvent e) {
	}

	public void resize() {
	}
}
