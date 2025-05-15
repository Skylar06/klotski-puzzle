package view.game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class PauseMenuPanel extends JFrame {
    private JPanel pasuePanel;
    private JLabel pauseLabel;  // "得分" 标签
    private JLabel timeCountLabel;
    private JLabel stepCountLabel;
    private JButton resumeButton;
    private JButton restartButton;
    private JButton mainMenuButton;
    private JButton soundToggleButton;

    // 背景图片
    private ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("pause_bg.gif"));

    public PauseMenuPanel(String time, String steps) {
        // 设置主窗口
        setTitle("暂停界面");
        setSize(500, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setLayout(new BorderLayout());

        // 创建背景面板并设置透明
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(true); // 确保背景面板本身不透明，用来绘制背景图
        add(backgroundPanel);

        // 创建胜利面板
        pasuePanel = new JPanel();
        pasuePanel.setLayout(new BoxLayout(pasuePanel, BoxLayout.Y_AXIS));  // 使用 BoxLayout 按照垂直顺序排列组件
        pasuePanel.setOpaque(false); // 胜利面板保持透明，覆盖在背景上

        // 创建一个面板来横向排列得分和其他信息
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());  // 中间得分，两边为最快通关和最少步数
        scorePanel.setOpaque(false);

        // 显示得分
        JPanel pasueHintPanel = new JPanel();
        pasueHintPanel.setLayout(new GridLayout(1, 1)); // 得分部分上下排
        pasueHintPanel.setOpaque(false);
        pauseLabel = new JLabel("暂停", JLabel.CENTER);
        pauseLabel.setFont(new Font("楷体", Font.PLAIN, 32));
        pasueHintPanel.add(pauseLabel);

        // 显示最快通关
        JPanel timeCountPanel = new JPanel();
        timeCountPanel.setLayout(new GridLayout(2, 1)); // 最快通关部分上下排
        timeCountPanel.setOpaque(false);
        JLabel timeCountTextLabel = new JLabel("用时", JLabel.CENTER);
        timeCountTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel timeCountValueLabel = new JLabel(time, JLabel.CENTER);
        timeCountValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeCountPanel.add(timeCountTextLabel);
        timeCountPanel.add(timeCountValueLabel);

        // 显示最少步数
        JPanel stepCountPanel = new JPanel();
        stepCountPanel.setLayout(new GridLayout(2, 1)); // 最少步数部分上下排
        stepCountPanel.setOpaque(false);
        JLabel stepCountTextLabel = new JLabel("步数", JLabel.CENTER);
        stepCountTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel stepCountValueLabel = new JLabel(steps, JLabel.CENTER);
        stepCountValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        stepCountPanel.add(stepCountTextLabel);
        stepCountPanel.add(stepCountValueLabel);

        // 将三个部分添加到scorePanel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 45));  // 横向排布，适当间距
        infoPanel.setOpaque(false);
        infoPanel.add(timeCountPanel);
        infoPanel.add(pasueHintPanel);
        infoPanel.add(stepCountPanel);

        // 按钮区域
        // 创建一个BoxPanel，用BoxLayout布局，垂直排列组件
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // 保证背景透明

        JPanel controlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlButtonsPanel.setOpaque(false);

        restartButton = new JButton("重新开始");
        resumeButton = new JButton("继续游戏");
        mainMenuButton = new JButton("返回菜单");

        setupButton(restartButton);
        setupButton(resumeButton);
        setupButton(mainMenuButton);

        controlButtonsPanel.add(restartButton);
        controlButtonsPanel.add(resumeButton);
        controlButtonsPanel.add(mainMenuButton);

        buttonPanel.add(controlButtonsPanel);

        // 在按钮区域添加游客模式按钮
        soundToggleButton = new JButton("切换音效");
        // 添加排行榜按钮
        soundToggleButton.setFont(new Font("楷体", Font.PLAIN, 15));
        soundToggleButton.setForeground(new Color(150, 150, 150)); // 淡灰色
        soundToggleButton.setBorderPainted(false);
        soundToggleButton.setContentAreaFilled(false);
        soundToggleButton.setFocusPainted(false);
        soundToggleButton.setOpaque(false);

        // 添加排行榜按钮到buttonPanel
        buttonPanel.add(Box.createVerticalStrut(10));  // 控制按钮与排行榜之间的间距
        buttonPanel.add(soundToggleButton);

        // 设置按钮事件
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 下一关逻辑
                System.out.println("继续游戏");
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 重新开始逻辑
                System.out.println("重新开始");
            }
        });

        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 返回主菜单逻辑
                System.out.println("返回主菜单");
            }
        });

        soundToggleButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                soundToggleButton.setForeground(new Color(255, 200, 0));  // 改为高亮色
            }

            public void mouseExited(MouseEvent e) {
                soundToggleButton.setForeground(new Color(150, 150, 150));  // 恢复默认色
            }

            public void mousePressed(MouseEvent e) {
                soundToggleButton.setForeground(new Color(255, 180, 0));  // 按下时的颜色
                // 重新开始逻辑
                System.out.println("查看排行榜");
            }
        });


        // 将组件添加到胜利面板
        pasuePanel.add(infoPanel,BorderLayout.CENTER);  // 添加得分和其他信息
        pasuePanel.add(buttonPanel,BorderLayout.SOUTH);  // 添加按钮区域

        // 将胜利面板添加到背景面板
        backgroundPanel.add(pasuePanel, BorderLayout.CENTER);
    }

    private void setupButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);  // 确保按钮背景透明
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("楷体", Font.BOLD, 18));

        // 设置按钮图标并调整尺寸
        ImageIcon originalIcon1 = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage1));

        // 设置按钮文字
        button.setText(button.getText());
        button.setForeground(new Color(60, 30, 0)); // 文字颜色
        button.setHorizontalTextPosition(SwingConstants.CENTER); // 文字居中
        button.setVerticalTextPosition(SwingConstants.CENTER);  // 文字放在图标下方

        button.setPreferredSize(new Dimension(120, 90));  // 增加高度以容纳文字
        button.setMaximumSize(new Dimension(120, 90));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setIcon(new ImageIcon(scaledImage1));
            }

            public void mouseExited(MouseEvent e) {
                button.setIcon(new ImageIcon(scaledImage1));
            }

            public void mousePressed(MouseEvent e) {
                button.setIcon(new ImageIcon(scaledImage1));
                playClickSound();
            }

            public void mouseReleased(MouseEvent e) {
                button.setIcon(new ImageIcon(scaledImage1));
            }
        });
    }

    private void playClickSound() {
        try {
            URL soundURL = getClass().getClassLoader().getResource("clickBtn.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 测试胜利界面
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PauseMenuPanel pauseMenuPanel1 = new PauseMenuPanel("2:30", "25步");
                pauseMenuPanel1.setVisible(true);
            }
        });
    }
}


