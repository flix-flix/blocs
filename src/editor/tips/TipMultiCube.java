package editor.tips;

import utilsBlocks.help.Tip;

public enum TipMultiCube implements Tip<TipMultiCube> {

	ADD_CUBE, REMOVE_CUBE, ALTITUDE;

	@Override
	public String getPath() {
		return "tips.editor.multi_cubes.";
	}

	@Override
	public Tip<TipMultiCube>[] _values() {
		return values();
	}
}
