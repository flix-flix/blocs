package client.window;

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
import client.session.Session;
import client.session.StateHUD;
import client.session.UserAction;
import client.window.graphicEngine.calcul.Engine;
import client.window.panels.PanDevlop;
import client.window.panels.PanGUI;
import client.window.panels.PanGame;
import client.window.panels.PanPause;
import client.window.panels.editor.PanEditor;
import data.id.ItemTable;
import data.map.Cube;
import data.map.resources.ResourceType;
import server.game.GameMode;
import server.send.Action;
import utils.FlixBlocksUtils;

public class Fen extends JFrame {
	private static final long serialVersionUID = 5348701813574947310L;

	Session session;

	// ============= Cursor ===================
	private static BufferedImage imgCursorInvisible = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	private static Cursor cursorInvisible = Toolkit.getDefaultToolkit().createCustomCursor(imgCursorInvisible,
			new Point(0, 0), "blank cursor");

	private Cursor cursorGoto, cursorBuild, cursorAttack;
	private Cursor cursorDrop, cursorDropWood, cursorDropStone, cursorDropWater;
	private Cursor cursorAxe, cursorPickaxe, cursorBucket;

	private boolean cursorVisible = true;

	public int mouseX, mouseY;

	// ============= Pan ===================
	public PanGame game;
	public PanPause pause;
	public PanDevlop devlop;
	public PanGUI gui;

	// ============= Editor ===================
	public PanEditor editor;

	// ============= Thread ===================
	/** Refresh the image */
	private Thread threadActu;
	/** Processing the next image doesn't affect the others tasks */
	private Thread threadImage;

	/** true : start the generation of a new image then turn to false */
	boolean repaint = false;

	// =========================================================================================================================

	public Fen(Session session) {
		this.session = session;
		session.fen = this;

		generateCursor();
		ResourceType.setTextureFolder(session.texturePack.getFolder());

		// ======================================

		game = new PanGame(session);
		pause = new PanPause(session);
		devlop = new PanDevlop(session);
		gui = new PanGUI(session);
		editor = session.editor.panel;

		// ======================================

		this.setTitle("Blocs");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1200, 1040);
		this.setLocation(0, 0);
		// this.setExtendedState(MAXIMIZED_BOTH);

		// ======================================

		this.setLayout(null);

		game.setSize(getContentPane().getSize());
		gui.setSize(getContentPane().getSize());
		devlop.setSize(getContentPane().getSize());
		pause.setSize(getContentPane().getSize());
		editor.setSize(getContentPane().getSize());

		editor.setVisible(false);

		// this.setContentPane(game);
		this.add(game);
		game.add(pause, -1);
		game.add(devlop, -1);
		game.add(gui, -1);
		game.add(editor, -1);

		// =========================================================================================================================

		threadActu = new Thread(new Actu());
		threadImage = new Thread(new GetImg());

		threadActu.setName("Max fps counter");
		threadImage.setName("Image generator");

		// =========================================================================================================================

