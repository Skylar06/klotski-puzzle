package view.game;

import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * GamePanel 作为桥梁，根据传入的 MapModel 随机加载一个具体的游戏模式面板。
 */
public class GamePanel extends JPanel {
    private AbstractGamePanel currentPanel;

    public GamePanel(MapModel model) {
        setLayout(new BorderLayout());

        currentPanel = createRandomMode(model);
        add(currentPanel, BorderLayout.CENTER);

        currentPanel.requestFocusInWindow();
    }

    /**
     * 随机选择一个游戏模式面板
     */
    private AbstractGamePanel createRandomMode(MapModel model) {
        int mode = new Random().nextInt(3); // 0、1、2 三种模式
        return switch (mode) {
            case 0 -> new StoryGamePanel(model);
            case 1 -> new SkillGamePanel(model);
            case 2 -> new EffectGamePanel(model);
            default -> new StoryGamePanel(model); // 兜底
        };
    }

    /**
     * 暴露设置控制器的方法（外部可以使用）
     */
    public void setController(controller.GameController controller) {
        currentPanel.setController(controller);
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
