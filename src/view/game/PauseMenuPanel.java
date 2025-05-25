package view.game;

import controller.GameController;
import view.Language;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PauseMenuPanel extends JFrame {
    private JPanel pasuePanel;
    private JLabel pauseLabel;  // "得分" 标签
    private JButton saveButton;
    private JButton loadButton;
    private JButton resumeButton;
    private JButton restartButton;
    private JButton mainMenuButton;
    private JButton soundToggleButton;
    private GameController gameController;
    // 背景图片
    private ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("pause_bg.gif"));
    private MusicPlayer musicPlayer;

    public PauseMenuPanel(String time, String steps,Language currentLanguage) {
        // 设置主窗口
        setSize(600, 410);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setLayout(new BorderLayout());
        this.setUndecorated(true);
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
        pauseLabel = new JLabel((currentLanguage == Language.CHINESE)?"暂停":"Pause", JLabel.CENTER);
        pauseLabel.setFont(new Font("楷体", Font.PLAIN, 40));
        pasueHintPanel.add(pauseLabel);

        // 显示最快通关
        JPanel timeCountPanel = new JPanel();
        timeCountPanel.setLayout(new GridLayout(2, 1)); // 最快通关部分上下排
        timeCountPanel.setOpaque(false);
        JLabel timeCountTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"用时":"Time", JLabel.CENTER);
        timeCountTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel timeCountValueLabel = new JLabel(time, JLabel.CENTER);
        timeCountValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeCountPanel.add(timeCountTextLabel);
        timeCountPanel.add(timeCountValueLabel);

        // 显示最少步数
        JPanel stepCountPanel = new JPanel();
        stepCountPanel.setLayout(new GridLayout(2, 1)); // 最少步数部分上下排
        stepCountPanel.setOpaque(false);
        JLabel stepCountTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"步数":"Steps", JLabel.CENTER);
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

        JPanel upperButtonPanel = new JPanel();
        upperButtonPanel.setLayout(new BoxLayout(upperButtonPanel, BoxLayout.Y_AXIS));
        upperButtonPanel.setOpaque(false); // 保证背景透明

        JPanel upperControlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        upperControlButtonsPanel.setOpaque(false);

        saveButton = new JButton();
//        loadButton = new JButton();

        setupButton(saveButton);
//        setupButton(loadButton);

        if (currentLanguage == Language.CHINESE) {
            saveButton.setText("保存进度");
        } else {
            saveButton.setText("Save");
        }

//        if (currentLanguage == Language.CHINESE) {
//            loadButton.setText("读取进度");
//        } else {
//            loadButton.setText("Load");
//        }

        saveButton.setMargin(new Insets(0, 0, 0, 0));
//        loadButton.setMargin(new Insets(0, 0, 0, 0));

        upperControlButtonsPanel.add(saveButton);
//        upperControlButtonsPanel.add(loadButton);

        saveButton.addActionListener(e -> {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            String filePath = "./" + timestamp + ".txt";
            this.gameController.saveGame(filePath);
        });

