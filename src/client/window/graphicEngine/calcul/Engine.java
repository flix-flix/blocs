package client.window.graphicEngine.calcul;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import client.textures.TexturePack;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import client.window.graphicEngine.structures.Quadri;
import data.enumeration.Face;
import data.map.Cube;

public class Engine {

	public static final double toRadian = Math.PI / 180;

	public static TexturePack texturePack;

	// Contains the data to be drawn
	public Model model;
	// The point of view and inclinations
	public Camera camera;

	// TODO [Improve] Set the view angle in function of the frame size
	// View angles (degrees)
	public int vueX = 60, vueY = 45;

	// ================ Target =====================
	public int cursorX = 100, cursorY = 100;

	static public Cube cubeTarget;
	static public Face faceTarget;

	// ================ Target (Temp) =====================
	static public Cube cubeTargetTemp;
	static public Face faceTargetTemp;

	// ================ Dev (F3) =====================
	public long timeInit = 0, timeMat = 0, timeDraw = 0, timePixel;
	public int nbCubes = 0, nbChunks = 0;

	// ================ Data =====================
	private Matrix matrice;

	private BufferedImage bimg;
	private DataBuffer dataBuffer;
	// Mark the already filled pixels
	private StatePixel[] statePixel;

	public int screenWidth = 100, screenHeight = 100, centerX, centerY;
	public double[] vue = new double[2];

	// =========================================================================================================================

	public Engine() {
	}

	// =========================================================================================================================

	public BufferedImage getImage(int w, int h) {
		if (screenWidth != w || screenHeight != h)
			init(w, h);

		matrice = new Matrix(-camera.getVx(), -camera.getVy(), camera.vue);

		Arrays.fill(statePixel, StatePixel.EMPTY);
		drawSky();

		cubeTarget = null;
		faceTarget = null;

		cubeTargetTemp = null;
		faceTargetTemp = null;

		timeInit = System.currentTimeMillis();

		draw();

		return bimg;
	}

	public void init(int w, int h) {
		screenWidth = w;
		screenHeight = h;

		vue[0] = Math.tan(vueX * toRadian);
		vue[1] = Math.tan(vueY * toRadian);

		centerX = screenWidth / 2;
		centerY = screenHeight / 2;

		bimg = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		dataBuffer = bimg.getRaster().getDataBuffer();

		statePixel = new StatePixel[screenWidth * screenHeight];
	}

	// =========================================================================================================================

	public void drawSky() {
		double angleToRow = screenHeight / (vueY * 2);

		int top = (int) (screenHeight / 2 + (camera.getVy() - 20) * angleToRow);

		int middle = (int) (screenHeight / 2 + (camera.getVy() + 10) * angleToRow);

		int bottom = (int) (screenHeight / 2 + (camera.getVy() + 40) * angleToRow);

		int middleColor = (3_380_955 + (int) (middle - top) / 6 * 65_792);
		int red = middleColor / (256 * 256);
		int green = (middleColor / 256) % 256;
		int blue = middleColor % 256;

		for (int row = 0; row < screenHeight; row++) {
			if (row < top)
				// Fill the top with blue
				for (int col = 0; col < screenWidth; col++)
					dataBuffer.setElem(row * screenWidth + col, 3_380_955);

			else if (row <= middle)
				// Fill the "middle top" with a light blue to dark blue gradient
				for (int col = 0; col < screenWidth; col++)
					dataBuffer.setElem(row * screenWidth + col, (3_380_955 + ((int) (row - top) / 6 * 65792)));

			else if (row <= bottom) {
				// Fill the "middle bottom" with a dark blue to black gradient
				double lala = 1 - (bottom - row) / ((double) bottom - middle);

				int dR = (int) (red * lala);
				int dG = (int) (green * lala);
				int dB = (int) (blue * lala);

				for (int col = 0; col < screenWidth; col++)
					dataBuffer.setElem(row * screenWidth + col, middleColor - (dR * 256 * 256 + dG * 256 + dB));
			} else
				// Fill the bottom with black
				for (int col = 0; col < screenWidth; col++)
					dataBuffer.setElem(row * screenWidth + col, -0xffffff);
		}
	}

	// =========================================================================================================================

	public void draw() {
		model.init(camera.vue, matrice);

		timeMat = System.currentTimeMillis();

		// TODO [Question] Switch list of draws to PriorityQueue ?
		ArrayList<Draw> draws = model.getDraws();
		nbCubes = draws.size();
		draws.sort(null);

		timeDraw = System.currentTimeMillis();

		for (Draw d : draws) {
			d.engine = this;
			// quadri.addAll(d.getQuadri(camera.vue, matrice));
			for (Quadri q : d.getQuadri(camera.vue, matrice))
				drawQuadri(q);
		}

		// for (Quadri q : quadri)
		// drawQuadri(q);

		timePixel = System.currentTimeMillis();
	}

	// =========================================================================================================================

	private int xInScreen(int x) {
		if (x < 0)
			return 0;
		if (x >= screenWidth)
			return screenWidth - 1;
		return x;
	}

	private int yInScreen(int y) {
		if (y < 0)
			return 0;
		if (y >= screenHeight)
			return screenHeight - 1;
		return y;
	}

	// =========================================================================================================================

	private void drawQuadri(Quadri q) {
		// Test if at least one of the points appear on the screen
		for (int index = 0; index < 4; index++)
			if (q.points[index].x < screenWidth && q.points[index].x > 0 && q.points[index].y < screenHeight
					&& q.points[index].y > 0) {

				// TODO [Improve] Avoid the generation of the list of the contour of the quadri
				ArrayList<Point> list = q.getList();

				if (q.fill)
					for (int i = 0; i < list.size(); i++) {
						int row = yInScreen(list.get(i).y);
						int col = xInScreen(list.get(i).x);

						while (++i < list.size() && list.get(i).y == row)
							;

						for (int k = col; k < xInScreen(list.get(i - 1).x); k++)
							setPixel(row, k, q.color, q.statePixel);
						i--;
					}
				else
					for (Point p : list)
						if (p.x >= 0 && p.x < screenWidth && p.y >= 0 && p.y < screenHeight)
							setPixel(p.y, p.x, q.color, q.statePixel);
				return;
			}
	}

	public void drawQuadri(Point[] p, int rgb, boolean fill, StatePixel statePixel) {
		drawQuadri(new Quadri(p, rgb, statePixel));
	}

	// =========================================================================================================================

	public void setPixel(int row, int col, int rgb, StatePixel state) {
		if (statePixel[col * screenHeight + row].isDrawable) {
			if (col == cursorX && row == cursorY) {
				Engine.faceTarget = Engine.faceTargetTemp;
				Engine.cubeTarget = Engine.cubeTargetTemp;
			}
			dataBuffer.setElem(row * screenWidth + col, rgb);
			statePixel[col * screenHeight + row] = state;
		}
	}

	public Point to2D(Point3D p) {
		if (p.x <= 0)
			return new Point(0, 0);
		double xx = p.z / (vue[0] * p.x);
		double yy = p.y / (vue[1] * p.x);

		int x = centerX + (int) (xx * screenWidth);
		int y = centerY + (int) (-yy * screenHeight);

		return new Point(x, y);
	}
}
