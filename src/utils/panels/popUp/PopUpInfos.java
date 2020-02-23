package utils.panels.popUp;

import java.awt.Color;

import data.id.ItemTableClient;
import utils.panels.ClickListener;
import utils.panels.FButton;

public class PopUpInfos extends PopUpText {
	private static final long serialVersionUID = -1540004210175506495L;

	// =============== Button ===============
	private FButton ok;

	// =========================================================================================================================

	public PopUpInfos() {
		setRect(500, 200);
		setExitOnClick(true);
		
		ok = new FButton();
		ok.setColor(getBackground(), Color.DARK_GRAY, 5, Color.DARK_GRAY);
		ok.setSize(100, 50);

		ok.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				close();
			}
		});

		this.add(ok);

		refreshLang();
		updateContentLocation();
	}

	// =========================================================================================================================

	@Override
	protected void updateContentLocation() {
		if (ok == null)
			return;

		ok.setXCenter(startX + width / 2);
		ok.setYBottom(startY + height - 20);
	}

	@Override
	public void refreshLang() {
		super.refreshLang();

		ok.setText(ItemTableClient.getText("pop_up.ok"));
	}
}
