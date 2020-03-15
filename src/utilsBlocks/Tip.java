package utilsBlocks;

public enum Tip {

	GAME_GLOBAL, EDITOR_GLOBAL,

	EDITOR_CUBE_TEXTURE, EDITOR_MULTICUBES,

	EDITOR_PENCIL, EDITOR_CALK,;

	// =========================================================================================================================

	static String[] gameGlobal = new String[] { "camera", "user_action", "mouse", "unit", "add", "remove", "f3",
			"camera_mode", "chat" };
	static String[] editorGlobal = new String[] { "zoom", "rotate", "undo", "face_name" };

	static String[] editorCubeTexture = new String[] { "reso", "grid", "fill" };
	static String[] editorMultiCubes = new String[] { "add_cube", "rotate", "remove_cube", "altitude", "camera" };

	static String[] editorCalk = new String[] { "select_rect", "copy_paste", "move", "rotate", "apply_delete",
			"select_all" };
	static String[] editorPencil = new String[] { "new_color", "alpha", "memory", "pick_color", "line_square" };

	// =========================================================================================================================

	public String getPath() {
		switch (this) {
		case GAME_GLOBAL:
			return "tips.game.global";
		case EDITOR_GLOBAL:
			return "tips.editor.global";

		case EDITOR_CUBE_TEXTURE:
			return "tips.editor.cube_texture";
		case EDITOR_MULTICUBES:
			return "tips.editor.multi_cubes";

		case EDITOR_CALK:
			return "tips.editor.calk";
		case EDITOR_PENCIL:
			return "tips.editor.pencil";
		}
		return null;
	}

	public String[] getTags() {
		switch (this) {
		case GAME_GLOBAL:
			return gameGlobal;
		case EDITOR_GLOBAL:
			return editorGlobal;

		case EDITOR_CUBE_TEXTURE:
			return editorCubeTexture;
		case EDITOR_MULTICUBES:
			return editorMultiCubes;

		case EDITOR_CALK:
			return editorCalk;
		case EDITOR_PENCIL:
			return editorPencil;
		}
		return null;
	}
}
