package editor;

public enum ActionEditor {

	/** Close the editor */
	CLOSE_EDITOR,

	EDIT_CUBE, EDIT_CUBE_TEXTURE, EDIT_MULTI_CUBE, EDIT_MULTI_TEXTURE,

	ALONE, DECOR,

	MINIATURE_MULTICUBE, MINIATURE_CUBE_TEXTURE,

	SQUARE_SELECTION, GRID, PAINT, FILL, PLAYER_COLOR, ROTATE,

	ADD_CUBE, DELETE_CUBE,

	CANCEL, SAVE,

	/** PanColor */
	SELECT_ALPHA,

	// ItemID
	ITEM_TAG, ITEM_ID, ITEM_COLOR, ITEM_SAVE, ITEM_CLEAR;

	// =========================================================================================================================

	public boolean isEditorType() {
		switch (this) {
		case EDIT_CUBE:
		case EDIT_CUBE_TEXTURE:
		case EDIT_MULTI_CUBE:
		case EDIT_MULTI_TEXTURE:
			return true;

		default:
			return false;
		}
	}
}
