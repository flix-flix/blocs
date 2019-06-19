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
import client.window.graphicEngine.models.ModelCube;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Quadri;
import data.ItemTable;
import data.enumeration.Face;
import utils.FlixBlocksUtils;

public class DrawCubeFace extends Draw {

	// Reference to the "data"
	public ModelCube cube;

	public static TexturePack texturePack;

	// =================== Basic infos ==================
	int id;
	Face face;
	Vector vx, vy, vz;
	Point3D[] points;

	public final static int DEFAULT_ALPHA = -1;
	int forcedAlpha;

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
			texture = texture.miningFusion(texturePack.getMiningFrame(cube.miningState));

		int cols = texture.color[0].length;
		int rows = texture.color.length;

		Point3D[][] tab3D = new Point3D[rows + 1][cols + 1];
		Point[][] tab2D = new Point[rows + 1][cols + 1];

		for (int i = 0; i <= rows; i++)
			for (int j = 0; j <= cols; j++)
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

		for (int i = 0; i <= rows; i++)
			for (int j = 0; j <= cols; j++)
				tab2D[i][j] = engine.to2D(tab3D[i][j]);

		if (ItemTable.drawContour(cube.itemID))
			if (cube.isPreview())
				quadri.add(new Quadri(tab2D[0][0], tab2D[0][cols], tab2D[rows][cols], tab2D[rows][0], -0xffffff,
						cube.isPreviewThrought() ? StatePixel.PREVIEW_THROUGHT : StatePixel.PREVIEW, false));
			else
				quadri.add(new Quadri(tab2D[0][0], tab2D[0][cols], tab2D[rows][cols], tab2D[rows][0], -0xffffff,
						StatePixel.CONTOUR, false));

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				int color = texture.getColor(row, col);

				// If the cube is highlighted : its color will be lighter
				if (cube.isHighlight())
					color = texture.lighter(color, 75);

				// If the cube is invalid : its color will have a red hue
				if (cube.isPreview() && cube.multibloc != null && !cube.multibloc.valid)
					color = Engine.mix(color, -0x00ffff);

				StatePixel state = StatePixel.FILL;
				int alpha = forcedAlpha == DEFAULT_ALPHA ? texture.getAlpha(row, col) : forcedAlpha;

				if (alpha == 0)
					state = StatePixel.INVISIBLE;
				else if (alpha != 255)
					state = StatePixel.TRANSPARENT;

				if (cube.isPreview()) {
					state = StatePixel.PREVIEW;
					if (cube.isPreviewThrought())
						state = StatePixel.PREVIEW_THROUGHT;
				}

				quadri.add(new Quadri(tab2D[row][col], tab2D[row + 1][col], tab2D[row + 1][col + 1],
						tab2D[row][col + 1], color, state));
			}
		return quadri;
	}
}
