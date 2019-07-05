package client.window.graphicEngine.calcul;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;

import client.textures.TexturePack;
import client.window.graphicEngine.extended.DrawCubeFace;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import client.window.graphicEngine.structures.Quadri;
import data.enumeration.Face;

public class Engine {

	public static final double toRadian = Math.PI / 180;

	public TexturePack texturePack;

	/** Contains the data to be drawn */
	private Model model;
	/** The point of view and inclinations */
	private Camera camera;

	/** View angles */
	private double vx = Math.tan(60 * toRadian);
	private double vy = Math.tan(45 * toRadian);

	// ================ Target =====================
	public int cursorX = 100, cursorY = 100;

	static public ModelCube cubeTarget;
	static public Face faceTarget;

	// ================ Dev (F3) =====================
	public long timeInit = 0, timeMat = 0, timeDraw = 0, timePixel;

	// ================ Data =====================
	private Matrix matrice;

	private BufferedImage bimg;
	private DataBuffer dataBuffer;

	public int imgWidth = 100, imgHeight = 100;
	public int centerX, centerY;
	/**
	 * Width calculated from imgHeight to keep a good width/height ratio on drawed
	 * cubes
	 */
	public int widthRatio;

	/** true : draw a sky on the background | false : it will be transparent */
	public boolean drawSky = true;

	// =========================================================================================================================

	public Engine(Camera camera, Model model, TexturePack texturePack) {
		this.camera = camera;
		this.model = model;
		this.texturePack = texturePack;
	}

	// =========================================================================================================================

	public BufferedImage getImage(int w, int h) {
		init(w, h);

		matrice = new Matrix(-camera.getVx(), -camera.getVy(), camera.vue);

		cubeTarget = null;
		faceTarget = null;

		timeInit = System.currentTimeMillis();

		draw();

		if (drawSky)
			drawSky();

		return bimg;
	}

	public void init(int w, int h) {
		imgWidth = w;
		imgHeight = h;

		widthRatio = (int) (imgHeight * (1920. / 1080));

		centerX = imgWidth / 2;
		centerY = imgHeight / 2;

		bimg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		dataBuffer = bimg.getRaster().getDataBuffer();
	}

	// =========================================================================================================================

	public void drawSky() {
		int blue = createColor(255, 51, 150, 219);
		int light_blue = createColor(255, 160, 200, 230);
		int black = createColor(255, 0, 0, 0);

		double angleToRow = imgHeight / 45;

		int sky = (int) (imgHeight / 2 + (camera.getVy() - 10) * angleToRow);
		int horizon = (int) (imgHeight / 2 + (camera.getVy() + 10) * angleToRow);
		int voiD = (int) (imgHeight / 2 + (camera.getVy() + 30) * angleToRow);

		int color;

		for (int row = 0; row < imgHeight; row++) {
			if (row < sky)
				color = blue;
			else if (row <= horizon) // blue -> light_blue
				color = addHue(blue, light_blue, (row - sky) / ((double) horizon - sky));
			else if (row <= voiD) // light_blue -> black
				color = addHue(light_blue, black, (row - horizon) / ((double) voiD - horizon));
			else
				color = black;

			for (int col = 0; col < imgWidth; col++)
				setPixel(col, row, color);
		}
	}

	// =========================================================================================================================

	public void draw() {
		model.init(camera, matrice);

		timeMat = System.currentTimeMillis();

		ArrayList<Draw> draws = model.getDraws(camera);
		draws.sort(null);

		timeDraw = System.currentTimeMillis();

		for (Draw d : draws) {
			Polygon poly = d.getPoly(this);
			// Test if at least one of the points appear on the screen
			for (int index = 0; index < 4; index++)
				if (poly.xpoints[index] < imgWidth && poly.xpoints[index] > 0 && poly.ypoints[index] < imgHeight
						&& poly.ypoints[index] > 0) {
					if (cubeTarget == null && poly.contains(cursorX, cursorY)
							&& ((DrawCubeFace) d).cube.isTargetable()) {
						faceTarget = ((DrawCubeFace) d).face;
						cubeTarget = ((DrawCubeFace) d).cube;
					}
				}
			for (Quadri q : d.getQuadri(this))
				// Test if at least one of the points appear on the screen
				for (int index = 0; index < 4; index++)
					if (q.points[index].x < imgWidth && q.points[index].x > 0 && q.points[index].y < imgHeight
							&& q.points[index].y > 0) {
						drawQuadri(q);
						break;
					}
		}

		timePixel = System.currentTimeMillis();
	}

