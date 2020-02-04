package graphicEngine.calcul;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;

import graphicEngine.structures.Drawable;
import graphicEngine.structures.Modelisable;
import graphicEngine.structures.Quadri;

public class Engine {

	private static final double toRadian = Math.PI / 180;

	/** Contains the data to be drawn */
	private Modelisable model;
	/** The point of view and inclinations */
	private Camera camera;

	/** Will replace the current model on the next getImage() call */
	private Modelisable newModel = null;
	/** Will replace the current model on the next getImage() call */
	private Camera newCamera = null;

	/** View angles */
	private double vx = Math.tan(60 * toRadian);
	private double vy = Math.tan(45 * toRadian);

	// ================ Target =====================
	private int targetX = 100, targetY = 100;

	private Drawable drawTarget;
	private Quadri quadriTarget;

	// ================ Processing time =====================
	public long timeStart, timeMat, timeDraw, timeEnd;

	// ================ Data =====================
	private Matrix matrix;

	private BufferedImage bimg;
	private DataBuffer dataBuffer;

	private int imgWidth = 100, imgHeight = 100;
	private int centerX, centerY;
	/**
	 * Width calculated from imgHeight to keep a good width/height ratio on drawed
	 * cubes
	 */
	private int widthRatio;

	// ================ Background =====================
	/** Leave the background transparent */
	public static final int NONE = 0;
	/** Fill the background with black */
	public static final int FILL = 1;
	/** Draw the sky in the background */
	public static final int SKY = 2;

	/** State of the background */
	private int background = SKY;

	// =========================================================================================================================

	public Engine(Camera camera, Modelisable model) {
		this.camera = camera;
		this.model = model;
	}

	// =========================================================================================================================

	public void setModelCamera(Modelisable model, Camera camera) {
		newModel = model;
		newCamera = camera;
	}

	public BufferedImage getImage(int w, int h) {
		if (w <= 0 || h <= 0)
			return null;

		if (newModel != null) {
			model = newModel;
			newModel = null;
		}
		if (newCamera != null) {
			camera = newCamera;
			newCamera = null;
		}

		timeStart = System.currentTimeMillis();
		init(w, h);

		matrix = new Matrix(-camera.getVx(), -camera.getVy(), camera.vue);

		drawTarget = null;
		quadriTarget = null;

		draw();

		timeEnd = System.currentTimeMillis();

		drawBackgroung();

		return bimg;
	}

	private void init(int w, int h) {
		imgWidth = w;
		imgHeight = h;

		widthRatio = (int) (imgHeight * (1920. / 1080));

		centerX = imgWidth / 2;
		centerY = imgHeight / 2;

		bimg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		dataBuffer = bimg.getRaster().getDataBuffer();
	}

	// =========================================================================================================================

	private void drawBackgroung() {
		if (background == NONE)
			return;

		if (background == FILL) {
			for (int row = 0; row < imgHeight; row++)
				for (int col = 0; col < imgWidth; col++)
					setPixel(col, row, 0xff000000);
			return;
		}

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

	private void draw() {
		model.init(camera, matrix);

		timeMat = System.currentTimeMillis();

		ArrayList<Drawable> draws = new ArrayList<>(model.getDraws(camera));
		draws.sort(null);

		timeDraw = System.currentTimeMillis();

		for (Drawable d : draws) {
			Polygon poly = d.getPoly(this);
			boolean targeted = false;

			// Test if at least one of the points appear on the screen
			for (int index = 0; index < 4; index++)
				if (poly.xpoints[index] < imgWidth && poly.xpoints[index] > 0 && poly.ypoints[index] < imgHeight
						&& poly.ypoints[index] > 0) {
					// Test if the target is in the polygon
					if (drawTarget == null && poly.contains(targetX, targetY) && d.isTargetable()) {
						targeted = true;
						drawTarget = d;
					}
				}
			for (Quadri q : d.getQuadri(this))
				// Test if at least one of the points appear on the screen
				for (int i = 0; i < 4; i++)
					if (q.points[i].x < imgWidth && q.points[i].x > 0 && q.points[i].y < imgHeight
							&& q.points[i].y > 0) {
						if (targeted && quadriTarget == null && q.id != Quadri.NOT_NUMBERED
								&& q.getPoly().contains(targetX, targetY))
							quadriTarget = q;
						drawQuadri(q);
						break;
					}
		}

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
		if (q.fill)
			for (int row = yInScreen(q.getTop()); row <= yInScreen(q.getBottom()); row++)
				for (int col = xInScreen(q.getLeft(row)); col <= xInScreen(q.getRight(row)); col++)
					setPixel(col, row, q.color);

		else
			for (Line l : q.lines) {
				if (l.max < 0 || l.min >= imgHeight)
					return;

				for (int row = yInScreen(l.min); row <= yInScreen(l.max); row++)
					for (int col = xInScreen(l.getLeft(row)); col <= xInScreen(l.getRight(row)); col++)
						setPixel(col, row, q.color);
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

	private int getElem(int col, int row) {
		return dataBuffer.getElem(row * imgWidth + col);
	}

	private void setElem(int col, int row, int val) {
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
		if (a == b)
			return a;

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

	// =========================================================================================================================

	public Drawable getDrawTarget() {
		return drawTarget;
	}

	public Quadri getQuadriTarget() {
		return quadriTarget;
	}

	// =========================================================================================================================

	public void setBackground(int x) {
		background = x;
	}

	public void setTarget(int x, int y) {
		targetX = x;
		targetY = y;
	}
}