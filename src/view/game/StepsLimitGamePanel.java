package view.game;

import controller.GameController;
import model.MapModel;
import view.Language;

import javax.swing.*;
import java.awt.*;

public class StepsLimitGamePanel extends AbstractGamePanel {
    private int maxSteps;
    private JLabel remainingStepsLabel;

    public StepsLimitGamePanel(MapModel model, int maxSteps) {
        super(model);
        this.maxSteps = maxSteps;
        initRemainingStepsLabel();
    }


    private void initRemainingStepsLabel() {
        //清空状态区！！
        statusPanel.removeAll();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        //标签
        remainingStepsLabel = createStyledLabel("剩余步数: " + maxSteps, "楷体", Font.BOLD, 22, Color.RED);
        timeLabel = createStyledLabel("时间: 00:00", "楷体", Font.BOLD, 20, Color.WHITE);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(remainingStepsLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(timeLabel);
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();
    }

    @Override
    public BoxComponent afterMove() {
        BoxComponent result = super.afterMove();

        int remaining = maxSteps - steps;

        if (remaining >= 0) {
            updateRemainingStepsLabel();
        } else {
            boardPanel.setEnabled(false);
            view.game.LoseScreen loseScreen = new view.game.LoseScreen(
                    String.format("%2d:%2d", this.elapsedTime/60, this.elapsedTime % 60),
                    String.format("%d", this.steps), Language.CHINESE);
            loseScreen.setGameController(this.controller);
            loseScreen.setVisible(true);
            boardPanel.setEnabled(false);
        }

        return result;
    }

    @Override
    public void setController(GameController controller) {
        super.setController(controller);

        controller.setMirrorMode(false);
        controller.setSlowMode(false);

        for (Component comp : boardPanel.getComponents()) {
            if (comp instanceof BoxComponent box) {
                box.setDisabled(false);
            }
        }
    }

    @Override
    public void updateLanguageTexts(Language currentLanguage) {
        updateCommonLabels(currentLanguage);
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";
        stepLabel.setText((currentLanguage == Language.CHINESE ? "剩余：" : "Remaining: ") + steps);
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
    }

    private void updateRemainingStepsLabel() {
        if (remainingStepsLabel != null) {
            int remaining = maxSteps - steps;
            remainingStepsLabel.setText("剩余: " + Math.max(0, remaining));
        }
    }

    @Override
    public void initialGame() {
        super.initialGame();
        setSteps(0);
        updateRemainingStepsLabel();

        if (!boardPanel.isEnabled()) {
            boardPanel.setEnabled(true);
        }
    }
}