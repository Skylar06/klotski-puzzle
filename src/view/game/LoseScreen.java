package view.game;

import controller.GameController;
import view.Language;
import view.LeaderboardFrame;

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

public class LoseScreen extends JFrame {
    private JPanel losePanel;
    private JLabel loseLabel;  // "得分" 标签
    private JButton restartButton;
    private JButton mainMenuButton;
    private JButton leaderboardButton;
    private GameController gameController;
    // 背景图片
    private ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("lose_bg.gif"));

    public LoseScreen(String time, String steps,Language currentLanguage) {
        // 设置主窗口
        setTitle("游戏失败");
        setSize(500, 330);
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

        // 创建一个面板来横向排列得分和其他信息
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());  // 中间得分，两边为最快通关和最少步数
        scorePanel.setOpaque(false);

        // 创建胜利面板
        losePanel = new JPanel();
        losePanel.setLayout(new BoxLayout(losePanel, BoxLayout.Y_AXIS));  // 使用 BoxLayout 按照垂直顺序排列组件
        losePanel.setOpaque(false); // 胜利面板保持透明，覆盖在背景上

        // 显示得分
        JPanel loseHintPanel = new JPanel();
        loseHintPanel.setLayout(new GridLayout(1, 1)); // 得分部分上下排
        loseHintPanel.setOpaque(false);
        loseLabel = new JLabel((currentLanguage == Language.CHINESE)?"失败！":"Lose!", JLabel.CENTER);
        loseLabel.setFont(new Font("楷体", Font.BOLD, 40));
        loseLabel.setForeground(Color.WHITE);
        loseHintPanel.add(loseLabel);

        // 显示最快通关
        JPanel timeCountPanel = new JPanel();
        timeCountPanel.setLayout(new GridLayout(2, 1)); // 最快通关部分上下排
        timeCountPanel.setOpaque(false);
        JLabel timeCountTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"用时":"Time", JLabel.CENTER);
        timeCountTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeCountTextLabel.setForeground(Color.WHITE);
        JLabel timeCountValueLabel = new JLabel(time, JLabel.CENTER);
        timeCountValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeCountValueLabel.setForeground(Color.WHITE);
        timeCountPanel.add(timeCountTextLabel);
        timeCountPanel.add(timeCountValueLabel);

        // 显示最少步数
        JPanel stepCountPanel = new JPanel();
        stepCountPanel.setLayout(new GridLayout(2, 1)); // 最少步数部分上下排
        stepCountPanel.setOpaque(false);
        JLabel stepCountTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"步数":"Steps", JLabel.CENTER);
        stepCountTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        stepCountTextLabel.setForeground(Color.WHITE);
        JLabel stepCountValueLabel = new JLabel(steps, JLabel.CENTER);
        stepCountValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        stepCountValueLabel.setForeground(Color.WHITE);
        stepCountPanel.add(stepCountTextLabel);
        stepCountPanel.add(stepCountValueLabel);

        // 将三个部分添加到scorePanel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 45));  // 横向排布，适当间距
        infoPanel.setOpaque(false);
        infoPanel.add(timeCountPanel);
        infoPanel.add(loseHintPanel);
        infoPanel.add(stepCountPanel);

        // 按钮区域
        // 创建一个BoxPanel，用BoxLayout布局，垂直排列组件
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // 保证背景透明

        JPanel controlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlButtonsPanel.setOpaque(false);

        restartButton = new JButton();
        mainMenuButton = new JButton();

        setupButton(restartButton);
        setupButton(mainMenuButton);

        if (currentLanguage == Language.CHINESE) {
            restartButton.setText("再启");
        } else {
            restartButton.setText("Restart");
        }

        if (currentLanguage == Language.CHINESE) {
            mainMenuButton.setText("归返");
        } else {
            mainMenuButton.setText("Back");
        }

        controlButtonsPanel.add(restartButton);
        controlButtonsPanel.add(mainMenuButton);

        buttonPanel.add(controlButtonsPanel);

        JPanel leaderboardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leaderboardPanel.setOpaque(false);

        // 在按钮区域添加游客模式按钮
        leaderboardButton = new JButton();
        if (currentLanguage == Language.CHINESE) {
            leaderboardButton.setText("封神榜");
        } else {
            leaderboardButton.setText("Leader Board");
        }

        // 添加排行榜按钮
        leaderboardButton.setFont(new Font("楷体", Font.PLAIN, 18));
        leaderboardButton.setForeground(Color.WHITE); // 淡灰色
        leaderboardButton.setBorderPainted(false);
        leaderboardButton.setContentAreaFilled(false);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setOpaque(false);
        leaderboardButton.setMargin(new Insets(0, 0, 0, 0));

        // 添加排行榜按钮到buttonPanel
        buttonPanel.add(Box.createVerticalStrut(0));  // 控制按钮与排行榜之间的间距
        leaderboardPanel.add(leaderboardButton);
        buttonPanel.add(leaderboardPanel);


        restartButton.addActionListener(e -> {
            this.gameController.restartGame();
            this.setVisible(false);
            remove(this);
        });

        mainMenuButton.addActionListener(e ->{
            this.gameController.levelSelectFrame.setVisible(true);
            this.setVisible(false);
            this.dispose();
            if (gameController.gameFrame1 != null) {
                gameController.gameFrame1.dispose(); // 关闭游戏界面
            }
        });

        leaderboardButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 100, 0));  // 改为高亮色
            }

            public void mouseExited(MouseEvent e) {
                leaderboardButton.setForeground(Color.WHITE);  // 恢复默认色
            }

            public void mousePressed(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 10, 0));  // 按下时的颜色
                // 重新开始逻辑
                LeaderboardFrame frame = new LeaderboardFrame();
                frame.setVisible(true);
            }
        });


        // 将组件添加到胜利面板
        losePanel.add(Box.createVerticalStrut(30));
        losePanel.add(infoPanel,BorderLayout.NORTH);
        losePanel.add(infoPanel,BorderLayout.CENTER);  // 添加得分和其他信息
        losePanel.add(buttonPanel,BorderLayout.SOUTH);  // 添加按钮区域

        // 将胜利面板添加到背景面板
        backgroundPanel.add(losePanel, BorderLayout.CENTER);
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
                view.game.LoseScreen loseScreen = new view.game.LoseScreen("2:30", "25步",Language.CHINESE);
                loseScreen.setVisible(true);
            }
        });
    }
}


