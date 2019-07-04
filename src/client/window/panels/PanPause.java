package client.window.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

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
		this.setVisible(false);

		add(resume = new JButton("Resume"));
		add(options = new JButton("Options"));
		add(save = new JButton("Save"));
		add(quit = new JButton("Quit"));

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

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				resume.setBounds(getWidth() / 2 - 100, 150, 200, 50);
				options.setBounds(getWidth() / 2 - 100, 300, 200, 50);
				save.setBounds(getWidth() / 2 - 100, 450, 200, 50);
				quit.setBounds(getWidth() / 2 - 100, 600, 200, 50);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	// =========================================================================================================================

	public void paintComponent(Graphics g) {
		setBounds(getParent().getBounds());
		g.setColor(new Color(90, 90, 90, 90));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}