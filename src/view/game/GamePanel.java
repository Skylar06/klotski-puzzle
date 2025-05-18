package view.game;

import controller.GameController;
import model.LevelManager;
import model.MapModel;
import view.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * GamePanel 作为桥梁，根据传入的 MapModel 随机加载一个具体的游戏模式面板。
 */
public class GamePanel extends JPanel {
    private AbstractGamePanel currentPanel;
    private boolean lastUsedTimeLimit = false;
    private int mode;
    private GameController controller;

    public static GamePanel instance;

    public GamePanel(MapModel model,int mode) {
        this.mode = mode;
        setLayout(new BorderLayout());

        instance = this; // ✅ 注册全局引用

        currentPanel = createRandomMode(model,mode);
        add(currentPanel, BorderLayout.CENTER);

        MusicPlayer player = MusicPlayer.getInstance();
        player.play("/bgm.wav");

        currentPanel.requestFocusInWindow();
    }

    /**
     * 随机选择一个游戏模式面板
     */
    private AbstractGamePanel createRandomMode(MapModel model,int m) {
        return switch (m) {
            case 1 -> new StoryGamePanel(model,2);
            case 2 -> {
                lastUsedTimeLimit = !lastUsedTimeLimit;
                if (lastUsedTimeLimit) {
                    yield new TimeLimitGamePanel(model);
                } else {
                    yield new StepsLimitGamePanel(model,8);
                }
            }

            case 0 -> new EffectGamePanel(model);
            default -> new StoryGamePanel(model,2); // 兜底
        };
    }

    public static void nextLevel() {
        if (instance != null) {
            instance.loadNextLevel();
        }
    }

    private void loadNextLevel() {
        LevelManager.nextLevel();
        MapModel newMap = LevelManager.getCurrentMap();

        remove(currentPanel);
        currentPanel = createRandomMode(newMap, mode);
        add(currentPanel, BorderLayout.CENTER);

        if (controller != null) {
            currentPanel.setController(controller);
            currentPanel.addKeyListener((KeyListener) controller);
        }

        currentPanel.setFocusable(true);
        SwingUtilities.invokeLater(() -> {
            currentPanel.requestFocusInWindow();
            currentPanel.initialGame(); // 重新初始化，刷新棋盘数据和状态
            currentPanel.repaint();     // 强制重绘
        });

        revalidate();
        repaint();
    }

    /**
     * 暴露设置控制器的方法（外部可以使用）
     */
    public void setController(controller.GameController controller) {
        this.controller = controller;
        currentPanel.setController(controller);
    }

    public void updateLanguageTexts(Language currentLanguage) {
        currentPanel.updateLanguageTexts(currentLanguage);
    }

    /**
     * 设置步数标签（显示 step）
     */
    public void setStepLabel(JLabel label) {
        currentPanel.setStepLabel(label);
    }

    public void initialGame(){
        currentPanel.initialGame();
    }

    public void undoMove() {
        currentPanel.undoMove();
    }

    public BoxComponent getSelectedBox() {
        return currentPanel.getSelectedBox();
    }

    public BoxComponent afterMove() {
        return currentPanel.afterMove();
    }

    public int getGRID_SIZE() {
        return currentPanel.getGRID_SIZE();
    }

    public JLabel getStepLabel() {
        return currentPanel.stepLabel;
    }

    public int getSteps() {
        return currentPanel.steps;
    }

    public void setSteps(int steps) {
        currentPanel.steps = steps;
    }

    /**
     * 提供读取和保存功能
     */
    public void saveGame(String path) {
        currentPanel.saveGame(path);
    }

    public void loadGame(String path) {
        currentPanel.loadGame(path);
    }

    public AbstractGamePanel getCurrentPanel() {
        return currentPanel;
    }

}
