package dataManager;

import java.awt.Color;
import java.util.ArrayList;

import data.id.ItemTableClient;
import dataManager.lines.PanItemLine;
import dataManager.lines.PanItemLineTitle;
import utils.panels.FPanel;
import utils.panels.PanCol;
import utils.panels.PanGrid;

public class PanDataManager extends FPanel {
	private static final long serialVersionUID = -4787934446207947471L;

	DataManager data;

	// =============== Menu ===============
	PanCol menu;
	int menuWidth = 200;

	// =============== Title ===============
	PanItemLineTitle title;

	// =============== Lines ===============
	ArrayList<PanItemLine> panels = new ArrayList<>();
	int lineHeight = 50;

	// =========================================================================================================================

	public PanDataManager(DataManager dm) {
		this.data = dm;

		setLayout(null);

		enableVerticalScroll();

		// =============== Menu ===============
		menu = new PanCol();
		menu.setBounds(0, 0, menuWidth, getHeight() - 1);
		this.add(menu);

		PanGrid grid = new PanGrid();
		grid.setCols(2);
		grid.setPadding(5);
		grid.setGridSpace(5);
		
		menu.addTop(grid, PanCol.REMAINING);


		ButtonDataManager lang = new ButtonDataManager(data, ActionDataManager.LANG);
		grid.addMenu(lang);

		ButtonDataManager texture = new ButtonDataManager(data, ActionDataManager.TEXTURE);
		grid.addMenu(texture);

		ButtonDataManager tree = new ButtonDataManager(data, ActionDataManager.TREE);
		grid.addMenu(tree);
	
		ButtonDataManager none = new ButtonDataManager(data, ActionDataManager.NONE);
		grid.addMenu(none);
		ButtonDataManager none2 = new ButtonDataManager(data, ActionDataManager.NONE);
		grid.addMenu(none2);
		ButtonDataManager none3 = new ButtonDataManager(data, ActionDataManager.NONE);
		grid.addMenu(none3);

		ButtonDataManager quit = new ButtonDataManager(data, ActionDataManager.QUIT);
		grid.addMenu(quit);

		// =============== Title ===============
		title = new PanItemLineTitle();
		addLine(title);

		// =============== Lines ===============
		ArrayList<Integer> list = ItemTableClient.getItemIDList();
		for (int i = 0; i < list.size(); i++) {
			PanItemLine panel = new PanItemLine(list.get(i));
			panel.setBackground(i % 2 == 0 ? Color.LIGHT_GRAY : Color.GRAY);
			panel.setBounds(menuWidth + 1, i * lineHeight, getWidth() - menuWidth, lineHeight);

			addLine(panel);
		}

		;

	}

	// =========================================================================================================================

	private void addLine(PanItemLine panel) {
		add(panel);
		panels.add(panel);

		setRealSize(getWidth(), panels.size() * lineHeight);
		updateScroll();
	}

	// =========================================================================================================================

	@Override
	public void updateScroll() {
		super.updateScroll();

		for (int i = 0; i < panels.size(); i++)
			panels.get(i).setLocation(menuWidth, i * lineHeight - getScrolled());
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();

		for (PanItemLine panel : panels)
			panel.setSize(getWidth() - 1 - menuWidth, lineHeight);

		menu.setSize(menuWidth, getHeight());
	}

	// =========================================================================================================================

}
