package mainMenu.server;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import data.id.ItemTableClient;
import mainMenu.MainMenu;
import mainMenu.MainMenuAction;
import mainMenu.buttons.ButtonAction;
import server.Server;
import server.ServerDescription;
import utils.panels.ButtonPad;
import utils.panels.ClickListener;
import utils.panels.FButton;
import utils.panels.FPanel;
import utilsBlocks.UtilsBlocks;

public class PanServer extends FPanel {
	private static final long serialVersionUID = -6109414994781199483L;

	private MainMenu main;

	private ButtonAction quit;

	// =============== Buttons/Label ===============
	/** Category */
	private FButton hosted, known;
	/** None */
	private ButtonPad noneHosted, noneKnows;
	/** Clickable */
	private ButtonPad start, add;

	public PanServerAdd panAdd;

	// =============== Display ===============
	/** Number of pixels between component border and quit button */
	private int margin = 10;
	/** Number of pixels between server's panels */
	private int space = 25;

	private Font fontCat = new Font("monospace", Font.BOLD, 35);
	private int catH = 75, catW = 1000;

	private Font fontNone = new Font("monospace", Font.BOLD, 25);

	// =============== Servers' Panel ===============
	private ArrayList<PanServerDescription> listHosted = new ArrayList<>();
	private ArrayList<PanServerDescription> listKnows = new ArrayList<>();

	// =========================================================================================================================

	public PanServer(MainMenu main) {
		this.main = main;

		this.enableVerticalScroll();
		this.setBackground(Color.LIGHT_GRAY);

		// =============== Start/Add Panel ===============
		panAdd = new PanServerAdd(main);
		panAdd.setLocation(0, 0);
		panAdd.setSize(getWidth(), getHeight());
		add(panAdd);

		// =============== Category ===============
		hosted = new FButton();
		hosted.setColor(Color.LIGHT_GRAY, Color.GRAY, 10, Color.GRAY);
		hosted.setText("HOSTED");
		hosted.setFont(fontCat);
		hosted.setSize(catW, catH);
		hosted.setLocation(getWidth() / 2, 50);
		this.add(hosted);

		known = new FButton();
		known.setColor(Color.LIGHT_GRAY, Color.GRAY, 10, Color.GRAY);
		known.setText("KNOWS");
		known.setFont(fontCat);
		known.setSize(catW, catH);
		known.setLocation(getWidth() / 2, 250);
		this.add(known);

		// =============== None ===============
		this.add(noneHosted = new ButtonPad("NONE", fontNone, Color.LIGHT_GRAY, 5, UtilsBlocks.RED));
		this.add(noneKnows = new ButtonPad("NONE", fontNone, Color.LIGHT_GRAY, 5, UtilsBlocks.RED));

		// =============== Start/Add Buttons ===============

		this.add(start = new ButtonPad("START", fontNone, Color.LIGHT_GRAY, 5, UtilsBlocks.GREEN));
		this.add(add = new ButtonPad("ADD", fontNone, Color.LIGHT_GRAY, 5, UtilsBlocks.GREEN));

		start.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				panAdd.setHosted(true);
				panAdd.setVisible(true);
			}
		});

		add.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				panAdd.setHosted(false);
				panAdd.setVisible(true);
			}
		});

		// =============== Quit ===============
		quit = new ButtonAction(main, MainMenuAction.SERVER_QUIT);
		quit.setSize(100, 100);
		quit.setBorder(5, Color.DARK_GRAY);
		add(quit);
	}

	// =========================================================================================================================

	public void startServer(Server server) {
		PanServerDescription pan = new PanServerDescription(main, server);
		listHosted.add(pan);
		add(pan);
		refresh();
	}

	public void addServer(ServerDescription server) {
		PanServerDescription pan = new PanServerDescription(main, server);
		listKnows.add(pan);
		add(pan);
		refresh();
	}

	public void removeHosted(ServerDescription description) {
		for (int i = 0; i < listHosted.size(); i++)
			if (description == listHosted.get(i).description) {
				remove(listHosted.remove(i));

				refresh();
				return;
			}
	}

	public void removeKnown(ServerDescription description) {
		for (int i = 0; i < listKnows.size(); i++)
			if (description == listKnows.get(i).description) {
				remove(listKnows.remove(i));

				refresh();
				return;
			}
	}

	// =========================================================================================================================

	private void refresh() {
		noneHosted.setVisible(listHosted.isEmpty());
		noneKnows.setVisible(listKnows.isEmpty());

		int startY = 50;

		hosted.setLocation(getWidth() / 2 - hosted.getWidth() / 2, startY);
		startY += hosted.getHeight() + space;

		if (listHosted.isEmpty()) {
			noneHosted.setLocation(getWidth() / 2 - noneHosted.getWidth() - 10, startY);
			start.setLocation(getWidth() / 2 + 10, startY);
		} else {
			for (PanServerDescription pan : listHosted) {
				pan.setLocation(getWidth() / 2 - pan.getWidth() / 2, startY);
				startY += pan.getHeight() + space;
			}

			start.setLocation(getWidth() / 2 - start.getWidth() / 2, startY);
		}
		startY += hosted.getHeight() + space;

		known.setLocation(getWidth() / 2 - known.getWidth() / 2, startY);
		startY += known.getHeight() + space;

		if (listKnows.isEmpty()) {
			noneKnows.setLocation(getWidth() / 2 - noneKnows.getWidth() - 10,
					startY + (add.getHeight() - noneKnows.getHeight()) / 2);
			add.setLocation(getWidth() / 2 + 10, startY);
		} else {
			for (PanServerDescription pan : listKnows) {
				pan.setLocation(getWidth() / 2 - pan.getWidth() / 2, startY);
				startY += pan.getHeight() + space;
			}

			add.setLocation(getWidth() / 2 - add.getWidth() / 2, startY);
		}
		startY += add.getHeight() + space;

		panAdd.setSize(getWidth(), getHeight());

		quit.setBottomRightCorner(getWidth() - margin, getHeight() - margin);

		repaint();
	}

	// =========================================================================================================================

	public void refreshLang() {
		hosted.setText(ItemTableClient.getText("main_menu.server.hosted"));
		known.setText(ItemTableClient.getText("main_menu.server.known"));
		noneHosted.setText(ItemTableClient.getText("main_menu.server.none"));
		noneKnows.setText(ItemTableClient.getText("main_menu.server.none"));
		add.setText(ItemTableClient.getText("main_menu.server.add"));
		start.setText(ItemTableClient.getText("main_menu.server.start"));

		panAdd.refreshLang();

		for (PanServerDescription pan : listHosted)
			pan.refreshLang();

		for (PanServerDescription pan : listKnows)
			pan.refreshLang();

		refresh();
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();

		refresh();
	}
}
