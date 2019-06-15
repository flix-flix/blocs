package client.window;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import client.keys.Key;
import client.session.GameMode;
import client.session.Session;
import client.window.panels.Pan;
import client.window.panels.PanDevlop;
import client.window.panels.PanGUI;
import client.window.panels.StateHUD;

public class Fen extends JFrame {
	private static final long serialVersionUID = 5348701813574947310L;

	Session session;

	// ============= Cursor ===================
	private static BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	private static Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0),
			"blank cursor");

	private int mouseX, mouseY;

	// ============= Pan ===================
	public Pan pan;

	public PanDevlop devlop;
	public PanGUI gui;

	// ============= Thread ===================
	// Refresh the image
	Thread threadActu;
	// Processing the next image doesn't affect the others tasks
	Thread threadImage;

	// true => generate a new image then turn to false
	boolean repaint = false;

	// =========================================================================================================================

	public Fen(Session session) throws AWTException {
		this.session = session;
		session.fen = this;

		// ======================================

		pan = new Pan(session);

		devlop = new PanDevlop(session);
		gui = new PanGUI(session);

		// ======================================

		this.setTitle("Blocs");
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);

		this.setFocusTraversalKeysEnabled(false);

		this.add(pan);
		pan.add(devlop, -1);
		pan.add(gui, -1);

		threadActu = new Thread(new Actu());

		threadImage = new Thread(new GetImg());

		// =========================================================================================================================

		this.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent k) {
			}

			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == Key.PAUSE.code) {
					if (session.stateGUI != StateHUD.GAME)
						session.keyboard.resume();
					else
						session.keyboard.pause();
				}

				else if (session.stateGUI == StateHUD.DIALOG) {

					if (k.getKeyCode() == Key.KEY_ENTER.code) {
						session.messages.send();
						session.stateGUI = StateHUD.GAME;
					} else if (k.getKeyCode() == Key.KEY_DEL.code)
						session.messages.deletePrevious();
					else if (k.getKeyCode() == Key.KEY_SUPPR.code)
						session.messages.deleteNext();

					else if (k.getKeyCode() == Key.KEY_UP.code)
						session.messages.historyPrevious();
					else if (k.getKeyCode() == Key.KEY_DOWN.code)
						session.messages.historyNext();
					else if (k.getKeyCode() == Key.KEY_RIGHT.code)
						session.messages.cursorMoveRight();
					else if (k.getKeyCode() == Key.KEY_LEFT.code)
						session.messages.cursorMoveLeft();

					else if (k.getKeyCode() == Key.KEY_PAGE_UP.code)
						session.messages.pageUp();
					else if (k.getKeyCode() == Key.KEY_PAGE_DOWN.code)
						session.messages.pageDown();
					else if (k.getKeyCode() == Key.KEY_TAB.code)
						session.messages.pageDown();
					else if (k.getKeyCode() == Key.KEY_END.code)
						session.messages.end();
					else if (k.getKeyCode() == Key.KEY_START.code)
						session.messages.start();

					else { // TODO [Improve] List of accepted character in dialog
						char c = k.getKeyChar();
						if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
								|| " \\/(){}[]+-:;,!?#|&\"'_²@=<>%.*^$£€".contains("" + c))
							session.messages.write(c);
					}
				}

				else if (session.stateGUI == StateHUD.GAME) {
					if (Key.get(k.getKeyCode()) != null)
						switch (Key.get(k.getKeyCode())) {

						case FORWARD:
							session.keyboard.forwardKeyEnabled = true;
							break;
						case BACKWARD:
							session.keyboard.backwardKeyEnabled = true;
							break;
						case RIGHT:
							session.keyboard.rightKeyEnabled = true;
							break;
						case LEFT:
							session.keyboard.leftKeyEnabled = true;
							break;

						case SPRINT:
							session.keyboard.sprintKeyEnabled = true;
							break;

						case UP:
							if (session.gamemode == GameMode.CREATIVE)
								session.keyboard.jumpKeyEnabled = true;
							break;
						case DOWN:
							if (session.gamemode == GameMode.CREATIVE)
								session.keyboard.sneakKeyEnabled = true;
							break;

						// =====================================================

						case DEVLOP:
							session.devlop = !session.devlop;
							break;

						// =====================================================

						case ACCESS_1:
							session.setGameMode(GameMode.CREATIVE);
							break;
						case ACCESS_2:
							session.setGameMode(GameMode.CLASSIC);
							break;
						case ACCESS_3:
							break;
						case ACCESS_4:
							break;
						case ACCESS_5:
							break;
						case ACCESS_6:
							break;
						case ACCESS_7:
							break;
						case ACCESS_8:
							break;
						case ACCESS_9:
							break;

						// =====================================================

						case KEY_EXCLAMATION:
							session.keyboard.dialog();
							break;

						// =====================================================

						case PAUSE:
							break;

						// =====================================================
						default:
							break;
						}
				}
			}

			public void keyReleased(KeyEvent k) {
				if (Key.get(k.getKeyCode()) != null)
					switch (Key.get(k.getKeyCode())) {
					case FORWARD:
						session.keyboard.forwardKeyEnabled = false;
						break;
					case BACKWARD:
						session.keyboard.backwardKeyEnabled = false;
						break;
					case RIGHT:
						session.keyboard.rightKeyEnabled = false;
						break;
					case LEFT:
						session.keyboard.leftKeyEnabled = false;
						break;
					case UP:
						session.keyboard.jumpKeyEnabled = false;
						break;
					case DOWN:
						session.keyboard.sneakKeyEnabled = false;
						break;
					case SPRINT:
						session.keyboard.sprintKeyEnabled = false;
						break;
					default:
						break;
					}
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				if (session.gamemode == GameMode.CLASSIC) {
					mouseX = e.getX();
					mouseY = e.getY();
				} else if (session.gamemode == GameMode.CREATIVE)
					if (session.stateGUI != StateHUD.DIALOG)
						session.keyboard.mouse(getLocationOnScreen().x, getLocationOnScreen().y, e.getX(), e.getY());
			}

			public void mouseDragged(MouseEvent e) {
				if (session.gamemode == GameMode.CLASSIC) {
					mouseX = e.getX();
					mouseY = e.getY();
				} else if (session.gamemode == GameMode.CREATIVE)
					if (session.stateGUI != StateHUD.DIALOG)
						session.keyboard.mouse(getLocationOnScreen().x, getLocationOnScreen().y, e.getX(), e.getY());

			}
		});

		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				// if (session.stateGUI == StateGUI.JEU)
				// e.getWheelRotation());
			}
		});

		this.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				if (session.stateGUI != StateHUD.PAUSE && session.stateGUI != StateHUD.DIALOG)
					if (e.getButton() == 1)
						session.keyboard.leftClicEnd();
					else if (e.getButton() == 3)
						session.keyboard.rightClicEnd();
			}

			public void mousePressed(MouseEvent e) {
				if (session.stateGUI != StateHUD.PAUSE && session.stateGUI != StateHUD.DIALOG)
					if (e.getButton() == 1)
						session.keyboard.leftClic();
					else if (e.getButton() == 3)
						session.keyboard.rightClic();

			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				session.keyboard.memX = e.getX();
				session.keyboard.memY = e.getY();
			}

			public void mouseClicked(MouseEvent e) {
			}
		});

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				repaint();
			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {

			}
		});
	}

	// =========================================================================================================================

	public void cursorVisible(boolean visible) {
		if (visible)
			pan.setCursor(Cursor.getDefaultCursor());
		else
			pan.setCursor(blankCursor);
	}

	// =========================================================================================================================

	public void start() {
		threadActu.start();
		threadImage.start();
	}

	// =========================================================================================================================

	class Actu implements Runnable {
		public void run() {
			// Count the number of frames displayed since the last "second timer" restart
			int fps = 0;
			// The number of ms before generating another frame
			int wait = 0;
			// Store the time which the last second starts
			long time = System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() - time >= 1000) {
					time = System.currentTimeMillis();

					session.fps = fps;
					fps = 0;

					session.ticksPhys = session.keyboard.ticks;
					session.keyboard.ticks = 0;
				}

				wait--;
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (session.stateGUI != StateHUD.PAUSE && !session.processing && wait < 0) {
					session.processing = true;
					wait = 1000 / session.FPSmax;

					repaint = true;
					fps++;
				}
			}
		}
	}

	class GetImg implements Runnable {
		public void run() {
			while (true) {
				if (repaint) {
					repaint = false;

					session.timeBefore = System.currentTimeMillis();

					session.setTarget(mouseX, mouseY);

					pan.img = session.getImage(pan.getWidth(), pan.getHeight());

					session.updateTimeDev();
					session.targetUpdate();

					pan.repaint();
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
