package environment.extendsEngine;

import java.awt.Point;
import java.util.ArrayList;

import data.id.ItemTableClient;
import data.map.enumerations.Face;
import environment.extendsData.CubeClient;
import environment.textures.TextureSquare;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import graphicEngine.calcul.Quadri;
import graphicEngine.calcul.Vector;

public class DrawLayer {

	private ArrayList<Data> dataList = new ArrayList<>();

	private CubeClient cube;
	private Face face;

	// Path
	private ArrayList<Face> connectedPath;

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

	public DrawLayer(CubeClient cube, Face face) {
		this.cube = cube;
		this.face = face;
	}

	// =========================================================================================================================

	public void drawLineMiddle(int x1, int y1, int x2, int y2, int color) {
		x1 = x1 * 4 + 2;
		y1 = y1 * 4 + 2;
		x2 = x2 * 4 + 2;
		y2 = y2 * 4 + 2;

		dataList.add(new Data(Type.LINE, x1, y1, x2, y2, true, 4, color));
	}

	public void drawCross(int x, int y, int color) {
		x = x * 4 + 2;
		y = y * 4 + 2;

		dataList.add(new Data(Type.LINE, x - 1, y, x + 1, y, true, 4, color));
		dataList.add(new Data(Type.LINE, x, y - 1, x, y + 1, true, 4, color));
	}

	public void drawLineAndCross(int x1, int y1, int x2, int y2, int colorLine, int colorCross) {
		drawLineMiddle(x1, y1, x2, y2, colorLine);
		drawCross(x1, y1, colorCross);
		drawCross(x2, y2, colorCross);
	}

	public void drawSquareAndCross(int x1, int y1, int x2, int y2, int colorLine, int colorCross) {
		drawLineMiddle(x1, y1, x1, y2, colorLine);
		drawLineMiddle(x1, y2, x2, y2, colorLine);
		drawLineMiddle(x2, y2, x2, y1, colorLine);
		drawLineMiddle(x2, y1, x1, y1, colorLine);

		drawCross(x1, y1, colorCross);
		drawCross(x1, y2, colorCross);
		drawCross(x2, y2, colorCross);
		drawCross(x2, y1, colorCross);
	}

	// =========================================================================================================================

	public void drawLine(int x1, int y1, int x2, int y2, int color, Face face) {
		dataList.add(new Data(Type.LINE, x1, y1, x2, y2, true, 1, color, face));
	}

	public void drawLine(int x1, int y1, int x2, int y2, int color) {
		dataList.add(new Data(Type.LINE, x1, y1, x2, y2, true, 1, color));
	}

	public void drawSquare(int x1, int y1, int x2, int y2, boolean sizeRelative, int size, int color) {
		drawLine(x1, y1, x1, y2, color);
		drawLine(x1, y2, x2, y2, color);
		drawLine(x2, y2, x2, y1, color);
		drawLine(x2, y1, x1, y1, color);
	}

	public void fillSquare(int x, int y, int color, boolean sizeRelative, int size, int fly) {
		dataList.add(new Data(Type.SQUARE, x, y, x + 1, y + 1, sizeRelative, size, color, face, fly));
	}

	// =========================================================================================================================

	public void drawContour(int size, int color) {
		drawLine(0, 0, size, 0, color);
		drawLine(0, 0, 0, size, color);
		drawLine(0, size, size, size, color);
		drawLine(size, 0, size, size, color);

		// Contour quadri by quadri
		// for (int i = 0; i < size; i++) {
		// drawLine(i, 0, i + 1, 0, color);
		// drawLine(0, i, 0, i + 1, color);
		// drawLine(i, size, i + 1, size, color);
		// drawLine(size, i, size, i + 1, color);
		// }
	}

	// =========================================================================================================================

	public void drawDottedLinePixel(int x1, int y1, boolean verti, int color1, int color2, Face face) {
		x1 = x1 * 2;
		y1 = y1 * 2;

		if (verti) {
			dataList.add(new Data(Type.LINE, x1, y1, x1, y1 + 1, true, 2, color1, face));
			dataList.add(new Data(Type.LINE, x1, y1 + 1, x1, y1 + 2, true, 2, color2, face));
		} else {
			dataList.add(new Data(Type.LINE, x1, y1, x1 + 1, y1, true, 2, color1, face));
			dataList.add(new Data(Type.LINE, x1 + 1, y1, x1 + 2, y1, true, 2, color2, face));
		}
	}

	/** Must be horizontal or vertical (no diagonal) */
	public void drawDottedLine(int x1, int y1, boolean verti, int coord2, int color1, int color2, Face face) {
		if (verti)
			for (int y = y1; y < coord2; y++)
				drawDottedLinePixel(x1, y, verti, color1, color2, face);
		else
			for (int x = x1; x < coord2; x++)
				drawDottedLinePixel(x, y1, verti, color1, color2, face);
	}

