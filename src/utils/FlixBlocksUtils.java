package utils;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import client.textures.TextureFace;
import client.window.panels.PanGame;

public class FlixBlocksUtils {
	public static final double toRadian = Math.PI / 180;
	public static final double toDegres = 180 / Math.PI;

	// =========================================================================================================================

	/** str must be 8 long (or 6 long assuming alpha = 255) */
	public static int parseHexa(String str) {
		if (str.length() != 8 && str.length() != 6)
			return -123;
		int x = str.length() == 8 ? 0 : 255;
		int decal = 0;

		while (decal < str.length()) {
			x <<= 8;
			x += Integer.parseInt(str.substring(decal, decal += 2), 16);
		}

		return x;
	}

	public static String hexaToString(int x) {
		return String.format("%04x", (x >> 16) & 0xffff) + String.format("%04x", x & 0xffff);
	}

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

	public static Cursor createCursor(String file) {
		Image img = FlixBlocksUtils.getImage("cursor/" + file);
		return Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), file);
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

	// =========================================================================================================================

	public static void writeImage(BufferedImage img, String name) {
		try {
			ImageIO.write(img, "png", new File("img.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================================

	public static ArrayList<String> getFilesName(String folder) {
		ArrayList<String> list = null;

		try (Stream<Path> walk = Files.walk(Paths.get(folder))) {

			list = new ArrayList<String>(
					walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	// =========================================================================================================================

	public static String read(String name) {
		String str = new String();
		FileChannel fc;
		FileInputStream fis;

		try {
			fis = new FileInputStream(new File(name));
			fc = fis.getChannel();
			int size = (int) fc.size();
			ByteBuffer bBuff = ByteBuffer.allocate(size);
			fc.read(bBuff);
			bBuff.flip();
			byte[] tab = bBuff.array();
			str = new String(tab);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static void write(String content, String name) {
		FileWriter fw;
		try {
			fw = new FileWriter(new File(name));
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