		this.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent k) {
			}

			public void keyPressed(KeyEvent k) {
				if (session.stateHUD == StateHUD.EDITOR)
					if (session.editor.keyPressed(k))
						return;

				if (session.stateHUD == StateHUD.DIALOG) {
					if (Key.get(k.getKeyCode()) != null)
						switch (Key.get(k.getKeyCode())) {
						case PAUSE:
							session.keyboard.resume();
							break;
						case KEY_ENTER:
							session.messages.send();
							session.keyboard.mouseToCenter();
							session.stateHUD = StateHUD.GAME;
							break;
						case KEY_DEL:
							session.messages.deletePrevious();
							break;
						case KEY_SUPPR:
							session.messages.deleteNext();
							break;

						case KEY_UP:
							session.messages.historyPrevious();
							break;
						case KEY_DOWN:
							session.messages.historyNext();
							break;
						case KEY_RIGHT:
							session.messages.cursorMoveRight();
							break;
						case KEY_LEFT:
							session.messages.cursorMoveLeft();
							break;

						case KEY_PAGE_UP:
							session.messages.pageUp();
							break;
						case KEY_PAGE_DOWN:
							session.messages.pageDown();
							break;
						case KEY_TAB:
							break;
						case KEY_END:
							session.messages.end();
							break;
						case KEY_START:
							session.messages.start();
							break;

						default:
							break;
						}

					// TODO [Improve] List of accepted character in dialog
					char c = k.getKeyChar();
					if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
							|| " \\/(){}[]+-:;,!?#|&\"'_²@=<>%.*^$£€".contains("" + c))
						session.messages.write(c);
				}

				else if (session.stateHUD == StateHUD.GAME || session.stateHUD == StateHUD.EDITOR) {
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
							if (session.gamemode == GameMode.CREATIVE || session.stateHUD == StateHUD.EDITOR)
								session.keyboard.jumpKeyEnabled = true;
							break;
						case DOWN:
							if (session.gamemode == GameMode.CREATIVE || session.stateHUD == StateHUD.EDITOR)
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
							session.keyboard.pause();
							break;

						// =====================================================
						default:
							break;
						}
				} else if (session.stateHUD == StateHUD.PAUSE)
					session.keyboard.resume();

			}

			public void keyReleased(KeyEvent k) {
				if (session.stateHUD == StateHUD.EDITOR)
					if (session.editor.keyReleased(k))
						return;

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
					session.keyboard.mouse(getLocationOnScreen().x, getLocationOnScreen().y, e.getX(), e.getY());
			}

			public void mouseDragged(MouseEvent e) {
				if (session.stateHUD == StateHUD.EDITOR)
					session.editor.drag(e);
				mouseMoved(e);
			}
		});

		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				session.keyboard.wheelRotation(e.getWheelRotation());
			}
		});

		this.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				if (session.stateHUD != StateHUD.PAUSE && session.stateHUD != StateHUD.DIALOG)
					if (e.getButton() == 1)
						session.keyboard.leftClickEnd();
					else if (e.getButton() == 3)
						session.keyboard.rightClickEnd();
			}

			public void mousePressed(MouseEvent e) {
				if (session.stateHUD != StateHUD.PAUSE && session.stateHUD != StateHUD.DIALOG)
					if (e.getButton() == 1)
						session.keyboard.leftClick(e);
					else if (e.getButton() == 3)
						session.keyboard.rightClick(e);
			}

			public void mouseExited(MouseEvent e) {
				if (session.gamemode == GameMode.CLASSIC) {
					mouseX = 1_000_000;
					mouseY = 1_000_000;
				}
			}

			public void mouseEntered(MouseEvent e) {
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
				game.setSize(getContentPane().getSize());
				gui.setSize(getContentPane().getSize());
				devlop.setSize(getContentPane().getSize());
				pause.setSize(getContentPane().getSize());
				editor.setSize(getContentPane().getSize());
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		this.setVisible(true);
	}

	// =========================================================================================================================
	// Cursor

	public void updateCursor() {
		Cursor cursor = Cursor.getDefaultCursor();
		Cube cube = session.targetedCube;

		session.unitAction = null;

		if (!cursorVisible)
			cursor = cursorInvisible;
		// TODO [Duplicate] Editor : Cursor selection
		else if (session.stateHUD == StateHUD.EDITOR)
			cursor = session.editor.getCursor();
		else if (session.stateHUD == StateHUD.GAME)
			if (session.getAction() == UserAction.MOUSE)

				if (cube != null && session.fen.gui.unit != null
						&& session.fen.gui.unit.getPlayer().equals(session.player))
					if (ItemTable.isResource(cube.itemID))// Harvestable
						switch (ItemTable.getResourceType(cube.itemID)) {
						case WOOD:
							cursor = cursorAxe;
							session.unitAction = Action.UNIT_HARVEST;
							break;
						case STONE:
							cursor = cursorPickaxe;
							session.unitAction = Action.UNIT_HARVEST;
							break;
						case WATER:
							cursor = cursorBucket;
							session.unitAction = Action.UNIT_HARVEST;
							break;
						}
					else if (cube.build != null) {// Building
						if (cube.build.getPlayer().equals(session.player)) {
							if (!cube.build.isBuild()) {
								cursor = cursorBuild;
								session.unitAction = Action.UNIT_BUILD;
							} else if (session.fen.gui.unit.hasResource()
									&& cube.build.canStock(session.fen.gui.unit.getResource())) {// Stock
								cursor = cursorDrop;
								session.unitAction = Action.UNIT_STORE;

								switch (session.fen.gui.unit.getResource().getType()) {
								case WOOD:
									cursor = cursorDropWood;
									break;
								case STONE:
									cursor = cursorDropStone;
									break;
								case WATER:
									cursor = cursorDropWater;
									break;
								}
							}
						} else {// Opponent
							cursor = cursorAttack;
							session.unitAction = Action.UNIT_ATTACK;
						}
					} else if (cube.unit != null) {// Unit
						if (cube.unit.getPlayer().equals(session.player)) {// Own

						} else {// Opponent
							cursor = cursorAttack;
							session.unitAction = Action.UNIT_ATTACK;
						}
					} else {
						cursor = cursorGoto;
						session.unitAction = Action.UNIT_GOTO;
					}

		setCursor(cursor);
	}

	public void setCursorVisible(boolean visible) {
		cursorVisible = visible;
		updateCursor();
	}

	public void generateCursor() {
		cursorGoto = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorGoto");
		cursorBuild = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorBuild");
		cursorAttack = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorAttack");

		cursorDrop = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorDrop");
		cursorDropWood = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorDropWood");
		cursorDropStone = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorDropStone");
		cursorDropWater = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorDropWater");

		cursorAxe = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorAxe");
		cursorPickaxe = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorPickaxe");
		cursorBucket = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorBucket");
	}

	// =========================================================================================================================

	public void start() {
		threadActu.start();
		threadImage.start();

		gui.refreshGUI();
	}

	// =========================================================================================================================

	public void setAction(UserAction action) {
		if (action == UserAction.EDITOR) {
			if (session.stateHUD == StateHUD.EDITOR) {// Close Editor
				session.stateHUD = StateHUD.GAME;

				session.setModelCamera(session.map, session.camera);

				setAction(UserAction.MOUSE);
			} else {// Open Editor
				session.stateHUD = StateHUD.EDITOR;
				session.setAction(UserAction.EDITOR);

				session.setModelCamera(session.editor.map, session.editor.camera);
			}
			boolean game = session.stateHUD == StateHUD.GAME;

			gui.setVisible(game);
			editor.setVisible(!game);
			session.setEngineBackground(game ? Engine.SKY : Engine.FILL);
		} else {

			session.setAction(action);

			updateCursor();
			gui.refreshGUI();
		}
	}

	// =========================================================================================================================

	public boolean isNeededQuadriPrecision() {
		if (session.getAction() == UserAction.EDITOR)
			return session.editor.isNeededQuadriPrecision();
		return false;
	}

	// =========================================================================================================================

	/**
	 * Set "repaint" to true when it need a new image
	 */
	class Actu implements Runnable {
		public void run() {
			// Count the number of frames displayed since the last "second timer" restart
			int fps = 0;
			// The number of ms before generating another frame
			long wait = 0;
			// Store the time which the last second starts
			long time = System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() - time >= 1000) {// Update FPS infos
					time = System.currentTimeMillis();

					session.fps = fps;
					fps = 0;

					session.ticksKeyBoard = session.keyboard.ticks;
					session.keyboard.ticks = 0;

					// TODO get ticks from server
					// session.ticksPhys = session.clock.ticks;
					// session.clock.ticks = 0;
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (session.stateHUD != StateHUD.PAUSE && !session.processing && wait <= System.currentTimeMillis()) {
					session.processing = true;
					wait = System.currentTimeMillis() + 1000 / session.FPSmax;

					repaint = true;
					fps++;
				}
			}
		}
	}

	/**
	 * Generates a new image if needed
	 */
	class GetImg implements Runnable {
		public void run() {
			while (true) {
				if (repaint) {
					repaint = false;

					game.startW = session.gamemode == GameMode.CLASSIC ? 400 : 0;

					if (session.gamemode == GameMode.CLASSIC)
						session.setTarget(mouseX - 8 - game.startW, mouseY - 32);
					else if (session.gamemode == GameMode.CREATIVE)
						session.setTarget(game.centerW - game.startW, game.centerH);

					if (game.width > 0 && game.height > 0)
						game.img = session.getImage(game.width, game.height);

					session.updateTimeDev();
					session.targetUpdate();

					game.repaint();
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
