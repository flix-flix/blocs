package utils;

import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class FlixBlocksUtils {
	public static final double toRadian = Math.PI / 180;
	public static final double toDegres = 180 / Math.PI;

	public static Image imgError;

	static {
		try {
			imgError = ImageIO.read(FlixBlocksUtils.class.getResource("/999.png"));
		} catch (IOException e) {
			debugBefore("Can't read imgERROR");
			e.printStackTrace();
		}
	}

	// =========================================================================================================================
	// Hexa

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
	// Image

	public static Image getImage(String path) {
		File file = new File("resources/" + path + ".png");

		if (!file.exists()) {
			debugBefore("ERROR: file doesn't exists: " + file.getPath());
			return imgError;
		}

		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			debugBefore("ERROR: can't read image: " + file.getPath());
			e.printStackTrace();
		}
		return imgError;
	}

	// =========================================================================================================================
	// Cursor

	public static Cursor createCursor(String file) {
		Image img = FlixBlocksUtils.getImage(file);
		return Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), file);
	}

	// =========================================================================================================================

	public static ArrayList<String> getLines(String text, FontMetrics fm, int width) {
		String[] words = text.split(" ");
		ArrayList<String> lines = new ArrayList<>();
		String line = "";

		for (String word : words)
			if (fm.stringWidth(line + " " + word) > width) {
				lines.add(line);
				line = word;
			} else
				line += (line.isEmpty() ? "" : " ") + word;

		lines.add(line);
		return lines;
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
	// Debug

	public static void debugBefore(String str) {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[3];
		System.err.println(String.format("%s (%s:%d)", str, stack.getFileName(), stack.getLineNumber()));
	}

	public static void debug(String str) {
		debugBefore(str);
	}

	// =========================================================================================================================
	// Files

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
	// Read/Write

	public static String read(String name) {
		StringWriter sw = new StringWriter();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(name)), "UTF8"));
			char[] arr = new char[1024];
			int size;

			while ((size = br.read(arr)) > 0) {
				sw.write(arr, 0, size);
			}

			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	public static void write(String content, String name) {
		File file = new File(name);

		// Create File (and folders)
		if (!file.exists()) {
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				FlixBlocksUtils.debug("Can't createFile");
				return;
			}
		}

		// Write
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));

			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
