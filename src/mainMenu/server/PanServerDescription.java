package mainMenu.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import data.id.ItemTableClient;
import mainMenu.MainMenu;
import mainMenu.MainMenuAction;
import server.Server;
import server.ServerDescription;
import utils.panels.ButtonPad;
import utils.panels.ClickListener;
import utils.panels.FButton;

public class PanServerDescription extends FButton {
	private static final long serialVersionUID = 2800972514811757907L;

	// =============== Data ===============
	ServerDescription description;

	// =============== Buttons ===============
	private ButtonPad join, stop;
	private Font fontButton = new Font("monospace", Font.BOLD, 25);

	// =============== Font ===============
	private Font fontName = new Font("monospace", Font.BOLD, 35);
	private Font fontIP = new Font("monospace", Font.BOLD, 15);

	// =========================================================================================================================

	public PanServerDescription(MainMenu main, ServerDescription description) {
		this.description = description;

		this.setBorder(10, Color.GRAY);
		this.setBackground(Color.LIGHT_GRAY);
		this.setSize(600, 150);
		this.setPadding(5);

		join = new ButtonPad("JOIN", fontButton, Color.LIGHT_GRAY, PanServer.GREEN, 5);

		join.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				main.clickServer(MainMenuAction.SERVER_JOIN, description);
			}
		});

		this.add(join);

		// If not hosted
		if (description.server == null) {
			stop = new ButtonPad("DELETE", fontButton, Color.LIGHT_GRAY, PanServer.RED, 5);

			stop.setClickListener(new ClickListener() {
				@Override
				public void leftClick() {
					main.clickServer(MainMenuAction.SERVER_DELETE, description);
				}
			});

			this.add(stop);
		}

		refreshLang();
	}

	public PanServerDescription(MainMenu main, Server server) {
		this(main, server.getDescription());

		stop = new ButtonPad("STOP", fontButton, Color.LIGHT_GRAY, PanServer.RED, 5);

		stop.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				main.clickServer(MainMenuAction.SERVER_STOP, description);
			}
		});

		this.add(stop);
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		g.setColor(Color.GRAY);
		drawCenteredRect(g, getContentHeight() / 2, getContentHeight() / 2, getContentHeight() - 20,
				getContentHeight() - 20, 5);

		g.setColor(Color.DARK_GRAY);
		g.setFont(fontName);
		g.drawString(description.name, getContentHeight(), 50);

		g.setFont(fontIP);
		g.drawString(description.ip + " : " + description.port, getContentHeight(), 75);
	}

	// =========================================================================================================================

	public void refreshLang() {
		join.setText(ItemTableClient.getText("main_menu.server.join"));
		stop.setText(ItemTableClient.getText("main_menu.server." + (description.server == null ? "delete" : "stop")));

		refreshLocation();
	}

	public void refreshLocation() {
		if (join != null)
			join.setBottomRightCorner(getWidth() - getUndrawSize(), getHeight() - getUndrawSize());
		if (stop != null)
			stop.setBottomRightCorner(getWidth() - getUndrawSize() - join.getWidth() - 5,
					getHeight() - getUndrawSize());
	}
}
