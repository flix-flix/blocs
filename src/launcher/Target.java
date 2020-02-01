package launcher;

import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.extended.DrawCubeFace;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.structures.Quadri;
import data.map.enumerations.Face;

public class Target {

	public ModelCube cube;
	public Face face;
	public int quadri;

	// =========================================================================================================================

	public Target(ModelCube cube, Face face, int quadri) {
		this.cube = cube;
		this.face = face;
		this.quadri = quadri;
	}

	public Target(Engine engine) {
		DrawCubeFace draw = ((DrawCubeFace) engine.getDrawTarget());
		Quadri quadri = engine.getQuadriTarget();

		cube = draw == null ? null : draw.cube;
		face = draw == null ? null : draw.face;
		this.quadri = (quadri == null ? Quadri.NOT_NUMBERED : quadri.id);
	}

	// =========================================================================================================================

	public boolean equals(Target target, boolean quadriPrecision) {
		// TODO [Improve] Target.equals()
		if (target == null || !target.isValid() || !isValid())
			return false;
		return cube.equals(target.cube) && face == target.face && (!quadriPrecision || quadri == target.quadri);
	}

	public boolean isValid() {
		return cube != null && face != null;
	}

	public boolean hasQuadri() {
		return quadri != Quadri.NOT_NUMBERED;
	}
}