	// =========================================================================================================================

	private int xInScreen(int x) {
		if (x < 0)
			return 0;
		if (x >= imgWidth)
			return imgWidth - 1;
		return x;
	}

	private int yInScreen(int y) {
		if (y < 0)
			return 0;
		if (y >= imgHeight)
			return imgHeight - 1;
		return y;
	}

	// =========================================================================================================================

	private void drawQuadri(Quadri q) {
		if (q.fill) {
			int top = yInScreen(q.getTop());
			int bottom = yInScreen(q.getBottom());
			for (int row = top; row <= bottom; row++) {
				int right = xInScreen(q.getRight(row));
				for (int col = xInScreen(q.getLeft(row)); col <= right; col++)
					setPixel(col, row, q.color);
			}
		} else
			for (Line l : q.lines) {
				if (l.max < 0 || l.min >= imgHeight)
					return;

				int max = yInScreen(l.max);
				for (int row = yInScreen(l.min); row <= max; row++) {

					int right = xInScreen(l.getRight(row));
					for (int col = xInScreen(l.getLeft(row)); col <= right; col++)
						setPixel(col, row, q.color);
				}
			}
	}

	// =========================================================================================================================

	private void setPixel(int col, int row, int rgb) {
		// Returns if the pixel is already paint
		if ((getElem(col, row) >> 24 & 0xff) == 255)
			return;

		// If colored transparence : generate mixed color
		if (getElem(col, row) != 0)
			rgb = mixARGB(getElem(col, row), rgb);

		setElem(col, row, rgb);
	}

	public int getElem(int col, int row) {
		return dataBuffer.getElem(row * imgWidth + col);
	}

	public void setElem(int col, int row, int val) {
		dataBuffer.setElem(row * imgWidth + col, val);
	}

	// =========================================================================================================================
	// Color manipulations

	/**
	 * Returns the mixed color from the two given colors
	 * 
	 * @param a
	 *            - first color {@link BufferedImage#TYPE_INT_ARGB}
	 * @param b
	 *            - second color {@link BufferedImage#TYPE_INT_ARGB}
	 * @return mixed color {@link BufferedImage#TYPE_INT_ARGB}
	 */
	public static int mixARGB(int a, int b) {
		int aA = (a >> 24) & 0xff;
		int aR = (a >> 16) & 0xff;
		int aG = (a >> 8) & 0xff;
		int aB = a & 0xff;

		int bA = (b >> 24) & 0xff;
		int bR = (b >> 16) & 0xff;
		int bG = (b >> 8) & 0xff;
		int bB = b & 0xff;

		double l = (255. - aA) * bA / 255.;
		int alpha = (int) Math.round(aA + l);
		int red = (int) ((aR * aA + bR * l) / (aA + l));
		int green = (int) ((aG * aA + bG * l) / (aA + l));
		int blue = (int) ((aB * aA + bB * l) / (aA + l));

		return createColor(alpha, red, green, blue);
	}

	/**
	 * Add the hue to the color (doesn't modify the alpha)
	 * 
	 * @param color
	 *            - the original color {@link BufferedImage#TYPE_INT_ARGB}
	 * @param hue
	 *            - the hue {@link BufferedImage#TYPE_INT_RGB}
	 * @param percent
	 *            - in bounds [0, 1]
	 * @return mixed color {@link BufferedImage#TYPE_INT_ARGB}
	 */
	public static int addHue(int color, int hue, double percent) {
		int alpha = (color >> 24) & 0xff;
		int red = (color >> 16) & 0xff;
		int green = (color >> 8) & 0xff;
		int blue = color & 0xff;

		int hueRed = (hue >> 16) & 0xff;
		int hueGreen = (hue >> 8) & 0xff;
		int hueBlue = hue & 0xff;

		red = Math.min(255, red + (int) ((hueRed - red) * percent));
		green = Math.min(255, green + (int) ((hueGreen - green) * percent));
		blue = Math.min(255, blue + (int) ((hueBlue - blue) * percent));

		return createColor(alpha, red, green, blue);
	}

	public static int createColor(int alpha, int red, int green, int blue) {
		return (((((alpha << 8) + red) << 8) + green) << 8) + blue;
	}

	// =========================================================================================================================

	public Point to2D(Point3D p) {
		if (p.x <= 0)
			return new Point(0, 0);
		double xx = p.z / (vx * p.x);
		double yy = p.y / (vy * p.x);

		int x = centerX + (int) (xx * widthRatio);
		int y = centerY + (int) (-yy * imgHeight);

		return new Point(x, y);
	}
}
