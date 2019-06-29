package client.window.graphicEngine.draws;

import java.awt.Point;
import java.util.ArrayList;

import client.textures.TexturePack;
import client.textures.TextureSquare;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.calcul.StatePixel;
import client.window.graphicEngine.calcul.Vector;
import client.window.graphicEngine.models.ModelCube;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Quadri;
import data.enumeration.Face;
import utils.FlixBlocksUtils;

public class DrawCubeFace extends Draw {

	// Reference to the "data"
	public ModelCube cube;

	public static TexturePack texturePack;

	// =================== Basic infos ==================
	int id;
	public Face face;
	Vector vx, vy, vz;
	Point3D[] points;

	public final static int DEFAULT_ALPHA = -1;
	int forcedAlpha;

	TextureSquare texture;
	int cols, rows;
	Point3D[] tab3D;
	Point[] tab2D;

	// =========================================================================================================================

	public DrawCubeFace(int id, Face face, Vector vx, Vector vy, Vector vz, Point3D[] points, Point3D center, int index,
			ModelCube cube) {
		this(id, face, vx, vy, vz, points, center, index, cube, DEFAULT_ALPHA);
	}

	public DrawCubeFace(int id, Face face, Vector vx, Vector vy, Vector vz, Point3D[] points, Point3D center, int index,
			ModelCube cube, int forcedAlpha) {
		this.id = id;
		this.face = face;
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.points = points;
		this.center = center;
		this.index = index;

		this.cube = cube;

		this.forcedAlpha = forcedAlpha;
	}

	// =========================================================================================================================

	@Override
	public ArrayList<Quadri> getQuadri() {
		quadri.clear();

		texture = texturePack.getFace(id, face, cube.rotation, cube.orientation);

		if (cube.miningState != FlixBlocksUtils.NO_MINING)
			texture = texture.miningFusion(texturePack.getMiningFrame(cube.miningState));

		cols = texture.width;
		rows = texture.height;
		int cols1 = cols + 1, rows1 = rows + 1;

		tab3D = new Point3D[rows1 * cols1];
		tab2D = new Point[rows1 * cols1];

		for (int row = 0; row <= rows; row++)
			for (int col = 0; col <= cols; col++)
				switch (face) {
				case UP:
					tab3D[row * cols1 + col] = vx.multiply(vz.multiply(points[4], col), row);
					break;
				case DOWN:
					tab3D[row * cols1 + col] = vx.multiply(vz.multiply(points[2], col), -row);
					break;
				case EAST:
					tab3D[row * cols1 + col] = vy.multiply(vx.multiply(points[1], col), row);
					break;
				case WEST:
					tab3D[row * cols1 + col] = vy.multiply(vx.multiply(points[2], -col), row);
					break;
				case SOUTH:
					tab3D[row * cols1 + col] = vy.multiply(vz.multiply(points[0], col), row);
					break;
				case NORTH:
					tab3D[row * cols1 + col] = vy.multiply(vz.multiply(points[3], -col), row);
					break;
				}

		for (int row = 0; row <= rows; row++)
			for (int col = 0; col <= cols; col++)
				tab2D[row * cols1 + col] = engine.to2D(tab3D[row * cols1 + col]);

		// Draw the black contour of the face
		quadri.add(new Quadri(tab2D[0], tab2D[cols], tab2D[rows1 * cols + rows], tab2D[rows * cols1], -0xffffff,
				StatePixel.CONTOUR, false));

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				// If the quadri is invisible -> next
				if (texture.getAlpha(row, col) == 0)
					continue;

				int color = texture.getColor(row, col);

				// If the cube is highlighted : its color will be lighter
				if (cube.isHighlight())
					color = texture.lighter(color, 75);

				// If the cube is invalid : its color will have a red hue
				if (cube.isPreview() && cube.multibloc != null && !cube.multibloc.valid)
					color = Engine.mix(color, -0x00ffff);

				StatePixel state = StatePixel.FILL;
				int alpha = forcedAlpha == DEFAULT_ALPHA ? texture.getAlpha(row, col) : forcedAlpha;

				if (0 < alpha && alpha < 255)
					state = StatePixel.TRANSPARENT;

				quadri.add(new Quadri(tab2D[row * cols1 + col], tab2D[(row + 1) * cols1 + col],
						tab2D[(row + 1) * cols1 + col + 1], tab2D[row * cols1 + col + 1], color, state, true));
			}
		return quadri;
	}
}
