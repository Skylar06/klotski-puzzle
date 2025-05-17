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
    private int remainingSeconds;

    public TimeLimitGamePanel(MapModel model) {
        super(model);
        this.remainingSeconds = COUNTDOWN_SECONDS;
        replaceTimeLabel();
        startCountdown();
    }

    private void replaceTimeLabel() {
        Component[] comps = statusPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel lbl && lbl.getText().startsWith("时间")) {
                statusPanel.remove(lbl);
                break;
            }
        }
        countdownLabel = createStyledLabel("剩余: " + formatTime(remainingSeconds),
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
    public void updateLanguageTexts(Language currentLanguage) {
        updateCommonLabels(currentLanguage);
        // 更新步数标签、时间标签的文本
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "剩余: " : "Remaining: ";

        // 更新步数标签、时间标签的文本
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
    }

    private void startCountdown() {
        countdownTimer = new Timer(1000, e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                countdownLabel.setText("剩余: " + formatTime(remainingSeconds));
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
        JOptionPane.showMessageDialog(this, "时间到！挑战失败。");
        boardPanel.setEnabled(false);
    }
}
