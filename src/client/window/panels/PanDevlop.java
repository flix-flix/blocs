package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import client.session.Session;
import data.map.Map;

public class PanDevlop extends JPanel {
	private static final long serialVersionUID = -3167144738527114380L;

	Session session;

	/** Space between text and background border (pixels) */
	static final int margin = 5;
	/** Height of the text-backgrounds */
	static final int size = 20;

	FontMetrics fm;
	Graphics graphics;

	/** Number of strings already drawn (left/right) */
	int right = 0, left = 0;

	public PanDevlop(Session session) {
		this.session = session;

		Font font = new Font("monospace", Font.PLAIN, 12);
		fm = getFontMetrics(font);

		this.setOpaque(false);
	}

	public void paintComponent(Graphics g) {
		graphics = g;
		right = 0;
		left = 0;

		if (session.devlop) {
			String strBloc = "Bloc: ";
			String strFace = "Face: ";

			// ======================= Target =========================

			if (session.cubeTarget != null) {
				strBloc += session.cubeTarget.toString();
				strFace += session.faceTarget.toString();
			} else {
				strBloc += "None";
				strFace += "None";
			}

			// =========================================================================================================================

			writeLeft(String.format("Camera: X = %.1f  Y = %.1f  Z = %.1f", session.camera.vue.x, session.camera.vue.y,
					session.camera.vue.z));
			writeLeft("Chunk: " + "X = " + Map.toChunkCoord(session.camera.vue.x) + " Z = "
					+ Map.toChunkCoord(session.camera.vue.z));
			writeLeft("View: X = " + session.camera.getVx() + " Y = " + session.camera.getVy());
			writeLeft("Orientation: " + session.playerOrientation.toString());
			left++;
			writeLeft(strBloc);
			writeLeft(strFace);

			// =========================================================================================================================

			writeRight("Chunks: " + session.nbChunks + " Faces: " + session.nbFaces);
			right++;
			writeRight("timeMat: " + session.timeMat + " timeDraw: " + session.timeDraw + " timePixel: "
					+ session.timeQuadri);
			right++;
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
