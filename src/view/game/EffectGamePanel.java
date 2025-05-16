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
    private boolean isEffectCompleted = false;
    private static String EFFECT_TEXT = "当前关卡特效：\n缓慢移动";
    public enum EffectType {
        MOVE, DISABLE, MIRROR
    }
    private EffectType currentEffect = EffectType.MOVE; // 默认缓动
    private BoxComponent disabledBox = null;
    private boolean mirrorMode = false;

    public EffectGamePanel(MapModel model) {
        super(model);

        currentEffect = EffectType.MIRROR;
        EFFECT_TEXT = "当前关卡特效：\n镜像模式";
//        int levelId = model.getLevelId(); // 假设你有这个方法
//        switch (levelId % 3) {
//            case 0:
//                currentEffect = EffectType.MOVE;
//                EFFECT_TEXT = "当前关卡特效：\n缓慢移动";
//                break;
//            case 1:
//                currentEffect = EffectType.DISABLE;
//                EFFECT_TEXT = "当前关卡特效：\n禁用方块";
//                break;
//            case 2:
//                currentEffect = EffectType.MIRROR;
//                EFFECT_TEXT = "当前关卡特效：\n镜像模式";
//                break;
//        }
    }

    @Override
    public void initialGame() {
        // 不立即调用 super.initialGame()，避免提前开始计时
        showMessage();
    }

    private void showMessage() {
        // 设置状态面板为垂直居中布局，并设置边距
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.removeAll();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 添加边距

        JTextPane storyPane = createStyledTextPane("", "楷体", Font.BOLD, 16, Color.WHITE);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(storyPane);
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        // 打字效果
        Timer storyTimer = new Timer(100, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < EFFECT_TEXT.length()) {
                    String currentText = EFFECT_TEXT.substring(0, index + 1);
                    storyPane.setText(currentText);
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();

                    // 剧情完成后延迟2秒开始游戏
                    Timer stopTimer = new Timer(2000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            switchToGamePanel();
                        }
                    });
                    stopTimer.setRepeats(false);
                    stopTimer.start();
                }
            }
        });
        storyTimer.start();
    }

    @Override
    public void updateLanguageTexts(Language currentLanguage) {
        updateCommonLabels(currentLanguage);
        // 根据语言设置剧情文本
        EFFECT_TEXT = getEffectTextByLanguage(currentLanguage);

        // 如果状态面板当前显示剧情，则更新剧情文本
        if (statusPanel != null && /* 判断当前是否显示剧情界面，或者用一个标志 */ true) {
            showEffectTextWithTypingEffect();
            switchToGamePanel();
        }

        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";

        // 更新步数标签、时间标签的文本
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
        // 其他可能存在的文本也要更新...
    }

    private String getEffectTextByLanguage(Language language) {
        switch (language) {
            case ENGLISH:
                return "Current Level Effect:\nSlow Movement";
            case CHINESE:
            default:
                return "当前关卡特效：\n缓慢移动";
        }
    }

    private void showEffectTextWithTypingEffect() {
        statusPanel.removeAll();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 添加边距

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
                if (index < EFFECT_TEXT.length()) {
                    String currentText = EFFECT_TEXT.substring(0, index + 1);
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
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // 内边距
        textPane.setPreferredSize(new Dimension(400, 130));  // 宽度可调节，影响换行
        return textPane;
    }

    private void switchToGamePanel() {
        statusPanel.removeAll();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 保持边距
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        // 设置标签居中
        stepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        getTimeLabel().setAlignmentX(Component.CENTER_ALIGNMENT);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(stepLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(getTimeLabel());
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        // 现在才真正初始化游戏和启动计时器
        super.initialGame();
        super.setController(this.controller);
        isEffectCompleted = true;
        startEffect();
    }

    private void startEffect() {
        switch (currentEffect) {
            case MOVE:
                setMirrorMode(false);
                applyMoveEffect();
                break;
            case DISABLE:
                setMirrorMode(false);
                applyDisableEffect();
                break;
            case MIRROR:
                applyMirrorEffect();
                break;
        }
    }

    public void setMirrorMode(boolean enabled) {
        this.mirrorMode = enabled;
        if (controller != null) {
            controller.setMirrorMode(enabled);
        }
    }

    @Override
    public void setController(GameController controller) {
        super.setController(controller);
        controller.setMirrorMode(this.mirrorMode);
    }

    private void applyMoveEffect() {
        // 启动缓动效果：棋子缓慢移动
        // 这里可以加入缓动动画的逻辑
        System.out.println("缓动移动特效已启用");
    }

    private void applyDisableEffect() {
        System.out.println("禁用方块特效已启用");

        List<BoxComponent> oneByOneBoxes = new ArrayList<>();

        for (Component comp : boardPanel.getComponents()) {
            if (comp instanceof BoxComponent box) {
                if (box.getType() == 1 && !box.isDisabled()) {
                    oneByOneBoxes.add(box);
                }
            }
        }

        if (!oneByOneBoxes.isEmpty()) {
            BoxComponent selected = oneByOneBoxes.get(new Random().nextInt(oneByOneBoxes.size()));
            selected.setDisabled(true); // 设置禁用 + 自动重绘
            System.out.println("禁用了位置 (" + selected.getRow() + ", " + selected.getCol() + ")");
        } else {
            System.out.println("未找到可禁用的 1x1 方块");
        }
    }

    private void applyMirrorEffect() {
        setMirrorMode(true);
        System.out.println("镜像模式特效已启用");
    }
}
