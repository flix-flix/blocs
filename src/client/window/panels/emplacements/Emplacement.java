package client.window.panels.emplacements;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import client.session.Session;

public abstract class Emplacement extends JPanel {
	private static final long serialVersionUID = -5458848328043427804L;

	Session session;

	int x, y, width, height;

	public Emplacement(int x, int y, int width, int height, Session session) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.session = session;

		this.setBounds(x, y, width, height);
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

	public abstract void click();
}
