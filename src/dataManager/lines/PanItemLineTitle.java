package dataManager.lines;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import data.id.ItemTableClient;

public class PanItemLineTitle extends PanItemLine {
	private static final long serialVersionUID = 1283864202629703304L;

	// =========================================================================================================================

	public PanItemLineTitle() {
		setBorder(5, Color.BLACK);

		ArrayList<LineType> list = new ArrayList<>();

		list.add(LineType.ID);
		list.add(LineType.TAG);
		list.add(LineType.NAME);
		list.add(LineType.MINIATURE);

		for (LineType type : list)
			addLinePartTitle(type);
	}

	// =========================================================================================================================

	@Override
	protected void paintBorder(Graphics g, int margin, int border) {
		super.paintBorder(g, margin, border);
	}

	// =========================================================================================================================

	private void addLinePartTitle(LineType type) {
		PanLinePart part = new PanLinePart(ItemTableClient.getText("data_manager.type." + type.name().toLowerCase()));

		part.setSize(widths.get(type), lineHeight - 2 * getBorderSize());
		part.setLocation(startW, getBorderSize());
		startW += widths.get(type) + separatorWidth;
		add(part);
		panels.put(type, part);
	}
}
