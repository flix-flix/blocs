package client.window.panels.menus;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import client.session.Session;

public abstract class Menu extends JPanel {
	private static final long serialVersionUID = -5458848328043427804L;

	Session session;

	public Menu() {
		this.setOpaque(false);

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				click();
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
	}

	public Menu(Session session) {
		this();
		this.session = session;

	}

	public abstract void click();
}