package view.game;

import model.MapModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EffectGamePanel extends AbstractGamePanel {

    private static final String Effect_TEXT = "当前关卡特效：\n缓慢移动";

    public EffectGamePanel(MapModel model) {
        super(model);
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
                if (index < Effect_TEXT.length()) {
                    String currentText = Effect_TEXT.substring(0, index + 1);
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

        startEffect();
    }

    private void startEffect() {
//        // 根据不同特效类型启用不同效果
//        switch (effectMessage) {
//            case "当前关卡特效：缓动移动":
//                applyMoveEffect();
//                break;
//            case "当前关卡特效：禁用方块":
//                applyDisableEffect();
//                break;
//            case "当前关卡特效：镜像模式":
//                applyMirrorEffect();
//                break;
//        }
    }

    private void applyMoveEffect() {
        // 启动缓动效果：棋子缓慢移动
        // 这里可以加入缓动动画的逻辑
        System.out.println("缓动移动特效已启用");
    }

    private void applyDisableEffect() {
        // 启动禁用方块特效
        // 例如禁用某些方块
        System.out.println("禁用方块特效已启用");
    }

    private void applyMirrorEffect() {
        // 启动镜像特效
        // 例如对棋盘进行镜像翻转
        System.out.println("镜像模式特效已启用");
    }
}
