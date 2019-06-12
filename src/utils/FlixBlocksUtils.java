package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import client.textures.TextureFace;
import client.window.panels.Pan;

public class FlixBlocksUtils {
	public static final double toRadian = Math.PI / 180;
	public static final double toDegres = 180 / Math.PI;

	public static final int NO_MINING = -1;

	// =========================================================================================================================

	public static int[][] imgToTab(String folder, String file) {
		Image img = getImage(folder + "/" + file, "/blocs/999.png");
		BufferedImage bimg = (BufferedImage) img;

		int[][] tab = new int[bimg.getHeight()][bimg.getWidth()];

		for (int i = 0; i < tab.length; i++)
			for (int j = 0; j < tab[0].length; j++)
				tab[tab.length - 1 - i][j] = bimg.getRGB(j, i);

		return tab;
	}

	public static Image getImage(String path, String error) {
		URL url = Pan.class.getResource("/" + path + ".png");

		if (url == null) {
			System.err.println("ERROR: can't read file: " + ("/" + path + ".png"));
			url = Pan.class.getResource(error);
		}

		try {
			return ImageIO.read(url);
		} catch (IOException e) {
			System.err.println("ERROR: can't read error file: " + url);
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
}
