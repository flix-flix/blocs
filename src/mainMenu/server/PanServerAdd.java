package mainMenu.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextField;

import data.id.ItemTableClient;
import mainMenu.MainMenuAction;
import server.ServerDescription;
import utils.panels.ClickListener;
import utils.panels.FButton;
import utils.panels.FPanel;

public class PanServerAdd extends FPanel {
	private static final long serialVersionUID = 6516568869780458100L;

	private boolean hosted = true;

	private JTextField name, ip;

	private FButton valid, cancel;

	private Font font = new Font("monospace", Font.BOLD, 14);
	private Font fontTextField = new Font("monospace", Font.BOLD, 15);

	private String textName, textIP, textPort;

	// =========================================================================================================================

	public PanServerAdd(PanServer panel) {
		this.setSize(500, 500);

		name = new JTextField();
		name.setSize(200, 50);
		name.setLocation(getWidth() / 2 - 50, 100);
		name.setFont(fontTextField);
		this.add(name);

		ip = new JTextField();
		ip.setSize(200, 50);
		ip.setLocation(getWidth() / 2 - 50, 200);
		ip.setFont(fontTextField);
		this.add(ip);

		valid = new FButton();
		valid.setSize(100, 50);
		valid.setLocation(getWidth() / 2 - valid.getWidth() - 5, 350);
		valid.setText("START");
		valid.setForeground(Color.BLACK);
		valid.setBackground(Color.LIGHT_GRAY);

		valid.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				try {
					if (hosted) {
						ServerDescription descritpion = new ServerDescription("", Integer.valueOf(ip.getText()),
								name.getText());
						panel.main.clickServer(MainMenuAction.SERVER_START, descritpion);
					} else {
						String[] parts = PanServerAdd.this.ip.getText().split(":");
						if (parts.length != 2)
							return;
						String ip = parts[0];
						int port = Integer.valueOf(parts[1]);

						ServerDescription descritpion = new ServerDescription(ip, port, name.getText());
						panel.main.clickServer(MainMenuAction.SERVER_ADD, descritpion);
					}

					name.setText("");
					ip.setText("");
					setVisible(false);
				} catch (NumberFormatException e) {
				}
			}
		});

		this.add(valid);

		cancel = new FButton();
		cancel.setSize(100, 50);
		cancel.setLocation(getWidth() / 2 + 5, 350);
		cancel.setText("CANCEL");
		cancel.setForeground(Color.BLACK);
		cancel.setBackground(Color.LIGHT_GRAY);

		cancel.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				name.setText("");
				ip.setText("");
				setVisible(false);
			}
		});

		this.add(cancel);
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		fillCenteredRoundRect(g, getWidth() / 2, getHeight() / 2, 250, 100);
		g.setColor(Color.GRAY);
		fillCenteredRoundRect(g, getWidth() / 2, getHeight() / 2, 250 - 5, 100);

		g.setFont(font);
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(textName + ": ", getWidth() / 2 - 100, 130);

		g.drawString((hosted ? textPort : textIP) + ": ", getWidth() / 2 - 100, 230);
	}

	// =========================================================================================================================

	public void setHosted(boolean hosted) {
		this.hosted = hosted;
		refreshLang();
		repaint();
	}

	// =========================================================================================================================

	public void refreshLang() {
		valid.setText(ItemTableClient.getText("main_menu.server." + (hosted ? "start" : "add")));
		cancel.setText(ItemTableClient.getText("main_menu.server.cancel"));

		textName = ItemTableClient.getText("main_menu.server.name");
		textPort = ItemTableClient.getText("main_menu.server.port");
		textIP = ItemTableClient.getText("main_menu.server.ip");
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();

		if (valid == null)
			return;
		valid.setLocation(getWidth() / 2 - valid.getWidth() - 5, 300);
		cancel.setLocation(getWidth() / 2 + 5, 300);
	}
}
