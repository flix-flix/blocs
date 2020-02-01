package launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class LauncherButton extends JPanel {
	private static final long serialVersionUID = 944433026405542062L;

	Launcher launcher;
	LauncherButtonAction action;

	LauncherButton(Launcher launcher, LauncherButtonAction action) {
		this.launcher = launcher;
		this.action = action;

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				launcher.click(action);
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

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.ORANGE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawString(action.name(), getWidth() - 50, getHeight() / 2 + 10);
	}

	// =========================================================================================================================

	enum LauncherButtonAction {
		GAME, EDITOR, QUIT;
	}
}
