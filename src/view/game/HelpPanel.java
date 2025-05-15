package view.game;

import javax.swing.*;
import javax.swing.border.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class HelpPanel extends JPanel {
    private final JList<String> sectionList;
    private JEditorPane contentPane;
    private final Map<String, String> contentMap;
    private Image bgImage;

    public HelpPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 241, 229)); // 宣纸底色

        // 加载背景图资源
        URL bgUrl = getClass().getResource("/help_bg.jpg");
        if (bgUrl != null) {
            bgImage = new ImageIcon(bgUrl).getImage();
        } else {
            System.err.println("背景图加载失败，请检查路径！");
        }

        // 内容映射
        contentMap = new LinkedHashMap<>();
        initContentMap();

        // 左侧目录
        DefaultListModel<String> listModel = new DefaultListModel<>();
        contentMap.keySet().forEach(listModel::addElement);
        sectionList = new JList<>(listModel);
        sectionList.setFont(new Font("楷体", Font.PLAIN, 20));
        sectionList.setBackground(new Color(252, 248, 237));
        sectionList.setSelectionBackground(new Color(139, 94, 60)); // 褐红
        sectionList.setSelectionForeground(Color.WHITE);
        sectionList.setFixedCellHeight(50); // 增加行高，让文字不挤
        sectionList.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(139, 94, 60), 2, true),
                "帮助目录", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("楷体", Font.BOLD, 20), new Color(62, 47, 28))
        );

        sectionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER); // 文字居中
                return label;
            }
        });

        // 鼠标悬停效果
        sectionList.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int index = sectionList.locationToIndex(e.getPoint());
                if (index != -1 && index != sectionList.getSelectedIndex()) {
                    sectionList.setSelectedIndex(index);
                }
            }
        });

        sectionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = sectionList.getSelectedValue();
                contentPane.setText(contentMap.getOrDefault(selected, "<html><body>暂无内容</body></html>"));
                contentPane.setCaretPosition(0);
                playClickSound(); // 播放音效
            }
        });

        JScrollPane listScrollPane = new JScrollPane(sectionList);
        listScrollPane.setPreferredSize(new Dimension(220, 0));

        // 右侧内容区
        contentPane = new JEditorPane();
        contentPane.setEditable(false);
        contentPane.setContentType("text/html");
        contentPane.setOpaque(false); // 背景透明以显示背景图
        contentPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        contentPane.setFont(new Font("楷体", Font.PLAIN, 18));

        JScrollPane contentScrollPane = new JScrollPane(contentPane) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        contentScrollPane.getViewport().setOpaque(false);
        contentScrollPane.setOpaque(false);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(new Color(139, 94, 60), 2));

        // 分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, contentScrollPane);
        splitPane.setDividerLocation(220);
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        // 默认选中第一个
        sectionList.setSelectedIndex(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void initContentMap() {
        contentMap.put("游戏简介", """
            <html><body style='font-family:KaiTi; color:#FFFFFF; text-align:center;'>
            <h2>游戏简介</h2>
            <p>《三国华容道》是一款结合三国题材与经典滑块玩法的益智游戏。</p>
            </body></html>
            """);

        contentMap.put("基本操作", """
            <html><body style='font-family:KaiTi; color:#FFFFFF; text-align:center;'>
            <h2>基本操作</h2>
            <ul style='list-style:none; padding-left:0;'>
              <li>使用鼠标点击滑动方块</li>
              <li>目标：帮助“曹操”从出口逃出</li>
            </ul>
            </body></html>
            """);

        contentMap.put("技能说明", """
            <html><body style='font-family:KaiTi; color:#FFFFFF; text-align:center;'>
            <h2>技能说明</h2>
            <p>每种关卡可能带有不同的技能按钮，可用于重置、移动等。</p>
            </body></html>
            """);

        contentMap.put("关卡类型", """
            <html><body style='font-family:KaiTi; color:#FFFFFF; text-align:center;'>
            <h2>关卡类型</h2>
            <ul style='list-style:none; padding-left:0;'>
            <li>剧情关卡</li>
            <li>技能关卡</li>
            <li>滑动特效关卡</li>
            </ul>
            </body></html>
            """);

        contentMap.put("通关判定", """
            <html><body style='font-family:KaiTi; color:#FFFFFF; text-align:center;'>
            <h2>通关判定</h2>
            <p>当“曹操”方块移动到指定出口区域即为胜利。</p>
            </body></html>
            """);

        contentMap.put("胜利结算", """
            <html><body style='font-family:KaiTi; color:#FFFFFF; text-align:center;'>
            <h2>胜利结算</h2>
            <p>显示分数、最少步数、最快时间、成就与排行榜入口。</p>
            </body></html>
            """);

        contentMap.put("历史背景", """
            <html><body style='font-family:KaiTi; color:#FFFFFF; text-align:center;'>
            <h2>历史背景</h2>
            <p>“华容道”出自《三国演义》——曹操兵败后，被关羽放行的一段故事。</p>
            </body></html>
            """);
    }

    private void playClickSound() {
        try {
            URL soundURL = getClass().getResource("/clickBtn.wav");
            if (soundURL == null) {
                System.err.println("音效文件未找到！");
                return;
            }
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试用主函数
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("三国华容道 - 帮助文档");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setContentPane(new HelpPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
