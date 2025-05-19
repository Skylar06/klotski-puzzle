package view.game;

import controller.GameController;
import model.LevelManager;
import model.MapModel;
import view.Language;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GamePanel extends JPanel {
    private AbstractGamePanel currentPanel;
    private boolean lastUsedTimeLimit = false;
    private int mode;
    private GameController controller;
    public int level = 0;
    public static GamePanel instance;

    public GamePanel(MapModel model, int mode) {
        this.mode = mode;
        setLayout(new BorderLayout());

        instance = this;
        currentPanel = createRandomMode(model, mode);
        add(currentPanel, BorderLayout.CENTER);

        MusicPlayer player = MusicPlayer.getInstance();
        player.play("/bgm.wav");

        currentPanel.requestFocusInWindow();
    }

    private AbstractGamePanel createRandomMode(MapModel model, int m) {
        Random random = new Random();
        int min = 10, max = 20;
        int randomNumber = random.nextInt(21) + 10;
        return switch (m) {
            case 1 -> new StoryGamePanel(model, level);
            case 2 -> {
                lastUsedTimeLimit = !lastUsedTimeLimit;
                if (lastUsedTimeLimit) {
                    yield new TimeLimitGamePanel(model);
                } else {
                    yield new StepsLimitGamePanel(model, randomNumber);
                }
            }
            case 0 -> new EffectGamePanel(model);
            default -> new StoryGamePanel(model, level);
        };
    }

    public static void nextLevel() {
        if (instance != null) {
            instance.loadNextLevel();
        }
    }

    private void loadNextLevel() {
        level++;
        LevelManager.nextLevel();
        MapModel newMap = new MapModel(LevelManager.getCurrentMap());

        remove(currentPanel);
        currentPanel = createRandomMode(newMap, mode);
        add(currentPanel, BorderLayout.CENTER);
        this.controller.setModel(newMap);
        if (controller != null) {
            currentPanel.setController(controller);
            //currentPanel.addKeyListener((KeyListener) controller);
        }

        currentPanel.setFocusable(true);
        SwingUtilities.invokeLater(() -> {
            currentPanel.requestFocusInWindow();
            currentPanel.initialGame();
            currentPanel.repaint();
        });

        revalidate();
        repaint();
    }

    public void setController(controller.GameController controller) {
        this.controller = controller;
        currentPanel.setController(controller);
    }

    public void updateLanguageTexts(Language currentLanguage) {
        currentPanel.updateLanguageTexts(currentLanguage);
    }


    public void setStepLabel(JLabel label) {
        currentPanel.setStepLabel(label);
    }

    public void initialGame() {
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
        return currentPanel.getGirdSize();
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
