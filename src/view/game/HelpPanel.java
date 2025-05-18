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
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>游戏简介</h2>
        <p>《三国华容道》是一款融合三国历史、古风美术与经典滑块解谜的益智策略游戏。</p>
        <p>玩家需通过合理移动方块，助“曹操”突围华容道。游戏设有多种模式与特效，考验操作与策略并重。</p>
        </body></html>
    """);

        contentMap.put("基本操作", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>基本操作</h2>
        <ul style='list-style:none; padding-left:0;'>
          <li>使用鼠标点击或滑动任意方块进行操作</li>
          <li>部分设备支持手势滑动控制方向</li>
          <li>目标：将“曹操”移动至棋盘底部中央出口</li>
          <li>技能按钮位于状态区下方，可增强通关效率</li>
        </ul>
        </body></html>
    """);

        contentMap.put("关卡类型", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>关卡类型</h2>
        <ul style='list-style:none; padding-left:0;'>
          <li><b>剧情模式：</b>每一关讲述三国经典桥段，左上角显示故事文本</li>
          <li><b>极限模式：</b>时间紧迫，步数有限，挑战你的极限操作</li>
          <li><b>特效模式：</b>含有各种棋盘视觉效果，如镜像、震动、缓动滑移等</li>
        </ul>
        </body></html>
    """);

        contentMap.put("技能说明", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>技能说明</h2>
        <p>每个关卡支持技能使用，每种技能初始可用1次，之后观看视频可补充使用次数。</p>
        <ul style='list-style:none; padding-left:0;'>
          <li><b>摘星：</b>高亮所有可移动的方块（不包含 2×2 的曹操）</li>
          <li><b>破阵：</b>移除一个随机的 1×1 方块，为曹操开路</li>
          <li><b>风云：</b>打乱当前棋盘布局（不会卡死）</li>
          <li><b>无常：</b>随机交换两个方块位置，可能意外破局</li>
        </ul>
        <p>技能按钮上悬停显示使用次数，点击后若可用将生效，若已用完可观看广告补充。</p>
        </body></html>
    """);

        contentMap.put("通关判定", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>通关判定</h2>
        <p>当曹操（2×2 方块）完整移动至棋盘底部中央（出口位置）时，即视为通关。</p>
        <p>极限模式可能要求在限定步数或时间内达成此目标。</p>
        </body></html>
    """);

        contentMap.put("胜利结算", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>胜利结算</h2>
        <p>通关后将弹出胜利面板，展示以下内容：</p>
        <ul style='list-style:none; padding-left:0;'>
          <li><b>本关得分：</b>综合用时与步数计算</li>
          <li><b>最快时间：</b>历史最快通关记录</li>
          <li><b>最少步数：</b>历史最优解法步数</li>
          <li><b>封神榜入口：</b>可查看全球排行前十</li>
          <li><b>再启、转战、归返：</b>分别代表重开、下一关与返回主菜单</li>
        </ul>
        </body></html>
    """);

        contentMap.put("皮肤系统", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>皮肤系统</h2>
        <p>游戏支持多种主题方块皮肤，带来截然不同的视觉体验：</p>
        <ul style='list-style:none; padding-left:0;'>
          <li><b>古风：</b>典雅宣纸风格，书画质感</li>
          <li><b>武将：</b>三国武将头像还原，辨识度高</li>
          <li><b>猫猫：</b>萌系风格，趣味十足</li>
          <li><b>卡通：</b>简约风动画线条感</li>
          <li><b>奶龙：</b>软萌可爱，适合轻松氛围</li>
          <li><b>像素：</b>8-bit 复古像素风</li>
          <li><b>南科大：</b>以南科大元素为主题的纪念皮肤</li>
        </ul>
        <p>可在设置界面或登录时更换，部分皮肤可能需达成成就解锁。</p>
        </body></html>
    """);

        contentMap.put("封神榜", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>封神榜</h2>
        <p>封神榜为游戏的排行榜系统，展示表现最佳的十位玩家：</p>
        <ul style='list-style:none; padding-left:0;'>
          <li>可切换按“最少步数”或“最快时间”排名</li>
          <li>若你未进入前十，将显示第十名及你自己的成绩</li>
          <li>前三名拥有专属展示背景</li>
        </ul>
        <p>榜单每日更新一次，快来挑战极限，留下你的封神之名！</p>
        </body></html>
    """);

        contentMap.put("历史背景", """
        <html><body style='font-family:KaiTi; color:#ffffff; text-align:center;'>
        <h2>历史背景</h2>
        <p>“华容道”典故出自《三国演义》第五十回，讲述曹操败走麦城，被关羽拦截于华容道。</p>
        <p>但因旧日恩情，关羽最終义释曹操。此为三国历史中最富争议与人性光辉的篇章之一。</p>
        <p>本游戏以此为灵感，结合策略与解谜，为玩家还原这段波澜壮阔的历史瞬间。</p>
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
            frame.setSize(640, 480);
            frame.setContentPane(new HelpPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
