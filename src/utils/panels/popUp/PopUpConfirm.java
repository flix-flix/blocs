package utils.panels.popUp;

import java.awt.Font;

import data.id.ItemTableClient;
import utils.panels.ClickListener;
import utils.panels.FButton;
import utilsBlocks.UtilsBlocks;

public class PopUpConfirm extends PopUpText {
	private static final long serialVersionUID = -5108408064855489376L;

	// =============== Buttons ===============
	private FButton confirm, cancel;

	// =========================================================================================================================

	public PopUpConfirm() {
		setRect(500, 250);
		setExitOnClick(true);

		fontText = new Font("monospace", Font.BOLD, 20);
		fmText = getFontMetrics(fontText);
		fontButton = new Font("monospace", Font.BOLD, 25);

		confirm = new FButton();
		confirm.setFont(fontButton);
		confirm.setColor(getBackground(), UtilsBlocks.GREEN, 5, UtilsBlocks.GREEN);
		confirm.setSize(150, 50);

		add(confirm);

		cancel = new FButton();
		cancel.setFont(fontButton);
		cancel.setColor(getBackground(), UtilsBlocks.RED, 5, UtilsBlocks.RED);
		cancel.setSize(150, 50);

		cancel.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				close();
			}
		});

		add(cancel);

		refreshLang();
		updateContentLocation();
	}

	// =========================================================================================================================

	public void setConfirmAction(ClickListener listener) {
		confirm.setClickListener(listener);
	}

	// =========================================================================================================================
	// PopUpText

	@Override
	public void updateContentLocation() {
		if (confirm == null)
			return;

		confirm.setXCenter(startX + width / 3);
		confirm.setYBottom(startY + height - 20);

		cancel.setXCenter(startX + width * 2 / 3);
		cancel.setYBottom(startY + height - 20);
	}

	@Override
	public void refreshLang() {
		super.refreshLang();

		confirm.setText(ItemTableClient.getText("pop_up.confirm"));
		cancel.setText(ItemTableClient.getText("pop_up.cancel"));
	}
}