	public void drawDottedSquare(int c1, int r1, int c2, int r2, int color1, int color2, Face face) {
		int x1 = Math.min(c1, c2);
		int y1 = Math.min(r1, r2);
		int x2 = Math.max(c1, c2) + 1;
		int y2 = Math.max(r1, r2) + 1;

		drawDottedLine(x1, y1, true, y2, color1, color2, face);
		drawDottedLine(x2, y1, true, y2, color1, color2, face);
		drawDottedLine(x1, y1, false, x2, color1, color2, face);
		drawDottedLine(x1, y2, false, x2, color1, color2, face);
	}

	// =========================================================================================================================

	public void drawGrid() {
		TextureSquare texture = ItemTableClient.getTexturePack().getFace(cube.getItemID(), face);

		for (int y = 0; y <= texture.height; y++)
			drawLine(0, y, texture.width, y, 0xffffffff, face);
		for (int x = 0; x <= texture.width; x++)
			drawLine(x, 0, x, texture.height, 0xffffffff, face);
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
			for (int x = 0; x < letter.width; x++)
				for (int y = 0; y < letter.height; y++)
					if (letter.getColor(y, x) == 0xff000000)
						fillSquare(start + x, size / 2 - 3 + y, 0xffc86400, false, size, size / 5);
			start += 1 + letter.width;
		}
	}

	public void drawFace() {
		drawString(face.name());
	}

	// =========================================================================================================================

	public void drawPathEnd() {
		for (int x = 3; x < 7; x++)
			for (int y = 3; y < 7; y++)
				fillSquare(x, y, 0xff0000ff, false, 10, -3);
	}

	public void drawPath() {
	}

	public void connectPath(Face connect) {
		if (connectedPath == null)
			connectedPath = new ArrayList<>();

		connectedPath.add(connect);
	}

	// =========================================================================================================================

	public ArrayList<Quadri> getQuadri(DrawCubeFace draw, Engine engine) {
		ArrayList<Quadri> quadris = new ArrayList<>();

		TextureSquare texture = ItemTableClient.getTexturePack().getFace(cube, face);

		for (Data data : dataList) {
			int size = data.sizeRelative ? texture.width * data.size : data.size;

			if (data.face == Data.ALL_FACES || data.face == draw.face.ordinal())
				switch (data.type) {
				case SQUARE:
					quadris.add(new Quadri(getPoint2D(engine, size, data.x1, data.y1, data.fly),
							getPoint2D(engine, size, data.x1, data.y2, data.fly),
							getPoint2D(engine, size, data.x2, data.y2, data.fly),
							getPoint2D(engine, size, data.x2, data.y1, data.fly), data.color, true));
					break;
				case LINE:
					// TODO [Improve] Long line are in front and behind the camera (point in (0,0))
					quadris.add(new Quadri(getPoint2D(engine, size, data.x1, data.y1, data.fly),
							getPoint2D(engine, size, data.x2, data.y2, data.fly), data.color));
					break;
				case CUBE:
					break;
				}
		}

		return quadris;
	}

	/** Generates the on-screen Point */
	public Point getPoint2D(Engine engine, int size, int x, int y, int fly) {
		Vector vx = cube.vx.divise(face == Face.UP || face == Face.DOWN ? size : size);// rows/cols
		Vector vy = cube.vy.divise(size);// rows
		Vector vz = cube.vz.divise(size);// cols

		Point3D p = null;

		switch (face) {
		case UP:
			p = vy.multiply(vx.multiply(vz.multiply(cube.points[4], x), y), fly);
			break;
		case DOWN:
			p = vy.multiply(vx.multiply(vz.multiply(cube.points[2], x), -y), -fly);
			break;
		case EAST:
			p = vz.multiply(vy.multiply(vx.multiply(cube.points[1], x), y), fly);
			break;
		case WEST:
			p = vz.multiply(vy.multiply(vx.multiply(cube.points[2], -x), y), -fly);
			break;
		case SOUTH:
			p = vx.multiply(vy.multiply(vz.multiply(cube.points[0], x), y), -fly);
			break;
		case NORTH:
			p = vx.multiply(vy.multiply(vz.multiply(cube.points[3], -x), y), fly);
			break;
		}

		return engine.to2D(p);
	}

	// =========================================================================================================================

	private class Data {
		Type type;
		int y1, x1, y2, x2;
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

		public Data(Type type, int x1, int y1, int x2, int y2, boolean sizeRelative, int size, int color) {
			this.type = type;
			this.y1 = y1;
			this.x1 = x1;
			this.y2 = y2;
			this.x2 = x2;
			this.sizeRelative = sizeRelative;
			this.size = size;
			this.color = color;
		}

		public Data(Type type, int x1, int y1, int x2, int y2, boolean sizeRelative, int size, int color, Face face) {
			this(type, x1, y1, x2, y2, sizeRelative, size, color);
			this.face = face.ordinal();
		}

		public Data(Type type, int x1, int y1, int x2, int y2, boolean sizeRelative, int size, int color, Face face,
				int fly) {
			this(type, x1, y1, x2, y2, sizeRelative, size, color, face);
			this.fly = fly;
		}
	}

	// =========================================================================================================================

	private enum Type {
		LINE, SQUARE, CUBE;
	}
}
