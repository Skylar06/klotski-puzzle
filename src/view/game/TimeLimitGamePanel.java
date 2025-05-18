package view.game;

import model.MapModel;
import view.Language;
import view.game.AbstractGamePanel;

import javax.swing.*;
import java.awt.*;

public class TimeLimitGamePanel extends AbstractGamePanel {
    private static final int COUNTDOWN_SECONDS = 120; // 固定倒计时
    private JLabel countdownLabel;
    private Timer countdownTimer;

    public TimeLimitGamePanel(MapModel model) {
        super(model);
    }

    private void replaceTimeLabel() {
        // 移除原有的倒计时标签
        if (countdownLabel != null) {
            statusPanel.remove(countdownLabel);
        }

        Component[] comps = statusPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel lbl && lbl.getText().startsWith("时间")) {
                statusPanel.remove(lbl);
                break;
            }
        }

        // 创建新的倒计时标签
        countdownLabel = createStyledLabel("剩余: " + formatTime(COUNTDOWN_SECONDS - this.elapsedTime),
                "楷体", Font.BOLD, 20, Color.WHITE);

        int insertIndex = 0;
        for (int i = 0; i < statusPanel.getComponentCount(); i++) {
            if (statusPanel.getComponent(i) == stepLabel) {
                insertIndex = i + 1;
                break;
            }
        }
        statusPanel.add(countdownLabel, insertIndex);
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    @Override
    public void initialGame() {
        super.initialGame();
        replaceTimeLabel();
        startCountdown();
    }

    @Override
    public void updateLanguageTexts(Language currentLanguage) {
        updateCommonLabels(currentLanguage);
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "剩余: " : "Remaining: ";
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
    }

    private void startCountdown() {
        if (countdownTimer!=null) {
            countdownTimer.start();
            return;
        }
        countdownTimer = new Timer(1000, e -> {
            if (COUNTDOWN_SECONDS - elapsedTime > 0) {
                countdownLabel.setText("剩余: " + formatTime(COUNTDOWN_SECONDS - elapsedTime));
            } else {
                countdownTimer.stop();
                timeUp();
            }
        });
        countdownTimer.start();
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void timeUp() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        view.game.LoseScreen loseScreen = new view.game.LoseScreen(
                String.format("%2d:%2d", COUNTDOWN_SECONDS / 60, COUNTDOWN_SECONDS % 60),
                String.format("%d", this.steps), Language.CHINESE);
        loseScreen.setGameController(this.controller);

        loseScreen.setVisible(true);
        boardPanel.setEnabled(false);
    }
}
