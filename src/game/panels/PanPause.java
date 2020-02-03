package game.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import data.id.ItemTableClient;
import game.Game;

public class PanPause extends JPanel {
	private static final long serialVersionUID = 2735034739187347959L;

	private JButton resume, options, saveQuit;

	// =========================================================================================================================

	public PanPause(Game game) {
		this.setOpaque(false);
		this.setLayout(null);
		this.setVisible(false);

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				e.consume();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				e.consume();
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

		add(resume = new JButton());
		add(options = new JButton());
		add(saveQuit = new JButton());
		resume.setSize(200, 50);
		options.setSize(200, 50);
		saveQuit.setSize(200, 50);

		options.setEnabled(false);

		refreshLang();

		resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.resume();
				game.fen.requestFocusInWindow();
			}
		});
		options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		saveQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.fen.returnToMainMenu();
			}
		});
	}

	// =========================================================================================================================

	public void paintComponent(Graphics g) {
		g.setColor(new Color(90, 90, 90, 90));
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	// =========================================================================================================================

	public void refreshLang() {
		resume.setText(ItemTableClient.getText("game.pause.buttons.resume"));
		options.setText(ItemTableClient.getText("game.pause.buttons.options"));
		saveQuit.setText(ItemTableClient.getText("game.pause.buttons.save_quit"));
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		// TODO [Improve] Pause buttons centered
		int start = (height - 3 * 50 - 2 * 100) / 2;

		resume.setLocation(getWidth() / 2 - 100, start);
		options.setLocation(getWidth() / 2 - 100, start + 150);
		saveQuit.setLocation(getWidth() / 2 - 100, start + 300);
	}
}