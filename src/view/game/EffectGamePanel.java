package view.game;

import controller.GameController;
import model.MapModel;
import view.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffectGamePanel extends AbstractGamePanel {
    private boolean isEffect = false;

    public enum EffectType {
        MOVE, DISABLE, MIRROR
    }

    private EffectType currentEffect = EffectType.MOVE;
    private static String effectText = "当前关卡特效：\n缓慢移动";
    private boolean isMirror = false;
    private static int effectIndex;

    public EffectGamePanel(MapModel model) {
        super(model);

        effectIndex = (effectIndex + 1) % 3;
        switch (effectIndex) {
            case 1:
                currentEffect = EffectType.MOVE;
                break;
            case 2:
                currentEffect = EffectType.MIRROR;
                break;
            case 0:
                currentEffect = EffectType.DISABLE;
                break;
        }

        effectText = getEffectTextByLanguage(Language.CHINESE);

        clearSkillButtons();
    }

    @Override
    public void initialGame() {
        showEffectTextWithTypingEffect();
    }

    @Override
    public void updateLanguageTexts(Language currentLanguage) {
        updateCommonLabels(currentLanguage);
        effectText = getEffectTextByLanguage(currentLanguage);

        if (statusPanel != null) {
            showEffectTextWithTypingEffect();
            switchToGamePanel();
        }

        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
    }

    private String getEffectTextByLanguage(Language language) {
        switch (currentEffect) {
            case MOVE:
                return (language == Language.CHINESE) ? "当前关卡特效：\n缓慢移动" : "Current Level Effect:\nSlow Movement";
            case DISABLE:
                return (language == Language.CHINESE) ? "当前关卡特效：\n禁用方块" : "Current Level Effect:\nDisable Block";
            case MIRROR:
                return (language == Language.CHINESE) ? "当前关卡特效：\n镜像模式" : "Current Level Effect:\nMirror Mode";
            default:
                return "";
        }
    }

    private void showEffectTextWithTypingEffect() {
        statusPanel.removeAll();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JTextPane storyPane = createStyledTextPane("", "楷体", Font.BOLD, 16, Color.WHITE);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(storyPane);
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        Timer storyTimer = new Timer(100, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < effectText.length()) {
                    String currentText = effectText.substring(0, index + 1);
                    storyPane.setText(currentText);
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();

                    Timer stopTimer = new Timer(2000, e2 -> switchToGamePanel());
                    stopTimer.setRepeats(false);
                    stopTimer.start();
                }
            }
        });
        storyTimer.start();
    }

    private JTextPane createStyledTextPane(String text, String fontName, int fontStyle, int fontSize, Color color) {
        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setFont(new Font(fontName, fontStyle, fontSize));
        textPane.setForeground(color);
        textPane.setOpaque(false);
        textPane.setEditable(false);
        textPane.setFocusable(false);
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textPane.setPreferredSize(new Dimension(400, 130));
        return textPane;
    }

    private void switchToGamePanel() {
        statusPanel.removeAll();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        stepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        getTimeLabel().setAlignmentX(Component.CENTER_ALIGNMENT);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(stepLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(getTimeLabel());
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        super.initialGame();

        super.setController(this.controller);
        isEffect = true;
        startEffect();
    }

    private void startEffect() {
        switch (currentEffect) {
            case MOVE:
                setMirror(false);
                moveEffect();
                break;
            case DISABLE:
                setMirror(false);
                disableEffect();
                break;
            case MIRROR:
                mirrorEffect();
                break;
        }
    }

    public void setMirror(boolean yes) {
        this.isMirror = yes;
        if (controller != null) {
            controller.setMirrorMode(yes);
        }
    }

    @Override
    public void setController(GameController controller) {
        super.setController(controller);

        // 清除特效模式可能残留的状态
        controller.setMirrorMode(false);
        controller.setSlowMode(false);

        // 如果可能存在禁用的 box，也可清空（可选）
        for (Component comp : boardPanel.getComponents()) {
            if (comp instanceof BoxComponent box) {
                box.setDisabled(false);
            }
        }

        // 设置当前镜像模式状态
        controller.setMirrorMode(this.isMirror);
    }

    private void moveEffect() {
        if (controller != null) {
            controller.setSlowMode(true);
        }
    }

    private void disableEffect() {
//        System.out.println("禁用方块特效已启用");
        List<BoxComponent> boxes = new ArrayList<>();
        for (Component comp : boardPanel.getComponents()) {
            if (comp instanceof BoxComponent box) {
                if (box.getType() == 1 && !box.isDisabled()) {
                    boxes.add(box);
                }
            }
        }

        if (!boxes.isEmpty()) {
            BoxComponent selected = boxes.get(new Random().nextInt(boxes.size()));
            selected.setDisabled(true);
//            System.out.println("禁用了位置 (" + selected.getRow() + ", " + selected.getCol() + ")");
        } else {
//            System.out.println("未找到可禁用的 1x1 方块");
        }
    }

    private void mirrorEffect() {
        setMirror(true);
//        System.out.println("镜像模式特效已启用");
    }
}
