package view.level.select;

import controller.GameController;
import model.LevelManager;
import model.MapModel;
import view.Language;
import view.game.GameFrame1;
import view.game.HelpPanel;
import view.game.Save;

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
    private JPanel carouselPanel;
    private Language currentLanguage = Language.CHINESE;
    private JPanel imageContainer; // 在类成员变量声明处添加
    private int currentCarouselIndex = 1; // 默认显示中间关卡
    private List<String> carouselImages = Arrays.asList("battle.png", "classic.png", "extreme.png");
    private GameController gameController;
    private MapModel model;
    public LevelSelectFrame() {
        this.setTitle("华容道·选择关卡");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1000, 750);
        this.setLocationRelativeTo(null);
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

        carouselPanel = createCarouselPanel();

        // 2. 创建中间容器（标题 + 轮播）
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 添加标题到中间容器顶部
        addTitleLabel(centerPanel);

        // 添加轮播面板到中间容器
        centerPanel.add(carouselPanel);
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. 底部按钮放在 SOUTH
        JPanel bottomPanel = createBottomPanel();
        bgPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }


    private void addTitleLabel(JPanel container) {
        // 创建标题标签
        titleLabel = new JLabel("关卡选择", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("titleFont.ttf")) {
            // 加载并应用自定义字体
            Font titleFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(60f);  // 放大到60
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(titleFont);
            titleLabel.setFont(titleFont);
        } catch (Exception e) {
            // 如果加载自定义字体失败，使用备用字体
            titleLabel.setFont(new Font("Serif", Font.BOLD, 80));  // 备用字体也放大
        }

        // 设置字体颜色
        titleLabel.setForeground(new Color(80, 40, 0));

        // 创建一个面板用于放置标题
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(Box.createVerticalStrut(0)); // 调整标题上下间距
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(0));

        container.add(titlePanel); // 将标题放在顶部
    }


    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // 左侧图标按钮
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

        // 右侧语言按钮
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

        // 创建箭头按钮
        JButton leftArrow = createArrowButton("leftarrow.png");
        JButton rightArrow = createArrowButton("rightarrow.png");
        leftArrow.addActionListener(e -> updateCarousel(-1));
        rightArrow.addActionListener(e -> updateCarousel(1));

        // 创建主内容容器，横向排列：← imageContainer →
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

        setupButton(confirmBtn);
        setupButton(loadBtn);

        panel.add(confirmBtn);
        panel.add(loadBtn);

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
                        JOptionPane.showMessageDialog(this, "不录之身禁止导入");
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
                            JOptionPane.showMessageDialog(this, "请选择一个txt格式的文件");
                            return;
                        }
                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(jf.getSelectedFile().getPath()))) {
                            Save temp = (Save) in.readObject();
                            if (!temp.user.equals(this.gameController.getUser())) {
                                JOptionPane.showMessageDialog(this, "您只能读取属于您的存档");
                                return;
                            }
                            // 加载游戏状态
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
                            JOptionPane.showMessageDialog(this, "初来乍到，未染红尘");
                        } catch (StreamCorruptedException e) {
                            JOptionPane.showMessageDialog(this, "文件损坏或格式不正确: " + e.getMessage());
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(this, "读取文件时发生错误: " + e.getMessage());
                        } catch (ClassNotFoundException e) {
                            JOptionPane.showMessageDialog(this, "保存的文件中包含未知的类: " + e.getMessage());
                        }
                    }
                }
        );



        return panel;
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


    // 修改updateCarousel方法，添加日志输出
    private void updateCarousel(int direction) {
        currentCarouselIndex = (currentCarouselIndex + direction + carouselImages.size()) % carouselImages.size();
        imageContainer.removeAll();

        int prev = (currentCarouselIndex - 1 + carouselImages.size()) % carouselImages.size();
        int current = currentCarouselIndex % carouselImages.size();
        int next = (currentCarouselIndex + 1) % carouselImages.size();

        // 添加淡入淡出效果
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

        // 使用 Timer 来实现淡入效果
        Timer fadeInTimer = new Timer(30, new ActionListener() {
            int alpha = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 5;
                if (alpha >= 255) {
                    ((Timer)e.getSource()).stop();
                }
                label.setOpaque(true);
                label.setBackground(new Color(0, 0, 0, alpha));  // 修改透明度
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

    // 修改图片加载方法，添加错误处理
    private Image getScaledImage(String imagePath, int width, int height) {
        URL url = getClass().getResource("/" + imagePath); // 添加斜杠确保从根目录查找
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

    public static void main(String[] args) {

        new LevelSelectFrame();
    }
}
