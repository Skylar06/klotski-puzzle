package view.game;

import controller.GameController;
import model.Direction;
import model.MapModel;
import view.Language;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class GameFrame1 extends JFrame {
    private JButton languageButton;
    private JButton helpButton;
    private JButton stopButton;
    private JButton restartButton;
    private JButton undoButton;
    private JButton aiButton;
    private Language currentLanguage = Language.CHINESE;
    private GameController gameController;
    private PauseMenuPanel pauseMenuPanel;
    private GamePanel gamePanel;
    private JPanel arrowPanel;

    public GameFrame1(MapModel mapModel,int mode,GameController gameController) {
        this.setTitle("华容道");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1000, 750);
        this.setLocationRelativeTo(null);
        this.gameController = gameController;

        // 背景
        JPanel bgPanel = new JPanel() {
            Image bg = new ImageIcon(getClass().getClassLoader().getResource("background.gif")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());
        this.setContentPane(bgPanel);

        //鼠标
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImage = toolkit.getImage(getClass().getResource("/cursor.png"));

        Point hotspot = new Point(0, 0);
        Cursor customCursor = toolkit.createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        this.setCursor(customCursor);

        //鼠标拖尾
        MouseTrailLayer trailLayer = new MouseTrailLayer();
        trailLayer.setBounds(0, 0, getWidth(), getHeight());
        trailLayer.setFocusable(false);
        this.getLayeredPane().add(trailLayer, JLayeredPane.PALETTE_LAYER);

        //顶部
        JPanel topPanel = createTopPanel();
        bgPanel.add(topPanel, BorderLayout.NORTH);

        //游戏界面
        this.gamePanel = new GamePanel(mapModel,mode);
        gamePanel.setController(this.gameController);
        this.gameController.setView(gamePanel);
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setOpaque(false);

        //全屏提示
        JLabel hintLabel = new JLabel("提示：全屏后可开启箭头操作", SwingConstants.CENTER);
        hintLabel.setFont(new Font("楷体", Font.BOLD, 20));
        hintLabel.setForeground(new Color(139, 69, 19));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setVisible(false);

        JPanel hintPanel = new JPanel();
        hintPanel.setOpaque(false);
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.add(Box.createVerticalStrut(10));
        hintPanel.add(hintLabel);
        gamePanel.add(hintPanel);

        if (this.getWidth() < 1100 || this.getHeight() < 800) {
            hintLabel.setVisible(true);
            new Timer(3000, e -> hintLabel.setVisible(false)).start();
        }
        bgPanel.add(gamePanel, BorderLayout.CENTER);

        //箭头
        arrowPanel = createArrowPanel();
        arrowPanel.setVisible(false);

        JPanel arrowWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        arrowWrapper.setOpaque(false);
        arrowWrapper.add(arrowPanel);
        bgPanel.add(arrowWrapper, BorderLayout.SOUTH);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                if (size.width >= 1050 && size.height >= 800) {
                    arrowPanel.setVisible(true);
                } else {
                    arrowPanel.setVisible(false);
                }
            }
        });

        this.setVisible(true);
    }

    private JPanel createArrowPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JButton up = createArrowButton("up.png", "UP");
        JButton down = createArrowButton("down.png", "DOWN");
        JButton left = createArrowButton("left.png", "LEFT");
        JButton right = createArrowButton("right.png", "RIGHT");
        up.addActionListener(e->{
            BoxComponent box = this.gamePanel.getCurrentPanel().selectedBox;
            if (box == null) return;
            Direction up1 = Direction.UP;
            if (this.gameController.doMove(box.getRow(), box.getCol(), up1)) {
                this.gamePanel.getCurrentPanel().afterMove();
            }
        });
        down.addActionListener(e->{
            BoxComponent box = this.gamePanel.getCurrentPanel().selectedBox;
            if (box == null) return;
            Direction down1 = Direction.DOWN;
            if (this.gameController.doMove(box.getRow(), box.getCol(), down1)) {
                this.gamePanel.getCurrentPanel().afterMove();
            }
        });
        left.addActionListener(e->{
            BoxComponent box = this.gamePanel.getCurrentPanel().selectedBox;
            if (box == null) return;
            Direction left1 = Direction.LEFT;
            if (this.gameController.doMove(box.getRow(), box.getCol(), left1)) {
                this.gamePanel.getCurrentPanel().afterMove();
            }
        });
        right.addActionListener(e->{
            BoxComponent box = this.gamePanel.getCurrentPanel().selectedBox;
            if (box == null) return;
            Direction right1 = Direction.RIGHT;
            if (this.gameController.doMove(box.getRow(), box.getCol(), right1)) {
                this.gamePanel.getCurrentPanel().afterMove();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(up, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(left, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(new JLabel(), gbc);
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(right, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(down, gbc);

        return panel;
    }


    private JButton createArrowButton(String iconName, String direction) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/" + iconName));
        Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JButton btn = new JButton(new ImageIcon(scaled));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

//        btn.addActionListener(e -> {
//            // 将方向字符串转换为具体逻辑
//            if (gameController != null) {
//                switch (direction) {
//                    case "UP": gameController.moveUp(); break;
//                    case "DOWN": gameController.moveDown(); break;
//                    case "LEFT": gameController.moveLeft(); break;
//                    case "RIGHT": gameController.moveRight(); break;
//                }
//            }
//        });
        return btn;
    }


    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        iconPanel.setOpaque(false);
        helpButton = createHoverButton("help.png", "帮助");
        restartButton = createHoverButton("restart.png", "重启");
        stopButton = createHoverButton("stop.png", "暂停");
        undoButton = createHoverButton("undo.png", "撤销");
        aiButton = createHoverButton("ai.png", "AI");
        iconPanel.add(helpButton);
        iconPanel.add(restartButton);
        iconPanel.add(stopButton);
        iconPanel.add(undoButton);
        iconPanel.add(aiButton);

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.setOpaque(false);
        languageButton = new JButton("中/En");
        setupButton(languageButton);
        langPanel.add(languageButton);
        helpButton.addActionListener(e -> {
            JFrame frame = new JFrame("三国华容道 - 帮助文档");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(640, 480);
            frame.setContentPane(new HelpPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        stopButton.addActionListener(e -> {
            int min = this.gamePanel.getCurrentPanel().getElapsedTime()/60;
            int seconds = this.gamePanel.getCurrentPanel().getElapsedTime()%60;
            String time = String.format("%02d:%02d", min, seconds);
            this.pauseMenuPanel = new PauseMenuPanel(time,String.format("%d",this.gamePanel.getCurrentPanel().steps),currentLanguage);
            this.setVisible(false);
            this.pauseMenuPanel.setGameController(this.gameController);
            this.pauseMenuPanel.setVisible(true);
            this.gameController.pauseTimer();
        });
        restartButton.addActionListener(e -> {
            this.gameController.restartGame();
        });
        languageButton.addActionListener(e -> {
            currentLanguage = (currentLanguage == Language.CHINESE) ? Language.ENGLISH : Language.CHINESE;
            updateLanguageTexts();
        });
        undoButton.addActionListener(e -> {
           this.gameController.undoLastMove();
        });
        aiButton.addActionListener(e -> {
            //TODO
        });
        topPanel.add(iconPanel, BorderLayout.WEST);
        topPanel.add(langPanel, BorderLayout.EAST);
        return topPanel;
    }

    private JButton createHoverButton(String imagePath, String text) {
        JButton button = setUpIconButton(imagePath, text);
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
                button.setIcon(new ImageIcon(getImage(imagePath, 32, 32)));
                button.setText("");
            }
        });
        return button;
    }

    private Image getImage(String imagePath, int width, int height) {
        URL url = getClass().getResource("/" + imagePath);
        if (url == null) {
            System.err.println("图片资源未找到: " + imagePath);
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        return new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private JButton setUpIconButton(String imagePath, String tooltip) {
        URL iconURL = getClass().getClassLoader().getResource(imagePath);
        if (iconURL == null) {
            System.err.println("图标资源未找到: " + imagePath);
            return new JButton(tooltip);
        }
        ImageIcon originalIcon = new ImageIcon(iconURL);
        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(50, 50));
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

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
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
            languageButton.setText("中原/外邦");
        } else {
            languageButton.setText("En / 中");
        }

        if (gamePanel != null) {
            gamePanel.updateLanguageTexts(currentLanguage);
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public static void main(String[] args) {
        MapModel mapModel = new MapModel(new int[][]{
                {2, 2, 2, 2, 1},
                {1, 3, 2, 2, 0},
                {1, 3, 4, 4, 1},
                {2, 2, 4, 4, 0}
        });
//        new GameFrame1(mapModel,1);
    }
}

