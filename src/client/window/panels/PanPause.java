package client.window.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.session.Session;

public class PanPause extends JPanel {
	private static final long serialVersionUID = 2735034739187347959L;

	Session session;

	public JButton resume, save, options, quit;

	// =========================================================================================================================

	public PanPause(Session session) {
		this.session = session;
		this.setOpaque(false);
		this.setLayout(null);

		resume = new JButton("Reprendre");
		save = new JButton("Sauvegarder");
		quit = new JButton("Quitter");
		options = new JButton("Options");

		resume.setBounds(900, 150, 200, 50);
		options.setBounds(900, 300, 200, 50);
		save.setBounds(900, 450, 200, 50);
		quit.setBounds(900, 600, 200, 50);

	

		add(resume);
		add(options);
		add(save);
		add(quit);

		options.setEnabled(false);
		save.setEnabled(false);

		resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				session.keyboard.resume();
				session.fen.requestFocusInWindow();
			}
		});
		options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	// =========================================================================================================================

	public void paintComponent(Graphics g) {
		this.setBounds(getParent().getBounds());

		g.setColor(new Color(90, 90, 90, 90));
		g.fillRect(0, 0, getWidth(), getHeight());

	}
}