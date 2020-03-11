package environment;

import data.map.Coord;
import data.map.enumerations.Face;
import environment.extendsData.CubeClient;
import environment.extendsEngine.DrawCubeFace;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Quadri;

public class Target {

	public CubeClient cube;
	public Face face;
	public int quadri;

	// =========================================================================================================================

	public Target() {
	}

	public Target(CubeClient cube, Face face, int quadri) {
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

	// =========================================================================================================================

	/**
	 * @return The cube in the air next to the targeted face of the targeted cube
	 */
	public Coord getAir() {
		return new Coord(cube).face(face);
	}
}
