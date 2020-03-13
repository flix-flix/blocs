package dataManager.lines;

import java.awt.Color;
import java.awt.Graphics;
import java.util.TreeMap;

import data.id.Item;
import data.id.ItemTableClient;
import utils.panels.FPanel;

public class PanItemLine extends FPanel {
	private static final long serialVersionUID = -2526947285443856811L;

	protected TreeMap<LineType, PanLinePart> panels = new TreeMap<>();

	protected int lineHeight = 50;

	// =============== Width ===============
	protected int separatorWidth = 3;
	protected int startW = 5;

	protected TreeMap<LineType, Integer> widths = new TreeMap<>();

	// =========================================================================================================================

	protected PanItemLine() {
		widths.put(LineType.ID, 50);
		widths.put(LineType.TAG, 100);
		widths.put(LineType.NAME, 100);
		widths.put(LineType.MINIATURE, 60);
	}

	public PanItemLine(int itemID) {
		this();
		Item item = ItemTableClient.get(itemID);

		addLinePart(new PanLinePart("" + itemID), LineType.ID);

		addLinePart(new PanLinePart(item.tag), LineType.TAG);

		addLinePart(new PanLinePart(item.name == null ? "-" : item.name), LineType.NAME);

		// =============== Texture ===============
		PanLinePart pan = new PanLinePart();
		pan.setModel(ItemTableClient.create(itemID));
		addLinePart(pan, LineType.MINIATURE);
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		g.setColor(Color.WHITE);
		if (this instanceof PanItemLineTitle)
			g.fillRect(0, 0, startW - 5, getHeight());
		else
			g.fillRect(0, 0, startW, getHeight());

		g.setColor(Color.BLACK);
		g.fillRect(getUndrawSize(), getUndrawSize(), 5, getContentHeight());
	}

	// =========================================================================================================================

	protected void addLinePart(PanLinePart pan, LineType type) {
		pan.setSize(widths.get(type), lineHeight);
		pan.setLocation(startW, 0);
		startW += widths.get(type) + separatorWidth;
		add(pan);
		panels.put(type, pan);
	}

	// =========================================================================================================================

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);

		if (panels != null)
			for (PanLinePart pan : panels.values())
				pan.setBackground(bg);
	}
}
