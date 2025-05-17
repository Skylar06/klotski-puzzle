package view.game;

import controller.GameController;
import view.Language;

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

public class VictoryScreen extends JFrame {

    private JPanel victoryPanel;
    private JLabel victoryLabel;
    private JLabel scoreTextLabel;  // "得分" 标签
    private JLabel scoreLabel;      // 分数
    private JLabel fastestTimeLabel;
    private JLabel leastMovesLabel;
    private JButton nextLevelButton;
    private JButton restartButton;
    private JButton mainMenuButton;
    private JButton leaderboardButton;
    private GameController gameController;
    // 背景图片
    private ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("victory_bg.gif"));

    public VictoryScreen(int score, String time, String moves, String fastestTime, String leastMoves,Language currentLanguage) {
        // 设置主窗口
        setSize(600, 410);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setUndecorated(true); // 去除标题栏
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
        victoryPanel = new JPanel();
        victoryPanel.setLayout(new BoxLayout(victoryPanel, BoxLayout.Y_AXIS));  // 使用 BoxLayout 按照垂直顺序排列组件
        victoryPanel.setOpaque(false); // 胜利面板保持透明，覆盖在背景上

        // 胜利提示，调整位置
        String message = "";
        if (currentLanguage == Language.CHINESE) {
            message = "贺捷！天机已破";
        } else {
            message = "Victory!";
        }
        victoryLabel = new JLabel(message, JLabel.CENTER);
        victoryLabel.setFont(new Font("楷体", Font.BOLD, 30));
        victoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 设置顶部间距
        JPanel victoryLabelPanel = new JPanel();
        victoryLabelPanel.setOpaque(false);
        victoryLabelPanel.add(victoryLabel);
        victoryLabelPanel.setPreferredSize(new Dimension(getWidth(), 50)); // 增加顶部间距，稍微下移

        // 创建一个面板来横向排列得分和其他信息
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());  // 中间得分，两边为最快通关和最少步数
        scorePanel.setOpaque(false);

        // 显示得分
        JPanel scoreValuePanel = new JPanel();
        scoreValuePanel.setLayout(new GridLayout(2, 1)); // 得分部分上下排
        scoreValuePanel.setOpaque(false);
        scoreTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"得分":"Score", JLabel.CENTER);
        scoreTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        scoreLabel = new JLabel(String.valueOf(score), JLabel.CENTER);
        scoreLabel.setFont(new Font("楷体", Font.PLAIN, 40));
        scoreValuePanel.add(scoreTextLabel);
        scoreValuePanel.add(scoreLabel);

        // 显示最快通关
        JPanel timeValuePanel = new JPanel();
        timeValuePanel.setLayout(new GridLayout(2, 1)); // 最快通关部分上下排
        timeValuePanel.setOpaque(false);
        JLabel timeTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"用时":"Time", JLabel.CENTER);
        timeTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel timeValueLabel = new JLabel(time, JLabel.CENTER);
        timeValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeValuePanel.add(timeTextLabel);
        timeValuePanel.add(timeValueLabel);

        // 显示最快通关
        JPanel fastestValuePanel = new JPanel();
        fastestValuePanel.setLayout(new GridLayout(2, 1)); // 最快通关部分上下排
        fastestValuePanel.setOpaque(false);
        JLabel fastestTimeTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"最快通关":"Fastest", JLabel.CENTER);
        fastestTimeTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel fastestTimeValueLabel = new JLabel(fastestTime, JLabel.CENTER);
        fastestTimeValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        fastestValuePanel.add(fastestTimeTextLabel);
        fastestValuePanel.add(fastestTimeValueLabel);

        JPanel movesValuePanel = new JPanel();
        movesValuePanel.setLayout(new GridLayout(2, 1)); // 最少步数部分上下排
        movesValuePanel.setOpaque(false);
        JLabel movesTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"步数":"Steps", JLabel.CENTER);
        movesTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel movesValueLabel = new JLabel(moves, JLabel.CENTER);
        movesValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        movesValuePanel.add(movesTextLabel);
        movesValuePanel.add(movesValueLabel);

        // 显示最少步数
        JPanel leastValuePanel = new JPanel();
        leastValuePanel.setLayout(new GridLayout(2, 1)); // 最少步数部分上下排
        leastValuePanel.setOpaque(false);
        JLabel leastMovesTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"最少步数":"Least", JLabel.CENTER);
        leastMovesTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel leastMovesValueLabel = new JLabel(leastMoves, JLabel.CENTER);
        leastMovesValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        leastValuePanel.add(leastMovesTextLabel);
        leastValuePanel.add(leastMovesValueLabel);

        // 将三个部分添加到scorePanel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 45));  // 横向排布，适当间距
        infoPanel.setOpaque(false);
        infoPanel.add(timeValuePanel);
        infoPanel.add(fastestValuePanel);
        infoPanel.add(scoreValuePanel);
        infoPanel.add(leastValuePanel);
        infoPanel.add(movesValuePanel);

        // 按钮区域
        // 创建一个BoxPanel，用BoxLayout布局，垂直排列组件
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // 保证背景透明

        JPanel controlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlButtonsPanel.setOpaque(false);

        restartButton = new JButton();
        nextLevelButton = new JButton();
        mainMenuButton = new JButton();

        setupButton(restartButton);
        setupButton(nextLevelButton);
        setupButton(mainMenuButton);

        if (currentLanguage == Language.CHINESE) {
            restartButton.setText("再启");
        } else {
            restartButton.setText("Restart");
        }

        if (currentLanguage == Language.CHINESE) {
            nextLevelButton.setText("转战");
        } else {
            nextLevelButton.setText("Continue");
        }

        if (currentLanguage == Language.CHINESE) {
            mainMenuButton.setText("归返");
        } else {
            mainMenuButton.setText("Back");
        }

        controlButtonsPanel.add(restartButton);
        controlButtonsPanel.add(nextLevelButton);
        controlButtonsPanel.add(mainMenuButton);

        buttonPanel.add(controlButtonsPanel);

        // 在按钮区域添加游客模式按钮
        JPanel leaderboardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leaderboardPanel.setOpaque(false);

        leaderboardButton = new JButton();
        if (currentLanguage == Language.CHINESE) {
            leaderboardButton.setText("封神榜");
        } else {
            leaderboardButton.setText("Leader Board");
        }

        // 添加排行榜按钮
        leaderboardButton.setFont(new Font("楷体", Font.PLAIN, 18));
        leaderboardButton.setForeground(new Color(50, 50, 50)); // 淡灰色
        leaderboardButton.setBorderPainted(false);
        leaderboardButton.setContentAreaFilled(false);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setOpaque(false);
        leaderboardButton.setMargin(new Insets(0, 0, 0, 0));

        buttonPanel.add(Box.createVerticalStrut(0));  // 只留3像素间距，默认一般是5~10像素
        leaderboardPanel.add(leaderboardButton);
        buttonPanel.add(leaderboardPanel);

        // 设置按钮事件
        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 下一关逻辑
                System.out.println("进入下一关");
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 重新开始逻辑
                System.out.println("重新开始");
            }
        });

        mainMenuButton.addActionListener(e -> {
            this.setVisible(false);
            this.gameController.levelSelectFrame.setVisible(true);
        });

        leaderboardButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 100, 0));  // 改为高亮色
            }

            public void mouseExited(MouseEvent e) {
                leaderboardButton.setForeground(new Color(50, 50, 50));  // 恢复默认色
            }

            public void mousePressed(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 10, 0));  // 按下时的颜色
                // 重新开始逻辑
                System.out.println("查看排行榜");
            }
        });


        // 将组件添加到胜利面板
        victoryPanel.add(Box.createVerticalStrut(10));
        victoryPanel.add(victoryLabelPanel);  // 使用调整过位置的胜利提示面板
        victoryPanel.add(infoPanel);  // 添加得分和其他信息
        victoryPanel.add(buttonPanel);  // 添加按钮区域

        // 将胜利面板添加到背景面板
        backgroundPanel.add(victoryPanel, BorderLayout.CENTER);
    }

    private void setupButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);  // 确保按钮背景透明
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("楷体", Font.BOLD, 22));

        // 设置按钮图标并调整尺寸
        ImageIcon originalIcon1 = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage1));

        ImageIcon originalIcon2 = new ImageIcon(getClass().getClassLoader().getResource("btn3.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        ImageIcon originalIcon3 = new ImageIcon(getClass().getClassLoader().getResource("btn2.png"));
        Image scaledImage3 = originalIcon3.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        // 设置按钮文字
        button.setText(button.getText());
        button.setForeground(new Color(60, 30, 0)); // 文字颜色
        button.setHorizontalTextPosition(SwingConstants.CENTER); // 文字居中
        button.setVerticalTextPosition(SwingConstants.CENTER);  // 文字放在图标下方

        button.setPreferredSize(new Dimension(160, 120));  // 增加高度以容纳文字
        button.setMaximumSize(new Dimension(160, 120));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setIcon(new ImageIcon(scaledImage2));
            }

            public void mouseExited(MouseEvent e) {
                button.setIcon(new ImageIcon(scaledImage1));
            }

            public void mousePressed(MouseEvent e) {
                button.setIcon(new ImageIcon(scaledImage3));
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

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public static void main(String[] args) {
        // 测试胜利界面
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VictoryScreen victoryScreen = new VictoryScreen(1000,"2:30", "25步", "2:30", "25步",Language.CHINESE);
                victoryScreen.setVisible(true);
            }
        });
    }
}
