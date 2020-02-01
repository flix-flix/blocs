package launcher;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import client.editor.Editor;
import client.window.Displayable;
import client.window.Fen;
import client.window.Game;
import client.window.KeyBoard;
import launcher.LauncherButton.LauncherButtonAction;

public class Launcher extends JPanel implements Displayable {
	private static final long serialVersionUID = -3189420915172593199L;

	private Fen fen;

	private LauncherButton game, editor, quit;

	private KeyBoardLauncher keyboard = new KeyBoardLauncher();

	// =========================================================================================================================

	public Launcher(Fen fen) {
		this.fen = fen;
		this.setLayout(null);

		add(game = new LauncherButton(this, LauncherButtonAction.GAME));
		add(editor = new LauncherButton(this, LauncherButtonAction.EDITOR));
		add(quit = new LauncherButton(this, LauncherButtonAction.QUIT));

		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				game.setBounds(getWidth() / 2 - 100, 150, 200, 50);
				editor.setBounds(getWidth() / 2 - 100, 400, 200, 50);
				quit.setBounds(getWidth() / 2 - 100, 650, 200, 50);
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

	public void click(LauncherButtonAction action) {
		switch (action) {
		case GAME:
			fen.setDisplay(new Game(fen));
			break;
		case EDITOR:
			fen.setDisplay(new Editor(fen));
			break;
		case QUIT:
			System.exit(0);
			break;

		default:
			break;
		}
	}

	// =========================================================================================================================

	@Override
	public JPanel getContentPane() {
		return this;
	}

	@Override
	public void updateSize(int x, int y) {
		setSize(x, y);
	}

	@Override
	public KeyBoard getKeyBoard() {
		return keyboard;
	}
}
