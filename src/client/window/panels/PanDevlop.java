package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import client.session.Session;
import data.enumeration.Face;
import data.map.Cube;
import data.map.Map;

public class PanDevlop extends JPanel {
	private static final long serialVersionUID = -3167144738527114380L;

	Session session;

	// Text-backgrounds are bigger than <margin> pixels
	static final int margin = 5;
	// Height of the text-backgrounds
	static final int size = 20;

	FontMetrics fm;
	Graphics graphics;

	// Number of strings already drawn (left/right)
	int right = 0, left = 0;

	public PanDevlop(Session session) {
		this.session = session;

		Font font = new Font("monospace", Font.PLAIN, 12);
		fm = getFontMetrics(font);

		this.setOpaque(false);
	}

	public void paintComponent(Graphics g) {
		this.setBounds(getParent().getBounds());
		graphics = g;
		right = 0;
		left = 0;

		if (session.devlop) {
			String strBloc = "Bloc: ";
			String strFace = "Face: ";
			String strTarget = "Target: ";

			// ======================= Bloc/Target =========================

			Cube cube = session.cubeTarget;
			Face face = session.faceTarget;

			if (cube != null) {
				strBloc += cube.toString();
				strFace += face.toString();
			} else {
				strBloc += "None";
				strFace += "None";
			}

			strTarget += "None";

			// ==================================================================================================================

			writeLeft("Camera: " + session.camera.vue.toString());
			writeLeft("Chunk: " + "X = " + Map.toChunkCoord(session.camera.vue.x) + " Z = "
					+ Map.toChunkCoord(session.camera.vue.z));
			writeLeft("Vue: vx = " + session.camera.getVx() + " vy = " + session.camera.getVy());
			writeLeft(strBloc);
			writeLeft(strFace);
			writeLeft(session.playerOrientation.toString());
			writeLeft(strTarget);

			// ==================================================================================================================

			writeRight("nbChunks: " + session.nbChunks + " nbCubes: " + session.nbCubes);
			writeRight("timeInit: " + session.timeInit + " timeMat: " + session.timeMat + " timeDraw: "
					+ session.timeDraw + " timePixel: " + session.timePixel);
			writeRight("FPS: " + session.fps);
			writeRight("Ticks (phys): " + session.ticksPhys);
			writeRight("Ticks (key): " + session.ticksKeyBoard);
		}
	}

	/**
	 * Write the string on the top left corner bellow the previous strings
	 * 
	 * @param str
	 */
	public void writeLeft(String str) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, margin + size * left, fm.stringWidth(str) + 2 * margin, size);
		graphics.setColor(Color.black);
		graphics.drawString(str, margin, size * (left + 1));
		left++;
	}

	/**
	 * Write the string on the top right corner bellow the previous strings
	 * 
	 * @param str
	 */
	public void writeRight(String str) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(getWidth() - fm.stringWidth(str) - margin, margin + size * right,
				fm.stringWidth(str) + 2 * margin, size);
		graphics.setColor(Color.black);
		graphics.drawString(str, getWidth() - fm.stringWidth(str), size * (right + 1));
		right++;
	}
}
