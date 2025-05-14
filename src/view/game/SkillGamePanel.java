package view.game;

import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 技能模式面板，含四个技能按钮，鼠标悬停显示文字，默认显示图标。
 */
public class SkillGamePanel extends AbstractGamePanel {

    private final String[] skillNames = {"破阵", "摘星", "风云", "无常"};
    private final String[] iconPaths = {
            "skill_remove.png",
            "skill_highlight.png",
            "skill_shuffle.png",
            "skill_random.png"
    };

    public SkillGamePanel(MapModel model) {
        super(model);
        initSkillButtons();
    }

    private void initSkillButtons() {
        skillPanel.setLayout(new GridLayout(2, 2, 5, 5));  // 四宫格布局
        skillPanel.removeAll();

        for (int i = 0; i < 4; i++) {
            String skillName = skillNames[i];
            String iconPath = iconPaths[i];
            JPanel buttonPanel = createSkillButton(skillName, iconPath);  // 修改返回类型为 JPanel
            skillPanel.add(buttonPanel);
        }

        skillPanel.revalidate();
        skillPanel.repaint();
    }

    private JPanel createSkillButton(String text, String iconPath) {
        JButton button = new JButton();
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        // 图标加载
        ImageIcon icon = loadIcon(iconPath);
        button.setIcon(icon);

        // 文字标签
        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setFont(new Font("楷体", Font.BOLD, 20));
        textLabel.setForeground(Color.WHITE);
        textLabel.setVisible(false);
        textLabel.setOpaque(false);

        // 覆盖层式布局
        JPanel layered = new JPanel(new BorderLayout());
        layered.setOpaque(false);
        layered.add(button, BorderLayout.CENTER);
        layered.add(textLabel, BorderLayout.SOUTH);

        // 悬停效果切换
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(null);
                textLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(icon);
                textLabel.setVisible(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleSkill(text); // 根据文字触发技能
            }
        });

        return wrapWithPanel(layered);  // 返回 JPanel
    }

    // 包装按钮为一个透明面板，方便布局与美观
    private JPanel wrapWithPanel(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }

    private void handleSkill(String skillName) {
        switch (skillName) {
            case "消除" -> System.out.println("触发技能：消除");
            case "高亮" -> System.out.println("触发技能：高亮可移动");
            case "打乱" -> System.out.println("触发技能：重新打乱");
            case "随机" -> {
                int idx = (int) (Math.random() * 3);
                handleSkill(skillNames[idx]);
            }
        }
    }

    private ImageIcon loadIcon(String path) {
        try {
            var url = getClass().getClassLoader().getResource(path);
            if (url != null) {
                Image image = new ImageIcon(url).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            } else {
                System.err.println("找不到图标: " + path);
                return null;
            }
        } catch (Exception e) {
            System.err.println("图标加载失败: " + path);
            return null;
        }
    }
}
