package environment.extendsEngine;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import data.id.ItemTableClient;
import data.map.Cube;
import data.map.enumerations.Face;
import environment.extendsData.CubeClient;
import environment.textures.TextureSquare;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import graphicEngine.calcul.Quadri;
import graphicEngine.calcul.Vector;
import graphicEngine.structures.Drawable;

public class DrawCubeFace extends Drawable {

	/** Reference to the "data" */
	public CubeClient cube;
	/** Face to draw */
	public Face face;

	public final static int DEFAULT_ALPHA = -1;
	private int forcedAlpha;

	// =========================================================================================================================

	public DrawCubeFace(CubeClient cube, Face face, Point3D center, int index, int forcedAlpha) {
		this.cube = cube;
		this.face = face;
		this.center = center;
		this.index = index;

		this.forcedAlpha = forcedAlpha;
	}

	public DrawCubeFace(CubeClient cube, Face face, Point3D center, int index) {
		this(cube, face, center, index, DEFAULT_ALPHA);
	}

	// =========================================================================================================================

	@Override
	public ArrayList<Quadri> getQuadri(Engine engine) {
		ArrayList<Quadri> quadri = new ArrayList<>();

		if (cube.hasLayer())
			for (DrawLayer layer : cube.getLayers())
				if (layer != null)
					quadri.addAll(layer.getQuadri(this, engine));

		if (ItemTableClient.drawContour(cube.getItemID())) {
			DrawLayer contour = new DrawLayer(cube, face);
			contour.drawContour(ItemTableClient.getTexturePack().getFace(cube, face).width, 0xff000000);
			quadri.addAll(contour.getQuadri(this, engine));
		}

		// Draw the mining animation
		if (cube.miningState != Cube.NO_MINING)
			generateQuadri(ItemTableClient.getTexturePack().getMiningFrame(cube.miningState), engine, quadri);

		// Draw the cube
		generateQuadri(ItemTableClient.getTexturePack().getFace(cube, face), engine, quadri);

		return quadri;
	}

	// =========================================================================================================================

	/**
	 * Generates and add the quadri, from the texture, to the list of quadri
	 */
	public void generateQuadri(TextureSquare texture, Engine engine, ArrayList<Quadri> quadri) {
		int cols1 = texture.width + 1;

		Point[] points2D = generate2D(engine, texture.width, texture.height);

		// TODO [Improve] Highlight darker when white face/quadri
		int sample = texture.getColor(0, 0);
		boolean darker = (sample & 0xff) + (sample >> 8 & 0xff) + (sample >> 16 & 0xff) > 600;

		boolean isFaceSelected = cube.selectedQuadri != CubeClient.NO_QUADRI && cube.selectedFace == face;

		for (int row = 0; row < texture.height; row++)
			for (int col = 0; col < texture.width; col++) {
				int color = texture.getColor(row, col);
				boolean selected = isFaceSelected && (row * texture.width + col) == cube.selectedQuadri;

				// If the quadri is invisible -> ignore color modifications
				if (((color >> 24) & 0xff) == 0) {
					if (selected)
						color = 0xffcccccc;
				} else {
					// Remplace default alpha by the forced one
					if (forcedAlpha != DEFAULT_ALPHA)
						color = (forcedAlpha << 24) + (color & 0xffffff);

					// If the cube is highlighted : its color will be lighter
					if (cube.isHighlight() || selected)
						if (darker)
							color = Engine.addHue(color, Engine.createColor(255, 0, 0, 0), .4);
						else
							color = Engine.addHue(color, Engine.createColor(255, 255, 255, 255), .4);

					// If the cube is preview (or building in construction) makes it transparent
					if (cube.isPreview() || (cube.build != null && !cube.build.isBuild())) {
						color = (127 << 24) + (color & 0xffffff);
						// If the multibloc is invalid : its color will have a red hue
						if (cube.multibloc != null && !cube.multibloc.valid)
							color = Engine.addHue(color, Engine.createColor(255, 255, 0, 0), .4);
					}
				}

				quadri.add(new Quadri(points2D[row * cols1 + col], points2D[(row + 1) * cols1 + col],
						points2D[(row + 1) * cols1 + col + 1], points2D[row * cols1 + col + 1], color, true,
						row * texture.width + col));
			}
	}

	/**
	 * Returns an array of the 2D points of the face with a [cols x rows] resolution
	 */
	public Point[] generate2D(Engine engine, int cols, int rows) {
		Point[] points2D = new Point[(rows + 1) * (cols + 1)];

		Vector vx = cube.vx.divise(face == Face.UP || face == Face.DOWN ? rows : cols);
		Vector vy = cube.vy.divise(rows);
		Vector vz = cube.vz.divise(cols);

		for (int row = 0; row <= rows; row++)
			for (int col = 0; col <= cols; col++)
				points2D[row * (cols + 1) + col] = engine.to2D(getPoint3D(vx, vy, vz, col, row));

		return points2D;
	}

	/**
	 * Returns the 3D point (col, row) of the current face
	 * 
	 * /!\ vx,vy,vz must have been divised by the resolution of the face
	 */
	public Point3D getPoint3D(Vector vx, Vector vy, Vector vz, int col, int row) {
		switch (face) {
		case UP:
			return vx.multiply(vz.multiply(cube.points[4], col), row);
		case DOWN:
			return vx.multiply(vz.multiply(cube.points[2], col), -row);
		case EAST:
			return vy.multiply(vx.multiply(cube.points[1], col), row);
		case WEST:
			return vy.multiply(vx.multiply(cube.points[2], -col), row);
		case SOUTH:
			return vy.multiply(vz.multiply(cube.points[0], col), row);
		case NORTH:
			return vy.multiply(vz.multiply(cube.points[3], -col), row);
		default:
			return null;
		}
	}

	// =========================================================================================================================

	@Override
	public Polygon getPoly(Engine engine) {
		Point[] points = generate2D(engine, 1, 1);
		return new Polygon(new int[] { points[0].x, points[1].x, points[3].x, points[2].x },
				new int[] { points[0].y, points[1].y, points[3].y, points[2].y }, 4);
	}

	@Override
	public boolean appearIn(Engine engine, int imgWidth, int imgHeight) {
		Polygon poly = getPoly(engine);
		for (int index = 0; index < 4; index++)
			if (poly.xpoints[index] < imgWidth && poly.xpoints[index] > 0 && poly.ypoints[index] < imgHeight
					&& poly.ypoints[index] > 0)
				return true;
		return false;
	}

	@Override
	public boolean isTargetable() {
		return cube.isTargetable();
	}
}