//
//        loadButton.addActionListener(e ->{
//            JFileChooser jf = new JFileChooser(".");
//            jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//            jf.setFileFilter(new FileFilter() {
//                @Override
//                public String getDescription() {
//                    return ".txt";
//                }
//
//                @Override
//                public boolean accept(File f) {
//                    if (f.getName().endsWith("txt")) {
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            });
//            int flag = jf.showOpenDialog(this);
//            if (flag == JFileChooser.APPROVE_OPTION) {
//                String fileName = jf.getSelectedFile().getName();
//                String lastName = fileName.substring(fileName.lastIndexOf(".") + 1);
//                if (!lastName.equals("txt")) {
//                    JOptionPane.showMessageDialog(this, "请选择一个txt格式的文件");
//                    return;
//                }
//                if(this.gameController.loadGame(jf.getSelectedFile().getAbsolutePath())) {
//                    this.setVisible(false);
//                    this.gameController.restartTimer();
//                }
//            }
//        });
        upperButtonPanel.add(upperControlButtonsPanel);

        JPanel midButtonPanel = new JPanel();
        midButtonPanel.setLayout(new BoxLayout(midButtonPanel, BoxLayout.Y_AXIS));
        midButtonPanel.setOpaque(false); // 保证背景透明

        JPanel midControlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        midControlButtonsPanel.setOpaque(false);

        restartButton = new JButton();
        resumeButton = new JButton();
        mainMenuButton = new JButton();

        setupButton(restartButton);
        setupButton(resumeButton);
        setupButton(mainMenuButton);

        if (currentLanguage == Language.CHINESE) {
            restartButton.setText("再启");
        } else {
            restartButton.setText("Restart");
        }

        if (currentLanguage == Language.CHINESE) {
            resumeButton.setText("征战");
        } else {
            resumeButton.setText("Continue");
        }

        if (currentLanguage == Language.CHINESE) {
            mainMenuButton.setText("归返");
        } else {
            mainMenuButton.setText("Back");
        }

        restartButton.setMargin(new Insets(0, 0, 0, 0));
        resumeButton.setMargin(new Insets(0, 0, 0, 0));
        mainMenuButton.setMargin(new Insets(0, 0, 0, 0));

        midControlButtonsPanel.add(restartButton);
        midControlButtonsPanel.add(resumeButton);
        midControlButtonsPanel.add(mainMenuButton);

        midButtonPanel.add(midControlButtonsPanel);

        JPanel soundTogglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        soundTogglePanel.setOpaque(false);

        this.musicPlayer = MusicPlayer.getInstance();
        boolean isMuted = musicPlayer.isMuted();
        musicPlayer.play("/bgm.wav");


        // 在按钮区域添加游客模式按钮
        soundToggleButton = new JButton();
        if (currentLanguage == Language.CHINESE) {
            soundToggleButton.setText(isMuted ? "音效关" : "音效开");
        } else {
            soundToggleButton.setText(isMuted ? "Music Off" : "Music On");
        }

        // 添加排行榜按钮
        soundToggleButton.setFont(new Font("楷体", Font.PLAIN, 18));
        soundToggleButton.setForeground(Color.WHITE); // 淡灰色
        soundToggleButton.setBorderPainted(false);
        soundToggleButton.setContentAreaFilled(false);
        soundToggleButton.setFocusPainted(false);
        soundToggleButton.setOpaque(false);
        soundToggleButton.setMargin(new Insets(0, 0, 0, 0));
        soundTogglePanel.add(soundToggleButton);
        // 添加排行榜按钮到buttonPanel

        upperButtonPanel.setBorder(null);
        midButtonPanel.setBorder(null);

        buttonPanel.add(upperButtonPanel);
        buttonPanel.add(midButtonPanel);
        buttonPanel.add(Box.createVerticalStrut(0));
        buttonPanel.add(soundTogglePanel);

        // 设置按钮事件
        resumeButton.addActionListener(e -> {
            this.setVisible(false);
            this.gameController.restartTimer();
        });
        restartButton.addActionListener(e->{
            this.setVisible(false);
            this.gameController.restartGame();
        });

        mainMenuButton.addActionListener(e->{
            this.setVisible(false);
            this.gameController.returnToMenu();
        });

        soundToggleButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                soundToggleButton.setForeground(new Color(255, 100, 0));
            }

            public void mouseExited(MouseEvent e) {
                soundToggleButton.setForeground(Color.WHITE);
            }

            public void mousePressed(MouseEvent e) {
                soundToggleButton.setForeground(new Color(255, 10, 0));

                // 切换音效状态
                musicPlayer.toggleMute();

                // 更新按钮文字（实时状态）
                boolean isNowMuted = musicPlayer.isMuted();
                if (currentLanguage == Language.CHINESE) {
                    soundToggleButton.setText(isNowMuted ? "音效关" : "音效开");
                } else {
                    soundToggleButton.setText(isNowMuted ? "Music Off" : "Music On");
                }

                System.out.println("当前音效状态：" + (isNowMuted ? "关闭" : "开启"));
            }
        });


        // 将组件添加到胜利面板
        pasuePanel.add(Box.createVerticalStrut(20));
        pasuePanel.add(infoPanel);
        pasuePanel.add(buttonPanel,BorderLayout.SOUTH);// 添加得分和其他信息

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

        ImageIcon originalIcon2 = new ImageIcon(getClass().getClassLoader().getResource("btn3.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        ImageIcon originalIcon3 = new ImageIcon(getClass().getClassLoader().getResource("btn2.png"));
        Image scaledImage3 = originalIcon3.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        // 设置按钮文字
        button.setText(button.getText());
        button.setForeground(new Color(60, 30, 0)); // 文字颜色
        button.setHorizontalTextPosition(SwingConstants.CENTER); // 文字居中
        button.setVerticalTextPosition(SwingConstants.CENTER);  // 文字放在图标下方

        button.setPreferredSize(new Dimension(120, 90));  // 增加高度以容纳文字
        button.setMaximumSize(new Dimension(120, 90));

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
                PauseMenuPanel pauseMenuPanel1 = new PauseMenuPanel("2:30", "25步",Language.CHINESE);
                pauseMenuPanel1.setVisible(true);
            }
        });
    }
}


