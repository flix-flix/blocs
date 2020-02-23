package mainMenu.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextField;

import data.id.ItemTableClient;
import mainMenu.MainMenu;
import mainMenu.MainMenuAction;
import server.ServerDescription;
import utils.panels.ClickListener;
import utils.panels.FButton;
import utils.panels.popUp.PopUp;

public class PanServerAdd extends PopUp {
	private static final long serialVersionUID = 6516568869780458100L;

	/** Align (X abscissa) Label and JTextField */
	private static final int shift = 40;

	private Font font = new Font("monospace", Font.BOLD, 14);
	private Font fontTextField = new Font("monospace", Font.BOLD, 15);

	/** true: ask user fort port | false : ask user for ip */
	private boolean hosted = true;

	private JTextField name, ip;
	private FButton valid, cancel;

	private String textName, textIP, textPort;

	// =========================================================================================================================

	public PanServerAdd(MainMenu main) {
		setRect(500, 500);
		setBackground(Color.GRAY);
		setBorder(10, Color.DARK_GRAY);
		setVoile(new Color(90, 90, 90, 150));
		setExitOnClick(true);

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
		valid.setForeground(Color.LIGHT_GRAY);
		valid.setBackground(Color.DARK_GRAY);
		valid.setBorder(2, Color.LIGHT_GRAY);
		valid.setInColor(Color.LIGHT_GRAY, Color.DARK_GRAY, Color.DARK_GRAY);

		valid.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				try {
					if (hosted) {
						ServerDescription descritpion = new ServerDescription("", Integer.valueOf(ip.getText()),
								name.getText());
						main.clickServer(MainMenuAction.SERVER_START, descritpion);
					} else {
						String[] parts = PanServerAdd.this.ip.getText().split(":");
						if (parts.length != 2)
							return;
						String ip = parts[0];
						int port = Integer.valueOf(parts[1]);

						ServerDescription descritpion = new ServerDescription(ip, port, name.getText());
						main.clickServer(MainMenuAction.SERVER_ADD, descritpion);
					}

					close();
				} catch (NumberFormatException e) {
				}
			}
		});

		this.add(valid);

		cancel = new FButton();
		cancel.setSize(100, 50);
		cancel.setLocation(getWidth() / 2 + 5, 350);
		cancel.setText("CANCEL");
		cancel.setForeground(Color.LIGHT_GRAY);
		cancel.setBackground(Color.DARK_GRAY);
		cancel.setBorder(2, Color.LIGHT_GRAY);
		cancel.setInColor(Color.LIGHT_GRAY, Color.DARK_GRAY, Color.DARK_GRAY);

		cancel.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				close();
			}
		});

		this.add(cancel);
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setFont(font);
		g.setColor(Color.WHITE);

		g.drawString(textName + ": ", getWidth() / 2 - 100 - shift, getHeight() / 2 - 120);
		g.drawString((hosted ? textPort : textIP) + ": ", getWidth() / 2 - 100 - shift, getHeight() / 2 - 20);
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
	public void close() {
		super.close();

		name.setText("");
		ip.setText("");
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();

		name.setLocation(getWidth() / 2 - 50 - shift, getHeight() / 2 - 150);
		ip.setLocation(getWidth() / 2 - 50 - shift, getHeight() / 2 - 50);

		valid.setLocation(getWidth() / 2 - valid.getWidth() - 5, getHeight() / 2 + 100);
		cancel.setLocation(getWidth() / 2 + 5, getHeight() / 2 + 100);
	}
}
