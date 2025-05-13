package view.game;

import model.MapModel;

import javax.swing.*;
import java.awt.*;

/**
 * 剧情模式面板：在状态区显示一段剧情文本。
 */
public class StoryGamePanel extends AbstractGamePanel {

    public StoryGamePanel(MapModel model) {
        super(model);
//        showStory();
    }

    /**
     * 在 statusPanel 中添加剧情文本
     */
//    private void showStory() {
//        statusPanel.setLayout(new BorderLayout());
//
//        JTextArea storyArea = new JTextArea();
//        storyArea.setText("""
//                曹操兵困华容道，诸葛亮巧设机关。
//                主公必须在规定步数内脱困，否则前功尽弃。
//                请谨慎行事，每一步都至关重要……
//                """);
//        storyArea.setLineWrap(true);
//        storyArea.setWrapStyleWord(true);
//        storyArea.setEditable(false);
//        storyArea.setFont(new Font("楷体", Font.PLAIN, 16));
//        storyArea.setOpaque(false);
//
//        statusPanel.add(storyArea, BorderLayout.CENTER);
//    }
}
