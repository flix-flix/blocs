package utils.panels;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JPanel;

public class PanCard extends FPanel {
	private static final long serialVersionUID = 7391396019732428806L;

	private ArrayList<String> list = new ArrayList<>();
	private TreeMap<String, JPanel> map = new TreeMap<>();

	/** Value of {@link #index} if all the panels are hidden */
	private static final int HIDEN = -1;
	/** Index of the displayed panel */
	private int index = HIDEN;

	// =========================================================================================================================

	public PanCard() {
	}

	// =========================================================================================================================

	/** Add panel at the indicated key */
	public void put(String key, JPanel panel) {
		if (list.isEmpty()) {
			index = 0;
			panel.setVisible(true);
		} else
			panel.setVisible(false);

		if (!list.contains(key))
			list.add(key);
		map.put(key, panel);

		panel.setSize(getWidth(), getHeight());
		add(panel);
	}

	// =========================================================================================================================

	/** Show the panel corresponding to the given key */
	public void show(String key) {
		int _index = list.indexOf(key);

		// If same ignore
		if (index == _index)
			return;

		// If was displaying another : hide it
		if (index != HIDEN)
			map.get(list.get(index)).setVisible(false);

		// Show the new
		index = _index;
		map.get(key).setVisible(true);
	}

	/**
	 * Show the next panel (by added order)<br>
	 * If all are hidden : display the first added
	 */
	public void next() {
		if (index == HIDEN)
			index = 0;
		else {
			index++;
			index %= list.size();
		}
		show(list.get(index));
	}

	/**
	 * Show the previous panel (by added order) <br>
	 * If all hidden : display last added
	 */
	public void prev() {
		if (index == HIDEN)
			index = list.size() - 1;
		else if (index == 0)
			index = list.size() - 1;
		else
			index--;

		show(list.get(index));
	}

	// =========================================================================================================================

	/** Hide all the panels */
	public void hide() {
		for (JPanel panel : map.values())
			panel.setVisible(false);

		index = HIDEN;
	}

	// =========================================================================================================================

	/** Returns the visible panel */
	public JPanel getVisible() {
		String key = getVisibleName();
		if (key == null)
			return null;
		return map.get(key);
	}

	/** Returns the name of the visible panel */
	public String getVisibleName() {
		if (index == HIDEN)
			return null;
		return list.get(index);
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();

		for (JPanel panel : map.values())
			panel.setSize(getWidth(), getHeight());
	}
}
