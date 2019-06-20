package client.window.graphicEngine.calcul;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import client.textures.TexturePack;
import client.window.graphicEngine.draws.DrawCubeFace;
import client.window.graphicEngine.models.ModelCube;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import client.window.graphicEngine.structures.Quadri;
import data.enumeration.Face;

public class Engine {

	public static final double toRadian = Math.PI / 180;

	public static TexturePack texturePack;

	// Contains the data to be drawn
	public Model model;
	// The point of view and inclinations
	public Camera camera;

	// TODO [Improve] Set the view angle in function of the frame size
	// View angles (degrees)
	public int vueXDeg = 60, vueYDeg = 45;
	// View angles (radian) calculated from degree on window's size changes
	public double vueXRad, vueYRad;

	// ================ Target =====================
	public int cursorX = 100, cursorY = 100;

	static public ModelCube cubeTarget;
	static public Face faceTarget;

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

	// =========================================================================================================================

	public Engine() {
	}

	// =========================================================================================================================

	public BufferedImage getImage(int w, int h) {
		if (screenWidth != w || screenHeight != h)
			init(w, h);

		matrice = new Matrix(-camera.getVx(), -camera.getVy(), camera.vue);

		Arrays.fill(statePixel, StatePixel.EMPTY);

		cubeTarget = null;
		faceTarget = null;

		timeInit = System.currentTimeMillis();

		draw();
		drawSky();

		return bimg;
	}

	public void init(int w, int h) {
		screenWidth = w;
		screenHeight = h;

		vueXRad = Math.tan(vueXDeg * toRadian);
		vueYRad = Math.tan(vueYDeg * toRadian);

		centerX = screenWidth / 2;
		centerY = screenHeight / 2;

		bimg = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		dataBuffer = bimg.getRaster().getDataBuffer();

		statePixel = new StatePixel[screenWidth * screenHeight];
	}

	// =========================================================================================================================

	public void drawSky() {
		double angleToRow = screenHeight / (vueYDeg * 2);

		int top = (int) (screenHeight / 2 + (camera.getVy() - 20) * angleToRow);

		int middle = (int) (screenHeight / 2 + (camera.getVy() + 10) * angleToRow);

		int bottom = (int) (screenHeight / 2 + (camera.getVy() + 40) * angleToRow);

		int middleColor = (-13_396_261 - (int) (top - middle) / 7 * 65_792);

		int red = ((middleColor + 16_777_216) / (256 * 256)) % 256;
		int green = ((middleColor + 16_777_216) / 256) % 256;
		int blue = (middleColor + 16_777_216) % 256;

		for (int row = 0; row < screenHeight; row++) {
			if (row < top)
				// Fill the top with blue
				for (int col = 0; col < screenWidth; col++)
					setPixel(row, col, -13_396_261, StatePixel.FILL);

			else if (row <= middle)
				// Fill the "middle top" with a blue to light blue gradient
				for (int col = 0; col < screenWidth; col++)
					setPixel(row, col, (-13_396_261 - ((int) (top - row) / 7 * 65792)), StatePixel.FILL);

			else if (row <= bottom) {
				// Fill the "middle bottom" with a light blue to black gradient
				double lala = 1 - (bottom - row) / ((double) bottom - middle);

				int dR = (int) (red * lala);
				int dG = (int) (green * lala);
				int dB = (int) (blue * lala);

				for (int col = 0; col < screenWidth; col++)
					setPixel(row, col, middleColor - (dR * 256 * 256 + dG * 256 + dB), StatePixel.FILL);
			} else
				// Fill the bottom with black
				for (int col = 0; col < screenWidth; col++)
					setPixel(row, col, -0xffffff, StatePixel.FILL);
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
			for (Quadri q : d.getQuadri()) {
				// TODO [Improve] Ised on 1st person : target cubes in the back of the camera
				// (off-screen)
				if (cubeTarget == null && q.statePixel == StatePixel.CONTOUR && q.getPoly().contains(cursorX, cursorY)
						&& ((DrawCubeFace) d).cube.isTargetable()) {
					faceTarget = ((DrawCubeFace) d).face;
					cubeTarget = ((DrawCubeFace) d).cube;
				}
				drawQuadri(q);
			}
		}

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
							setPixel(row, k, q.color, q.statePixel, q.alpha);
						i--;
					}
				else
					for (Point p : list)
						if (p.x >= 0 && p.x < screenWidth && p.y >= 0 && p.y < screenHeight)
							setPixel(p.y, p.x, q.color, q.statePixel);
				return;
			}
	}

	// =========================================================================================================================

	public void setPixel(int row, int col, int rgb, StatePixel state) {
		setPixel(row, col, rgb, state, 0);
	}

	public void setPixel(int row, int col, int rgb, StatePixel state, int alpha) {
		// If colored transparence : generate mixed color
		if (statePixel[row * screenWidth + col] == StatePixel.TRANSPARENT)
			rgb = mix(rgb, dataBuffer.getElem(row * screenWidth + col));

		// Set color and state
		if (statePixel[row * screenWidth + col] != StatePixel.FILL
				&& statePixel[row * screenWidth + col] != StatePixel.CONTOUR) {
			dataBuffer.setElem(row * screenWidth + col, rgb);
			statePixel[row * screenWidth + col] = state;
		}
	}

	// =========================================================================================================================

	public static int mix(int a, int b) {
		a += 16_777_216;
		b += 16_777_216;

		int aR = a / (256 * 256);
		int aG = (a / 256) % 256;
		int aB = a % 256;

		int bR = b / (256 * 256);
		int bG = (b / 256) % 256;
		int bB = b % 256;

		int red = (aR + bR) / 2;
		int green = (aG + bG) / 2;
		int blue = (aB + bB) / 2;

		return -16_777_216 + red * 256 * 256 + green * 256 + blue;
	}

	// =========================================================================================================================

	public Point to2D(Point3D p) {
		if (p.x <= 0)
			return new Point(0, 0);
		double xx = p.z / (vueXRad * p.x);
		double yy = p.y / (vueYRad * p.x);

		int x = centerX + (int) (xx * screenWidth);
		int y = centerY + (int) (-yy * screenHeight);

		return new Point(x, y);
	}
}
