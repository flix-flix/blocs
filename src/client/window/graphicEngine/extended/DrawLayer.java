package client.window.graphicEngine.extended;

import java.awt.Point;
import java.util.ArrayList;

import client.textures.TextureSquare;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.calcul.Vector;
import client.window.graphicEngine.structures.Quadri;
import data.map.enumerations.Face;

public class DrawLayer {

	ArrayList<Data> dataList = new ArrayList<>();

	ModelCube cube;
	Face face;

	// =========================================================================================================================

	static TextureSquare[] faces = new TextureSquare[6];

	static {
		for (Face face : Face.faces)
			faces[face.ordinal()] = TextureSquare.generateSquare("menu/editor/faces", face.name().toLowerCase());
	}

	// =========================================================================================================================

	public DrawLayer(ModelCube cube, Face face) {
		this.cube = cube;
		this.face = face;
	}

	public void drawLine(int col1, int row1, int col2, int row2, int color) {
		col1 = col1 * 4 + 2;
		row1 = row1 * 4 + 2;
		col2 = col2 * 4 + 2;
		row2 = row2 * 4 + 2;

		dataList.add(new LineData(col1, row1, col2, row2, true, 4, color));
	}

	public void drawCross(int col, int row, int color) {
		col = col * 4 + 2;
		row = row * 4 + 2;

		dataList.add(new LineData(col - 1, row, col + 1, row, true, 4, color));
		dataList.add(new LineData(col, row - 1, col, row + 1, true, 4, color));
	}

	public void drawLineAndCross(int col1, int row1, int col2, int row2, int colorLine, int colorCross) {
		drawLine(col1, row1, col2, row2, colorLine);
		drawCross(col1, row1, colorCross);
		drawCross(col2, row2, colorCross);
	}

	public void drawSquare(int col1, int row1, int col2, int row2, int colorLine, int colorCross) {
		drawLine(col1, row1, col1, row2, colorLine);
		drawLine(col1, row2, col2, row2, colorLine);
		drawLine(col2, row2, col2, row1, colorLine);
		drawLine(col2, row1, col1, row1, colorLine);

		drawCross(col1, row1, colorCross);
		drawCross(col1, row2, colorCross);
		drawCross(col2, row2, colorCross);
		drawCross(col2, row1, colorCross);
	}

	public void fillSquare(int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
		dataList.add(new SquareData(col1, row1, col2, row2, sizeRelative, size, color));
	}

	public void drawFace() {
		for (int row = 0; row < 16; row++)
			for (int col = 0; col < 16; col++)
				if (faces[face.ordinal()].getColor(row, col) == 0xff000000)
					fillSquare(col, row, col + 1, row + 1, false, 16, 0xffcccccc);
	}

	// =========================================================================================================================

	public ArrayList<Quadri> getQuadri(TextureSquare texture, Engine engine) {
		ArrayList<Quadri> quadris = new ArrayList<>();

		for (Data data : dataList) {
			int size = data.sizeRelative ? texture.width * data.size : data.size;

			if (data instanceof LineData)
				quadris.add(new Quadri(getPoint2D(engine, size, data.col1, data.row1),
						getPoint2D(engine, size, data.col2, data.row2), getPoint2D(engine, size, data.col2, data.row2),
						getPoint2D(engine, size, data.col1, data.row1), data.color, false, -1));
			else
				quadris.add(new Quadri(getPoint2D(engine, size, data.col1, data.row1),
						getPoint2D(engine, size, data.col1, data.row2), getPoint2D(engine, size, data.col2, data.row2),
						getPoint2D(engine, size, data.col2, data.row1), data.color, true, -1));
		}

		return quadris;
	}

	/** Generates the on-screen Point */
	public Point getPoint2D(Engine engine, int size, int col, int row) {
		return engine.to2D(getPoint3D(size, col, row));
	}

	/**
	 * Returns the 3D point (col, row) of the current face
	 * 
	 * /!\ vx,vy,vz must have been divised by the resolution of the face
	 */
	public Point3D getPoint3D(int size, int col, int row) {
		Vector vx = cube.vx.divise(face == Face.UP || face == Face.DOWN ? size : size);// rows/cols
		Vector vy = cube.vy.divise(size);// rows
		Vector vz = cube.vz.divise(size);// cols

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

	private abstract class Data {
		int row1, col1, row2, col2;
		int color;
		int size;

		/**
		 * true : There is "size" more times rows in the layer than in the original
		 * texture
		 * 
		 * false : There is "size" rows in the layer
		 */
		boolean sizeRelative = true;

		public Data(int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.sizeRelative = sizeRelative;
			this.size = size;
			this.color = color;
		}
	}

	private class LineData extends Data {
		public LineData(int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
			super(col1, row1, col2, row2, sizeRelative, size, color);
		}
	}

	private class SquareData extends Data {
		public SquareData(int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
			super(col1, row1, col2, row2, sizeRelative, size, color);
		}
	}
}
