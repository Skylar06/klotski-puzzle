package view.game;

import model.MapModel;
import view.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StoryGamePanel extends AbstractGamePanel {
    private boolean isStoryCompleted = false;
    private static String STORY_TEXT = "在这个混乱的时代，曹操和孙权的势力激烈对抗，刘备则在夹缝中寻找生存之道......";

    public StoryGamePanel(MapModel model) {
        super(model);
    }

    @Override
    public void initialGame() {
        // 不立即调用 super.initialGame()，避免提前开始计时
        showStory();
    }

    private void showStory() {
        // 设置状态面板为垂直居中布局，并设置边距
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.removeAll();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 添加边距

        JTextPane storyPane = createStyledTextPane("", "楷体", Font.BOLD, 14, Color.WHITE);

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
                if (index < STORY_TEXT.length()) {
                    String currentText = STORY_TEXT.substring(0, index + 1);
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
        STORY_TEXT = getStoryTextByLanguage(currentLanguage);

        // 如果状态面板当前显示剧情，则更新剧情文本
        if (!isStoryCompleted) {
            showStoryTextWithTypingEffect();
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

    private String getStoryTextByLanguage(Language language) {
        switch (language) {
            case ENGLISH:
                return "In this chaotic era, the forces of Cao Cao and Sun Quan clash fiercely, while Liu Bei seeks survival in between...";
            case CHINESE:
            default:
                return "在这个混乱的时代，曹操和孙权的势力激烈对抗，刘备则在夹缝中寻找生存之道......";
        }
    }


    private void showStoryTextWithTypingEffect() {
        statusPanel.removeAll();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // 添加边距

        JTextPane storyPane = createStyledTextPane("", "楷体", Font.BOLD, 14, Color.WHITE);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(storyPane);
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();

        Timer storyTimer = new Timer(100, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < STORY_TEXT.length()) {
                    String currentText = STORY_TEXT.substring(0, index + 1);
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

        isStoryCompleted = true;
    }
}
