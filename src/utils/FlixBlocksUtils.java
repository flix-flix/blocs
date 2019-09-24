package utils;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import client.textures.TextureFace;
import client.window.panels.PanGame;

public class FlixBlocksUtils {
	public static final double toRadian = Math.PI / 180;
	public static final double toDegres = 180 / Math.PI;

	public static final int NO_MINING = -1;

	// =========================================================================================================================

	public static Image getImage(String path) {
		URL url = PanGame.class.getResource("/" + path + ".png");

		if (url == null) {
			debugBefore("ERROR: can't read file: " + ("/" + path + ".png"));
			url = PanGame.class.getResource("/999.png");
		}

		try {
			return ImageIO.read(url);
		} catch (IOException e) {
			debugBefore("ERROR: can't read error file: " + url);
			e.printStackTrace();
		}
		return null;
	}

	// =========================================================================================================================

	public static boolean pngExist(String name) {
		return resourceExist(name + ".png");
	}

	public static boolean resourceExist(String name) {
		URL url = TextureFace.class.getResource("/" + name);
		return url != null;
	}

	// =========================================================================================================================

	public static int mixColor(int a, int b) {
		a += 16_777_216;
		b += 16_777_216;

		int aR = a / (256 * 256) % 256;
		int aG = (a / 256) % 256;
		int aB = a % 256;

		int bR = b / (256 * 256) % 256;
		int bG = (b / 256) % 256;
		int bB = b % 256;

		int red = (aR + bR) / 2;
		int green = (aG + bG) / 2;
		int blue = (aB + bB) / 2;

		return -16_777_216 + red * 256 * 256 + green * 256 + blue;
	}

	// =========================================================================================================================

	public static void debugBefore(String str) {
		System.err.print(str);
		System.err.println(" (" + Thread.currentThread().getStackTrace()[3].getFileName() + ":"
				+ Thread.currentThread().getStackTrace()[3].getLineNumber() + ")");
	}

	public static void debug(String str) {
		debugBefore(str);
	}
}
