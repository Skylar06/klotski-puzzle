package view.level.select;

import controller.GameController;
import model.LevelManager;
import model.MapModel;
import view.Language;
import view.game.GameFrame1;
import view.game.HelpPanel;
import view.game.Save;
import view.login.LoginFrame;

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
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class LevelSelectFrame extends JFrame {
    private JLabel titleLabel;
    private JButton languageBtn;
    private JButton confirmBtn;
    private JButton loadBtn;
    private JButton helpBtn;
    private JButton backBtn;
    private JPanel carouselPanel;
    private Language currentLanguage = Language.CHINESE;
    private JPanel imageContainer;
    private int currentCarouselIndex = 1;
    private List<String> carouselImages = Arrays.asList("battle.png", "classic.png", "extreme.png");
    private GameController gameController;
    private MapModel model;
    private JLayeredPane layeredPane;
    private TransparentPanel darkOverlayPanel;
    private JLabel flameLabel;
    private LoginFrame loginFrame;

    public LevelSelectFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.setTitle("华容道·选择关卡");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1000, 750);
        this.setLocationRelativeTo(null);

        //背景
        JPanel bgPanel = new JPanel() {
            Image bg = new ImageIcon(getClass().getClassLoader().getResource("background.gif")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        bgPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        addTitleLabel(centerPanel);
        carouselPanel = createCarouselPanel();
        centerPanel.add(carouselPanel);
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        bgPanel.add(bottomPanel, BorderLayout.SOUTH);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        this.setContentPane(layeredPane);

        //火盆
        layeredPane.add(bgPanel, Integer.valueOf(0));
        bgPanel.setAlignmentX(0.5f);
        bgPanel.setAlignmentY(0.5f);

        darkOverlayPanel = new TransparentPanel(0.4f);
        layeredPane.add(darkOverlayPanel, Integer.valueOf(1));
        darkOverlayPanel.setAlignmentX(0.5f);
        darkOverlayPanel.setAlignmentY(0.5f);

        flameLabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("pen.png")));

        JPanel flameWrapper = new JPanel();
        flameWrapper.setOpaque(false);
        flameWrapper.setLayout(new BorderLayout());

        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setOpaque(false);
        bottomRightPanel.setLayout(new BoxLayout(bottomRightPanel, BoxLayout.LINE_AXIS));
        bottomRightPanel.add(Box.createHorizontalGlue());
        bottomRightPanel.add(flameLabel);
        bottomRightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 30)); // 右下边距

        JPanel verticalBox = new JPanel();
        verticalBox.setOpaque(false);
        verticalBox.setLayout(new BoxLayout(verticalBox, BoxLayout.PAGE_AXIS));
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.add(bottomRightPanel);

        flameWrapper.add(verticalBox, BorderLayout.CENTER);

        layeredPane.add(flameWrapper, Integer.valueOf(2));
        flameWrapper.setAlignmentX(0.5f);
        flameWrapper.setAlignmentY(0.5f);

        flameLabel.addMouseListener(new MouseAdapter() {
            boolean lit = false;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (lit) return;
                lit = true;
                ImageIcon flameIcon = new ImageIcon(getClass().getClassLoader().getResource("flame.png"));
                flameLabel.setIcon(flameIcon);
                playIgniteSound();

                Timer fadeTimer = new Timer(30, null);
                fadeTimer.addActionListener(new ActionListener() {
                    float alpha = 0.4f;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        alpha -= 0.02f;
                        if (alpha <= 0f) {
                            fadeTimer.stop();
                            layeredPane.remove(darkOverlayPanel);
                            layeredPane.repaint();
                        } else {
                            darkOverlayPanel.setAlpha(alpha);
                        }
                    }
                });
                fadeTimer.start();
            }
        });

        this.setVisible(true);
    }

    private void playIgniteSound() {
        new Thread(() -> {
            try {
                URL soundURL = getClass().getClassLoader().getResource("flame.wav");
                if (soundURL == null) {
                    System.out.println("点火音效资源没找到！");
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    //半透明
    static class TransparentPanel extends JPanel {
        private float alpha;

        public TransparentPanel(float alpha) {
            this.alpha = alpha;
            setOpaque(false);
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private void addTitleLabel(JPanel container) {
        titleLabel = new JLabel("关卡选择", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("titleFont.ttf")) {
            Font titleFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(60f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(titleFont);
            titleLabel.setFont(titleFont);
        } catch (Exception e) {
            titleLabel.setFont(new Font("Serif", Font.BOLD, 80));
        }

        titleLabel.setForeground(new Color(80, 40, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(Box.createVerticalStrut(0));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(0));

        container.add(titlePanel);
    }


    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        //图标按钮
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        iconPanel.setOpaque(false);
        helpBtn = createHoverButton("help.png", (currentLanguage == Language.CHINESE) ?"帮助":"Help");
        iconPanel.add(helpBtn);

        helpBtn.addActionListener(e -> {
            JFrame frame = new JFrame("三国华容道 - 帮助文档");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(640, 480);
            frame.setContentPane(new HelpPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        //语言按钮
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.setOpaque(false);
        languageBtn = new JButton("中原/外邦");
        setupButton(languageBtn);
        langPanel.add(languageBtn);

        languageBtn.addActionListener(e -> {
            currentLanguage = (currentLanguage == Language.CHINESE) ? Language.ENGLISH : Language.CHINESE;
            updateLanguageTexts();
        });

        topPanel.add(iconPanel, BorderLayout.WEST);
        topPanel.add(langPanel, BorderLayout.EAST);
        return topPanel;
    }

    private JPanel createCarouselPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50)); // 保留内边距

        //箭头按钮
        JButton leftArrow = createArrowButton("leftarrow.png");
        JButton rightArrow = createArrowButton("rightarrow.png");
        leftArrow.addActionListener(e -> updateCarousel(-1));
        rightArrow.addActionListener(e -> updateCarousel(1));

        JPanel contentRow = new JPanel();
        contentRow.setLayout(new BoxLayout(contentRow, BoxLayout.X_AXIS));
        contentRow.setOpaque(false);

        imageContainer = new JPanel();
        imageContainer.setLayout(new BoxLayout(imageContainer, BoxLayout.X_AXIS));
        imageContainer.setOpaque(false);

        contentRow.add(Box.createHorizontalGlue());
        contentRow.add(leftArrow);
        contentRow.add(Box.createHorizontalStrut(20));
        contentRow.add(imageContainer);
        contentRow.add(Box.createHorizontalStrut(20));
        contentRow.add(rightArrow);
        contentRow.add(Box.createHorizontalGlue());

        panel.add(contentRow, BorderLayout.CENTER);

        updateCarousel(0);
        return panel;
    }


    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);
        panel.add(Box.createVerticalGlue());

        confirmBtn = new JButton("确定");
        loadBtn = new JButton("读取进度");
        backBtn = new JButton("归返登入");

        if (currentLanguage == Language.CHINESE) {
            loadBtn.setText("读取进度");
        } else {
            loadBtn.setText("Load");
        }

        setupButton(confirmBtn);
        setupButton(loadBtn);
        setupButton(backBtn);

        panel.add(loadBtn);
        panel.add(confirmBtn);
        panel.add(backBtn);

        confirmBtn.addActionListener(
            e -> {
                this.setVisible(false);
                this.gameController.setMode(this.currentCarouselIndex);
                this.model.setMatrix(LevelManager.getCurrentMap());
                this.gameController.setModel(this.model);
                this.gameController.gameFrame1 = new GameFrame1(this.model,this.currentCarouselIndex,this.gameController);
                this.gameController.gameFrame1.setVisible(true);
            }
        );
        loadBtn.addActionListener(
                event -> {
                    if (this.gameController.isVisitor()){
                        showLoginError("不录之身禁止导入");
                        return;
                    }
                    JFileChooser jf = new JFileChooser(".");
                    jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    jf.setFileFilter(new FileFilter() {
                        @Override
                        public String getDescription() {
                            return ".txt";
                        }

                        @Override
                        public boolean accept(File f) {
                            if (f.getName().endsWith("txt")) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                    int flag = jf.showOpenDialog(this);
                    if (flag == JFileChooser.APPROVE_OPTION) {
                        String fileName = jf.getSelectedFile().getName();
                        String lastName = fileName.substring(fileName.lastIndexOf(".") + 1);
                        if (!lastName.equals("txt")) {
                            showLoginError( "请选择一个txt格式的文件");
                            return;
                        }
                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(jf.getSelectedFile().getPath()))) {
                            Save temp = (Save) in.readObject();
                            if (!temp.user.equals(this.gameController.getUser())) {
                                showLoginError("您只能读取属于您的存档");
                                return;
                            }
                            //加载游戏状态
                            int[][] savedMatrix = temp.model.getMatrix();
                            model.setMatrix(savedMatrix);
                            this.gameController.setModel(temp.model);
                            this.gameController.setMode(temp.mode);
                            this.gameController.gameFrame1 = new GameFrame1(temp.model,temp.mode,this.gameController);
                            this.gameController.gameFrame1.setVisible(true);
                            this.setVisible(false);
                            this.gameController.getMoveHistory().clear();
                            this.gameController.gameFrame1.getGamePanel().getCurrentPanel().setElapsedTime(temp.time);
                            this.gameController.gameFrame1.getGamePanel().getCurrentPanel().setSteps(temp.step);
                        } catch (FileNotFoundException e) {
                            showLoginError("初来乍到，未染红尘");
                        } catch (StreamCorruptedException e) {
                            showLoginError("文件损坏或格式不正确: " + e.getMessage());
                        } catch (IOException e) {
                            showLoginError("读取文件时发生错误: " + e.getMessage());
                        } catch (ClassNotFoundException e) {
                            showLoginError("保存的文件中包含未知的类: " + e.getMessage());
                        }
                    }
                }
        );

        backBtn.addActionListener(
                e -> {
                    this.dispose();
                    this.setVisible(false);
                    if (loginFrame != null) {
                        loginFrame.setVisible(true);
                    }
                }
        );
        return panel;
    }

    private void showLoginError(String message) {
        playSound("error.wav");
        shakeWindow(this);
        new view.login.HintScreen(message,currentLanguage).setVisible(true);
    }
    private void playSound(String resourcePath) {
        try {
            URL soundURL = getClass().getClassLoader().getResource(resourcePath);
            if (soundURL == null) {
                System.err.println("找不到资源文件: " + resourcePath);
                return;
            }
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shakeWindow(JFrame frame) {
        int x = frame.getX();
        int y = frame.getY();
        for (int i = 0; i < 10; i++) {
            frame.setLocation(x + (i % 2 == 0 ? 5 : -5), y);
            try { Thread.sleep(30); } catch (InterruptedException e) { }
        }
        frame.setLocation(x, y);
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
                button.setIcon(new ImageIcon(getScaledImage(imagePath, 50, 50)));
                button.setText("");
            }
        });
        return button;
    }

    private JButton createArrowButton(String imagePath) {
        ImageIcon icon = new ImageIcon(getScaledImage(imagePath, 40, 40));
        ImageIcon enlargedIcon = new ImageIcon(getScaledImage(imagePath, 50, 50)); // 悬停时更大

        JButton button = new JButton(icon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setIcon(enlargedIcon);
            }

            public void mouseExited(MouseEvent e) {
                button.setIcon(icon);
            }
        });

        return button;
    }

    private void updateCarousel(int direction) {
        currentCarouselIndex = (currentCarouselIndex + direction + carouselImages.size()) % carouselImages.size();
        imageContainer.removeAll();

        int prev = (currentCarouselIndex - 1 + carouselImages.size()) % carouselImages.size();
        int current = currentCarouselIndex % carouselImages.size();
        int next = (currentCarouselIndex + 1) % carouselImages.size();

        imageContainer.add(createCarouselImageWithFade(carouselImages.get(prev), false));
        imageContainer.add(Box.createHorizontalStrut(20));
        imageContainer.add(createCarouselImageWithFade(carouselImages.get(current), true));
        imageContainer.add(Box.createHorizontalStrut(20));
        imageContainer.add(createCarouselImageWithFade(carouselImages.get(next), false));

        imageContainer.revalidate();
        imageContainer.repaint();
    }

    private JLabel createCarouselImageWithFade(String imagePath, boolean isCenter) {
        int size = isCenter ? 300 : 180;

        ImageIcon icon = new ImageIcon(getScaledImage(imagePath, size, size));
        JLabel label = new JLabel(icon);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(size, size));

        Timer fadeInTimer = new Timer(30, new ActionListener() {
            int alpha = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 5;
                if (alpha >= 255) {
                    ((Timer)e.getSource()).stop();
                }
                label.setOpaque(true);
                label.setBackground(new Color(0, 0, 0, alpha));
                label.repaint();
            }
        });
        fadeInTimer.start();

        return label;
    }


    private JLabel createCarouselImage(String imagePath, boolean isCenter) {
        int size = isCenter ? 300 : 180;

        ImageIcon icon = new ImageIcon(getScaledImage(imagePath, size, size));
        JLabel label = new JLabel(icon);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(size, size));
        return label;
    }

    private Image getScaledImage(String imagePath, int width, int height) {
        URL url = getClass().getResource("/" + imagePath);
        if (url == null) {
            System.err.println("图片资源未找到: " + imagePath);
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        return new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

//    private void styleTextButton(JButton button) {
//        button.setFont(new Font("宋体", Font.PLAIN, 14));
//        button.setForeground(Color.WHITE);
//        button.setContentAreaFilled(false);
//        button.setBorderPainted(false);
//        button.setFocusPainted(false);
//        button.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) {
//                button.setForeground(Color.YELLOW);
//                button.setText("<html><u>" + button.getText() + "</u></html>");
//            }
//
//            public void mouseExited(MouseEvent e) {
//                button.setForeground(Color.WHITE);
//                button.setText(button.getText().replaceAll("<.*?>", ""));
//            }
//        });
//    }


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

        ImageIcon originalIcon1 = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage1));

        ImageIcon originalIcon2 = new ImageIcon(getClass().getClassLoader().getResource("btn3.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        ImageIcon originalIcon3 = new ImageIcon(getClass().getClassLoader().getResource("btn2.png"));
        Image scaledImage3 = originalIcon3.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

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
            languageBtn.setText("中原/外邦");
            confirmBtn.setText("确定");
            helpBtn.setText("帮助");

            if (titleLabel != null) {
                titleLabel.setText("关卡选择");
            }

            if (currentLanguage == Language.CHINESE) {
                loadBtn.setText("读取进度");
            } else {
                loadBtn.setText("Load");
            }

            helpBtn = createHoverButton("help.png", "帮助");
        } else {
            languageBtn.setText("En / 中");
            confirmBtn.setText("Confirm");
            helpBtn.setText("Help");

            if (titleLabel != null) {
                titleLabel.setText("Level Select");
            }

            helpBtn = createHoverButton("help.png", "Help");
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        if (this.model == null) {
            this.model = new MapModel(new int[][]{
                    {2, 2, 4, 4, 1},
                    {2, 2, 4, 4, 1},
                    {0, 0, 2, 2, 3},
                    {2, 2, 1, 1, 3}
            });
            this.gameController.setModel(this.model);
        } else {
            this.model.setMatrix(LevelManager.getCurrentMap());
        }
    }

    public void setModel(MapModel model) {
        this.model = model;
    }

}
