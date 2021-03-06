package tetris;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.json.JSONObject;
import tetris.view.ViewFactory;
import tetris.view.ViewName;
import tetris.view.component.ComponentView;
import tetris.view.component.DialogLabel;
import tetris.view.component.RepaintView;
import tetris.view.listener.OnChangeViewListener;
import util.AudioManager;
import util.AudioManager.OnPreloadListener;
import util.Debug;

/**
 * 俄羅斯方塊主程式,建立GameView並add之後執行.
 *
 * @author Ray Lee
 */
public class Tetris extends JFrame implements OnChangeViewListener {
  private static final long serialVersionUID = 1L;
  private ComponentView view;
  private ViewFactory viewFactory;

  public Tetris() {}

  /** 初始化. */
  public void initialize() {
    Container pane = getContentPane();
    viewFactory = new ViewFactory();
    view = viewFactory.create(this, Config.get(), ViewName.MENU);

    pane.addContainerListener(
        new ContainerListener() {

          @Override
          public void componentAdded(ContainerEvent e) {
            if (e.getChild() instanceof RepaintView) {
              RepaintView o = (RepaintView) e.getChild();

              o.initialize();
            }
          }

          @Override
          public void componentRemoved(ContainerEvent e) {
            if (e.getChild() instanceof RepaintView) {
              RepaintView o = (RepaintView) e.getChild();

              o.release();
            }
          }
        });

    view.setOnChangeViewListener(this);
    pane.add(view);

    // 鍵盤事件處理
    addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            if (view != null) {
              view.onKeyCode(e.getKeyCode());
            }
          }
        });
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            // 滑鼠左鍵
            if (e.getButton() == MouseEvent.BUTTON1) {
              if (view != null) {
                view.onMouseClicked(e);
              }
            }
          }
        });

    preload();
  }

  @Override
  public void onChangeView(ViewName event, JSONObject params) {
    getContentPane().remove(view);
    view.setOnChangeViewListener(null);
    view = viewFactory.create(this, Config.get(), event, params);

    view.setOnChangeViewListener(this);
    getContentPane().add(view);
  }

  private void preload() {
    DialogLabel dialog = newLoadingDialog();
    ArrayList<String> audioPath = new ArrayList<>();

    dialog.setLabelText("loading...");
    audioPath.add("/sound/music.wav");
    audioPath.add("/sound/down.wav");
    audioPath.add("/sound/turn.wav");

    AudioManager.get()
        .preload(
            audioPath,
            new OnPreloadListener() {

              @Override
              public void onLoaded(String path) {
                SwingUtilities.invokeLater(() -> dialog.setLabelText("load audio : " + path));
              }

              @Override
              public void onCompleted() {
                SwingUtilities.invokeLater(
                    () -> {
                      dialog.dispose();
                      Tetris.this.setVisible(true);
                    });
              }
            });
  }

  /** 讀取視窗. */
  public DialogLabel newLoadingDialog() {
    DialogLabel dialog = new DialogLabel();
    dialog.setLayout(new GridBagLayout());
    dialog.setLableFont(new Font("Dialog", Font.ITALIC, 14));
    dialog.setSize(250, 50);
    dialog.setLocation(
        getWidth() + (dialog.getWidth() / 2), getHeight() + (dialog.getHeight() / 2));
    dialog.setResizable(false);
    dialog.setModal(false);
    dialog.setUndecorated(true);
    dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    return dialog;
  }

  /**
   * 主程式.
   *
   * @param args 參數
   */
  public static void main(String[] args) {
    Debug.get().console(false);

    Config.get().setDarkMode(true);

    Tetris tetris = new Tetris();

    tetris.setTitle("俄羅斯方塊-" + Config.get().getVersion());
    tetris.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    tetris.setSize(Config.get().zoom(350), Config.get().zoom(480) + 20);

    // 遊戲啟動後畫面會置中
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    double x = (1 - (screen.getHeight() / tetris.getHeight())) / 2;
    double y = (1 - (screen.getWidth() / tetris.getWidth())) / 2;
    tetris.setLocation((int) x, (int) y);

    tetris.setLocationRelativeTo(null);

    tetris.setResizable(false); // 視窗放大按鈕無效
    tetris.initialize();
  }
}
