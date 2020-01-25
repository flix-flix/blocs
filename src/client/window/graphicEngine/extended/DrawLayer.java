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

	static TextureSquare[] letters = new TextureSquare[26];

	static {
		TextureSquare alphabet = TextureSquare.generateSquare("static/alphabet");

		int start = 0;

		for (char c = 'A'; c <= 'Z'; c++) {
			int width = 3;

			// Look after the red end-line
			while (start + width + 1 < alphabet.width && alphabet.getColor(0, start + width) != -65536)
				width++;

			letters[c - 'A'] = alphabet.getRect(start, 0, width, 7);
			start += width + 1;
		}
	}

	public static TextureSquare getLetter(char c) {
		return letters[Character.toUpperCase(c) - 'A'];
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

		dataList.add(new Data(Type.LINE, col1, row1, col2, row2, true, 4, color));
	}

	public void drawCross(int col, int row, int color) {
		col = col * 4 + 2;
		row = row * 4 + 2;

		dataList.add(new Data(Type.LINE, col - 1, row, col + 1, row, true, 4, color));
		dataList.add(new Data(Type.LINE, col, row - 1, col, row + 1, true, 4, color));
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
		dataList.add(new Data(Type.LINE, col1, row1, col2, row2, true, 1, color, face));
	}

	public void drawLine(int col1, int row1, int col2, int row2, int color) {
		dataList.add(new Data(Type.LINE, col1, row1, col2, row2, true, 1, color));
	}

	public void drawSquare(int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
		drawLine(col1, row1, col1, row2, color);
		drawLine(col1, row2, col2, row2, color);
		drawLine(col2, row2, col2, row1, color);
		drawLine(col2, row1, col1, row1, color);
	}

	public void fillSquare(int col, int row, int color, boolean sizeRelative, int size, int fly) {
		dataList.add(new Data(Type.SQUARE, col, row, col + 1, row + 1, sizeRelative, size, color, face, fly));
	}

	// =========================================================================================================================

	public void drawDottedLinePixel(int col1, int row1, boolean verti, int color1, int color2, Face face) {
		col1 = col1 * 2;
		row1 = row1 * 2;

		if (verti) {
			dataList.add(new Data(Type.LINE, col1, row1, col1, row1 + 1, true, 2, color1, face));
			dataList.add(new Data(Type.LINE, col1, row1 + 1, col1, row1 + 2, true, 2, color2, face));
		} else {
			dataList.add(new Data(Type.LINE, col1, row1, col1 + 1, row1, true, 2, color1, face));
			dataList.add(new Data(Type.LINE, col1 + 1, row1, col1 + 2, row1, true, 2, color2, face));
		}
	}

	/** Must be horizontal or vertical (no diagonal) */
	public void drawDottedLine(int col1, int row1, boolean verti, int coord2, int color1, int color2, Face face) {
		if (verti)
			for (int row = row1; row < coord2; row++)
				drawDottedLinePixel(col1, row, verti, color1, color2, face);
		else
			for (int col = col1; col < coord2; col++)
				drawDottedLinePixel(col, row1, verti, color1, color2, face);
	}

	public void drawDottedSquare(int c1, int r1, int c2, int r2, int color1, int color2, Face face) {
		int col1 = Math.min(c1, c2);
		int row1 = Math.min(r1, r2);
		int col2 = Math.max(c1, c2) + 1;
		int row2 = Math.max(r1, r2) + 1;

		drawDottedLine(col1, row1, true, row2, color1, color2, face);
		drawDottedLine(col2, row1, true, row2, color1, color2, face);
		drawDottedLine(col1, row1, false, col2, color1, color2, face);
		drawDottedLine(col1, row2, false, col2, color1, color2, face);
	}

	// =========================================================================================================================

	public void drawGrid() {
		TextureSquare texture = cube.texturePack.getFace(cube.itemID, face);

		for (int row = 0; row <= texture.height; row++)
			drawLine(0, row, texture.width, row, 0xffffffff, face);
		for (int col = 0; col <= texture.width; col++)
			drawLine(col, 0, col, texture.height, 0xffffffff, face);
	}

	public void drawString(String str) {
		TextureSquare[] text = new TextureSquare[str.length()];
		int size = 6 + text.length - 1;// 3 before / 3 after / 1 between each letter

		for (int i = 0; i < text.length; i++) {
			text[i] = getLetter(str.charAt(i));
			size += text[i].width;
		}

		int start = 3;
		for (int i = 0; i < text.length; i++) {
			TextureSquare letter = getLetter(str.charAt(i));
			for (int col = 0; col < letter.width; col++) {
				for (int row = 0; row < letter.height; row++)
					if (letter.getColor(row, col) == 0xff000000)
						fillSquare(start + col, size / 2 - 3 + row, 0xffc86400, false, size, 2);
				// dataList.add(new Data(Type.SQUARE, start + col, size / 2 - 3 + row, start +
				// col + 1,
				// size / 2 - 3 + row + 1, false, size, 0xffc86400, face, 2));
			}
			start += 1 + letter.width;
		}
	}

	public void drawFace() {
		drawString(face.name());
	}

	// =========================================================================================================================

	public ArrayList<Quadri> getQuadri(DrawCubeFace draw, Engine engine) {
		ArrayList<Quadri> quadris = new ArrayList<>();

		TextureSquare texture = cube.texturePack.getFace(cube, face);

		for (Data data : dataList) {
			int size = data.sizeRelative ? texture.width * data.size : data.size;

			if (data.face == Data.ALL_FACES || data.face == draw.face.ordinal())
				switch (data.type) {
				case SQUARE:
					quadris.add(new Quadri(getPoint2D(engine, size, data.col1, data.row1, data.fly),
							getPoint2D(engine, size, data.col1, data.row2, data.fly),
							getPoint2D(engine, size, data.col2, data.row2, data.fly),
							getPoint2D(engine, size, data.col2, data.row1, data.fly), data.color, true));
					break;
				case LINE:
					quadris.add(new Quadri(getPoint2D(engine, size, data.col1, data.row1, data.fly),
							getPoint2D(engine, size, data.col2, data.row2, data.fly),
							getPoint2D(engine, size, data.col2, data.row2, data.fly),
							getPoint2D(engine, size, data.col1, data.row1, data.fly), data.color, false));
					break;
				case CUBE:
					break;
				}
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
		Type type;
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

		public Data(Type type, int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color) {
			this.type = type;
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.sizeRelative = sizeRelative;
			this.size = size;
			this.color = color;
		}

		public Data(Type type, int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color,
				Face face) {
			this(type, col1, row1, col2, row2, sizeRelative, size, color);
			this.face = face.ordinal();
		}

		public Data(Type type, int col1, int row1, int col2, int row2, boolean sizeRelative, int size, int color,
				Face face, int fly) {
			this(type, col1, row1, col2, row2, sizeRelative, size, color, face);
			this.fly = fly;
		}
	}

	// =========================================================================================================================

	private enum Type {
		LINE, SQUARE, CUBE;
	}
}
