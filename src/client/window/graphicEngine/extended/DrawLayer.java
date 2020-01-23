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
			faces[face.ordinal()] = TextureSquare.generateSquare("static/faces/" + face.name().toLowerCase());
	}

	// =========================================================================================================================

	public DrawLayer(ModelCube cube, Face face) {
		this.cube = cube;
		this.face = face;
	}

	// =========================================================================================================================

	public void drawLineMiddle(int col1, int row1, int col2, int row2, int color) {
		col1 = col1 * 4 + 2;
		row1 = row1 * 4 + 2;
		col2 = col2 * 4 + 2;
		row2 = row2 * 4 + 2;

		dataList.add(new Data(false, col1, row1, col2, row2, true, 4, color));
	}

	public void drawCross(int col, int row, int color) {
		col = col * 4 + 2;
		row = row * 4 + 2;

		dataList.add(new Data(false, col - 1, row, col + 1, row, true, 4, color));
		dataList.add(new Data(false, col, row - 1, col, row + 1, true, 4, color));
	}

	public void drawLineAndCross(int col1, int row1, int col2, int row2, int colorLine, int colorCross) {
		drawLineMiddle(col1, row1, col2, row2, colorLine);
		drawCross(col1, row1, colorCross);
		drawCross(col2, row2, colorCross);
	}

	public void drawSquareAndCross(int col1, int row1, int col2, int row2, int colorLine, int colorCross) {
		drawLineMiddle(col1, row1, col1, row2, colorLine);
		drawLineMiddle(col1, row2, col2, row2, colorLine);
		drawLineMiddle(col2, row2, col2, row1, colorLine);
		drawLineMiddle(col2, row1, col1, row1, colorLine);

		drawCross(col1, row1, colorCross);
		drawCross(col1, row2, colorCross);
		drawCross(col2, row2, colorCross);
		drawCross(col2, row1, colorCross);
	}

	// =========================================================================================================================

	public void drawLine(int col1, int row1, int col2, int row2, int color, Face face) {
		dataList.add(new Data(false, col1, row1, col2, row2, true, 1, color, face));
	}

	public void drawLine(int col1, int row1, int col2, int row2, int color) {
		dataList.add(new Data(false, col1, row1, col2, row2, true, 1, color));
	}

	public void drawSquare(int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
		drawLine(col1, row1, col1, row2, color);
		drawLine(col1, row2, col2, row2, color);
		drawLine(col2, row2, col2, row1, color);
		drawLine(col2, row1, col1, row1, color);
	}

	public void drawFace() {
		for (int row = 0; row < 16; row++)
			for (int col = 0; col < 16; col++)
				if (faces[face.ordinal()].getColor(row, col) == 0xff000000)
					dataList.add(new Data(true, col, row, col + 1, row + 1, false, 16, 0xff762277, face, 2));
	}

	public void drawGrid() {
		TextureSquare texture = cube.texturePack.getFace(cube.itemID, face);

		for (int row = 0; row <= texture.height; row++)
			drawLine(0, row, texture.width, row, 0xffffffff, face);
		for (int col = 0; col <= texture.width; col++)
			drawLine(col, 0, col, texture.height, 0xffffffff, face);
	}

	// =========================================================================================================================

	public ArrayList<Quadri> getQuadri(DrawCubeFace draw, Engine engine) {
		ArrayList<Quadri> quadris = new ArrayList<>();

		TextureSquare texture = cube.texturePack.getFace(cube, face);

		for (Data data : dataList) {
			int size = data.sizeRelative ? texture.width * data.size : data.size;

			if (data.face == Data.ALL_FACES || data.face == draw.face.ordinal())
				if (data.square)
					quadris.add(new Quadri(getPoint2D(engine, size, data.col1, data.row1, data.fly),
							getPoint2D(engine, size, data.col1, data.row2, data.fly),
							getPoint2D(engine, size, data.col2, data.row2, data.fly),
							getPoint2D(engine, size, data.col2, data.row1, data.fly), data.color, true));
				else
					quadris.add(new Quadri(getPoint2D(engine, size, data.col1, data.row1, data.fly),
							getPoint2D(engine, size, data.col2, data.row2, data.fly),
							getPoint2D(engine, size, data.col2, data.row2, data.fly),
							getPoint2D(engine, size, data.col1, data.row1, data.fly), data.color, false));
		}

		return quadris;
	}

	/** Generates the on-screen Point */
	public Point getPoint2D(Engine engine, int size, int col, int row, int fly) {
		Vector vx = cube.vx.divise(face == Face.UP || face == Face.DOWN ? size : size);// rows/cols
		Vector vy = cube.vy.divise(size);// rows
		Vector vz = cube.vz.divise(size);// cols

		Point3D p = null;

		switch (face) {
		case UP:
			p = vy.multiply(vx.multiply(vz.multiply(cube.points[4], col), row), fly);
			break;
		case DOWN:
			p = vy.multiply(vx.multiply(vz.multiply(cube.points[2], col), -row), -fly);
			break;
		case EAST:
			p = vz.multiply(vy.multiply(vx.multiply(cube.points[1], col), row), fly);
			break;
		case WEST:
			p = vz.multiply(vy.multiply(vx.multiply(cube.points[2], -col), row), -fly);
			break;
		case SOUTH:
			p = vx.multiply(vy.multiply(vz.multiply(cube.points[0], col), row), -fly);
			break;
		case NORTH:
			p = vx.multiply(vy.multiply(vz.multiply(cube.points[3], -col), row), fly);
			break;
		}

		return engine.to2D(p);
	}

	// =========================================================================================================================

	private class Data {
		/** true : square | false : line */
		boolean square;
		int row1, col1, row2, col2;
		int color;
		int size;
		/** Number of pixel above the face */
		int fly = 0;

		/**
		 * true : There is "size" more times rows in the layer than in the original
		 * texture
		 * 
		 * false : There is "size" rows in the layer
		 */
		boolean sizeRelative = true;

		static final int ALL_FACES = -1;
		int face = ALL_FACES;

		public Data(boolean square, int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
			this.square = square;
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.sizeRelative = sizeRelative;
			this.size = size;
			this.color = color;
		}

		public Data(boolean square, int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color,
				Face face) {
			this(square, col1, row1, col2, row2, sizeRelative, size, color);
			this.face = face.ordinal();
		}

		public Data(boolean square, int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color,
				Face face, int fly) {
			this(square, col1, row1, col2, row2, sizeRelative, size, color, face);
			this.fly = fly;
		}
	}
}
