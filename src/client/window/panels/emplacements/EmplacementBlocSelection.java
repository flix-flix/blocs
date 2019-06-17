package client.window.panels.emplacements;

import java.awt.Color;
import java.awt.Graphics;

import client.session.Session;
import data.enumeration.ItemID;

public class EmplacementBlocSelection extends Emplacement {
	private static final long serialVersionUID = -8393842761922506846L;

	public ItemID itemID;

	public boolean selected = false;

	public EmplacementBlocSelection(int x, int y, int width, int height, Session session, ItemID itemID) {
		super(x, y, width, height, session);
		this.itemID = itemID;

		this.setBackground(Color.GRAY);

		selected = session.selectedItemID == itemID;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(selected ? Color.LIGHT_GRAY : Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (selected) {
			g.setColor(Color.GRAY);
			g.drawRect(0, 0, getWidth(), getHeight());
			g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
			g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
		}

		g.setColor(Color.BLACK);
		g.drawString(itemID.name(), 10, getHeight() / 2);
	}

	@Override
	public void click() {
		session.setSelectedItemID(itemID);
		selected = true;
	}
}
