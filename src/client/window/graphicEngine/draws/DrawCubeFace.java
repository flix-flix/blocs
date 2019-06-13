package client.window.graphicEngine.draws;

import java.awt.Point;
import java.util.ArrayList;

import client.textures.TexturePack;
import client.textures.TextureSquare;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.calcul.StatePixel;
import client.window.graphicEngine.calcul.Vector;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Quadri;
import data.enumeration.Face;
import data.map.Cube;
import utils.FlixBlocksUtils;

public class DrawCubeFace extends Draw {

	// Reference to the "data"
	public Cube cube;

	public static TexturePack texturePack;

	// =================== Basic infos ==================
	int id;
	Face face;
	Vector vx, vy, vz;
	Point3D[] points;

	// =========================================================================================================================

	public DrawCubeFace(int id, Face face, Vector vx, Vector vy, Vector vz, Point3D[] points, Point3D centre, int index,
			Cube cube) {
		this.id = id;
		this.face = face;
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.points = points;
		this.center = centre;
		this.index = index;

		this.cube = cube;
	}

	// =========================================================================================================================

	@Override
	public void init(Point3D camera, Matrix matrice) {
		dist = center.distToOrigin();
	}

	@Override
	public ArrayList<Quadri> getQuadri(Point3D camera, Matrix matrice) {
		Engine.faceTargetTemp = face;
		Engine.cubeTargetTemp = cube;

		quadri.clear();
		TextureSquare texture = Engine.texturePack.getFace(id, face);

		if (cube.miningState != FlixBlocksUtils.NO_MINING)
			texture = texture.fusion(texturePack.getMiningFrame(cube.miningState));

		int nbX = texture.color[0].length;
		int nbY = texture.color.length;
		int nbX1 = (int) (nbX + 1);
		int nbY1 = (int) (nbY + 1);

		Point3D[][] tab3D = new Point3D[nbY1][nbX1];
		Point[][] tab2D = new Point[nbY1][nbX1];

		for (int i = 0; i < nbY1; i++)
			for (int j = 0; j < nbX1; j++)
				switch (face) {
				case UP:
					tab3D[i][j] = vx.multiply(vz.multiply(points[4], j), i);
					break;
				case DOWN:
					tab3D[i][j] = vx.multiply(vz.multiply(points[2], j), -i);
					break;
				case EAST:
					tab3D[i][j] = vy.multiply(vx.multiply(points[1], j), i);
					break;
				case WEST:
					tab3D[i][j] = vy.multiply(vx.multiply(points[2], -j), i);
					break;
				case SOUTH:
					tab3D[i][j] = vy.multiply(vz.multiply(points[0], j), i);
					break;
				case NORTH:
					tab3D[i][j] = vy.multiply(vz.multiply(points[3], -j), i);
					break;
				}

		for (int i = 0; i < nbY1; i++)
			for (int j = 0; j < nbX1; j++)
				tab2D[i][j] = engine.to2D(tab3D[i][j]);

		quadri.add(new Quadri(tab2D[0][0], tab2D[0][nbX], tab2D[nbX][nbX], tab2D[nbX][0], -0xffffff, StatePixel.CONTOUR,
				false));

		for (int i = 0; i < nbY; i++)
			for (int j = 0; j < nbX; j++) {
				int color = texture.color[i][j];

				// If the bloc is targeted by the player : its color will be lighter
				if (cube.isTarget)
					color = texture.lighter(color, 75);

				quadri.add(new Quadri(tab2D[i][j], tab2D[i + 1][j], tab2D[i + 1][j + 1], tab2D[i][j + 1], color,
						StatePixel.FILL));
			}
		return quadri;
	}
}
