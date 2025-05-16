package view.game;

import controller.GameController;
import model.MapModel;
import view.Language;
import view.game.SkinManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class GameFrame1 extends JFrame {
    private JButton languageBtn;
    private JButton helpBtn;
    private JButton stopBtn;
    private JButton restartBtn;
    private JButton undoBtn;
    private JButton skinToggleBtn;
    private Language currentLanguage = Language.CHINESE;
    private GameController gameController;
    private PauseMenuPanel pauseMenuPanel;
    private GamePanel gamePanel;
    public GameFrame1(MapModel mapModel,int mode,GameController gameController) {
        this.setTitle("华容道");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1000, 750);
        this.setLocationRelativeTo(null);
        this.gameController = gameController;
        // 背景面板
        JPanel bgPanel = new JPanel() {
            Image bg = new ImageIcon(getClass().getClassLoader().getResource("background.gif")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());  // 使用 BorderLayout 来帮助居中显示
        this.setContentPane(bgPanel);

        // 1. 顶部按钮面板直接添加到 NORTH
        JPanel topPanel = createTopPanel();
        bgPanel.add(topPanel, BorderLayout.NORTH);

        // 2. 创建中间容器（标题 + 轮播）
        this.gamePanel = new GamePanel(mapModel,mode);
        gamePanel.setController(this.gameController);
        this.gameController.setView(gamePanel);
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setOpaque(false);
        bgPanel.add(gamePanel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // 左侧图标按钮
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        iconPanel.setOpaque(false);
        helpBtn = createHoverButton("help.png", "帮助");
        restartBtn = createHoverButton("restart.png", "重启");
        stopBtn = createHoverButton("stop.png", "暂停");
        undoBtn = createHoverButton("undo.png", "撤销");
        iconPanel.add(helpBtn);
        iconPanel.add(restartBtn);
        iconPanel.add(stopBtn);
        iconPanel.add(undoBtn);

        // 右侧语言按钮
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.setOpaque(false);
        languageBtn = new JButton("中/En");
        setupButton(languageBtn);
        langPanel.add(languageBtn);
        stopBtn.addActionListener(e -> {
            int min = this.gamePanel.getCurrentPanel().getElapsedTime()/60;
            int seconds = this.gamePanel.getCurrentPanel().getElapsedTime()%60;
            String time = String.format("%02d:%02d", min, seconds);
            this.pauseMenuPanel = new PauseMenuPanel(time,String.format("%d",this.gamePanel.getCurrentPanel().steps));
            this.pauseMenuPanel.setGameController(this.gameController);
            this.pauseMenuPanel.setVisible(true);
            this.gameController.pauseTimer();
        });
        languageBtn.addActionListener(e -> {
            currentLanguage = (currentLanguage == Language.CHINESE) ? Language.ENGLISH : Language.CHINESE;
            updateLanguageTexts();
        });

        topPanel.add(iconPanel, BorderLayout.WEST);
        topPanel.add(langPanel, BorderLayout.EAST);
        return topPanel;
    }

    private JButton createHoverButton(String imagePath, String text) {
        JButton button = createIconButton(imagePath, text);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(null);
                button.setText("<html><center>" + text + "</center></html>");
                button.setFont(new Font("楷体", Font.BOLD, 12));
                button.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(new ImageIcon(getScaledImage(imagePath, 32, 32)));
                button.setText("");
            }
        });
        return button;
    }

    // 修改图片加载方法，添加错误处理
    private Image getScaledImage(String imagePath, int width, int height) {
        URL url = getClass().getResource("/" + imagePath); // 添加斜杠确保从根目录查找
        if (url == null) {
            System.err.println("图片资源未找到: " + imagePath);
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        return new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private JButton createIconButton(String imagePath, String tooltip) {
        URL iconURL = getClass().getClassLoader().getResource(imagePath);
        if (iconURL == null) {
            System.err.println("图标资源未找到: " + imagePath);
            return new JButton(tooltip);
        }
        ImageIcon originalIcon = new ImageIcon(iconURL);
        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // 控制大小
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(50, 50)); // 设置大小，避免撑开
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private void setupButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("楷体", Font.BOLD, 22));
        button.setForeground(new Color(60, 30, 0));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(160, 120));
        button.setMaximumSize(new Dimension(160, 120));

        ImageIcon originalIcon = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        button.setIcon(scaledIcon);

        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                playClickSound();
            }
        });
    }

    private void playClickSound() {
        try {
            URL soundURL = getClass().getClassLoader().getResource("clickBtn.wav");
            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLanguageTexts() {
        if (currentLanguage == Language.CHINESE) {
            languageBtn.setText("中 / En");
        } else {
            languageBtn.setText("En / 中");
        }

        if (gamePanel != null) {
            gamePanel.updateLanguageTexts(currentLanguage);
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public static void main(String[] args) {
        MapModel mapModel = new MapModel(new int[][]{// 创建游戏地图数据
                {2, 2, 2, 2, 1},
                {1, 3, 2, 2, 0},
                {1, 3, 4, 4, 1},
                {2, 2, 4, 4, 0}
        });
//        new GameFrame1(mapModel,1);
    }
}

