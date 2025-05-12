package view.login;

import view.Language;
import view.game.GameFrame;
import view.login.RegisterFrame;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitBtn;
    private JButton resetBtn;
    private JButton registerBtn;
    private JButton languageBtn;
    private JButton guestModeBtn;
    private GameFrame gameFrame;
    private Language currentLanguage = Language.CHINESE;
    private JLabel userLabel;  // 用户标签
    private JLabel passLabel;
    private static final String USERNAME_PLACEHOLDER = "请输入用户名";
    private static final String PASSWORD_PLACEHOLDER = "请输入密码";

    public LoginFrame() {
        this.setTitle("华容道·登营");
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
        bgPanel.setLayout(new BorderLayout());
        this.setContentPane(bgPanel);

        // 语言按钮 - 右上角图标
        languageBtn = new JButton("中/En");
        setupButton(languageBtn);
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.setOpaque(false);
        langPanel.add(languageBtn);
        bgPanel.add(langPanel, BorderLayout.NORTH);

        // 中心面板
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        // 标题
        JLabel titleLabel = new JLabel("三国 · 华容道", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("titleFont.ttf")) {
            Font titleFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(60f);  // 放大到60
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(titleFont);
            titleLabel.setFont(titleFont);
        } catch (Exception e) {
            titleLabel.setFont(new Font("Serif", Font.BOLD, 80));  // 备用字体也放大
        }
        titleLabel.setForeground(new Color(80, 40, 0));
        centerPanel.add(Box.createVerticalStrut(20));  // 标题向下放一点
        centerPanel.add(titleLabel);

        // 输入表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalStrut(30));  // 适当调整输入框与标题间距
        centerPanel.add(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标签字体更大
        userLabel = new JLabel("丹青名牒：");
        userLabel.setForeground(new Color(60, 30, 0));
        passLabel = new JLabel("玄机密令：");
        passLabel.setForeground(new Color(60, 30, 0));

        Font inputFont;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("inputFont.ttf")) {
            inputFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(28f);  // 字体更大
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(inputFont);
        } catch (Exception e) {
            inputFont = new Font("Serif", Font.PLAIN, 28);  // 字体更大
        }

        userLabel.setFont(inputFont);
        passLabel.setFont(inputFont);

        // 修改后的构造函数中的输入框初始化部分
        usernameField = new JTextField(15);
        usernameField.setFont(inputFont);
        usernameField.setBackground(new Color(255, 248, 220));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 100), 3));  // 边框加粗
        usernameField.setText(USERNAME_PLACEHOLDER);  // 设置默认提示文字
        usernameField.setForeground(Color.GRAY);  // 设置提示文字为灰色


// 为用户名输入框添加 FocusListener
        usernameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals(USERNAME_PLACEHOLDER)) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);  // 设置输入文字为黑色
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText(USERNAME_PLACEHOLDER);
                    usernameField.setForeground(Color.GRAY);  // 恢复提示文字为灰色
                }
            }
        });

        // 修改密码框部分，使用一个文本框来显示提示文字
        passwordField = new JPasswordField(15);
        passwordField.setFont(inputFont);
        passwordField.setBackground(new Color(255, 248, 220));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 100), 3));  // 边框加粗
        passwordField.setEchoChar((char) 0);  // 设置不显示密码内容，显示默认提示文字
        passwordField.setText(PASSWORD_PLACEHOLDER);  // 设置默认提示文字
        passwordField.setForeground(Color.GRAY);  // 设置提示文字为灰色

// 为密码输入框添加 FocusListener
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals(PASSWORD_PLACEHOLDER)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);  // 设置输入文字为黑色
                    passwordField.setEchoChar('*');  // 开始显示密码字符
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText(PASSWORD_PLACEHOLDER);
                    passwordField.setForeground(Color.GRAY);  // 恢复提示文字为灰色
                    passwordField.setEchoChar((char) 0);  // 不显示密码字符，恢复为提示文字
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // 按钮区域
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomButtonPanel.setOpaque(false);
        bgPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        submitBtn = new JButton("登录");
        resetBtn = new JButton("清空");
        registerBtn = new JButton("注册");

        setupButton(submitBtn);
        setupButton(resetBtn);
        setupButton(registerBtn);

        bottomButtonPanel.add(registerBtn);
        bottomButtonPanel.add(submitBtn);
        bottomButtonPanel.add(resetBtn);

        // 在按钮区域添加游客模式按钮
        guestModeBtn = new JButton("以游客身份进入");
        guestModeBtn.setFont(new Font("楷体", Font.PLAIN, 20));
        guestModeBtn.setForeground(new Color(150, 150, 150)); // 淡灰色
        guestModeBtn.setBorderPainted(false);
        guestModeBtn.setContentAreaFilled(false);
        guestModeBtn.setFocusPainted(false);
        guestModeBtn.setOpaque(false);

        // 将游客模式按钮放到登录按钮正下方
        guestModeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(guestModeBtn);
        centerPanel.add(Box.createVerticalStrut(-10));  // 可选，增加一点底部间距

        // 按钮事件
        // 登录按钮事件（登录动画部分）
        submitBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            boolean loginSuccess = false;
            try {
                File file = new File("user.txt");
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "用户文件不存在！");
                    return;
                }

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    String currentUsername = null;
                    String currentPassword = null;
                    while ((line = br.readLine()) != null) {
                        if (currentUsername == null) {
                            currentUsername = line;
                        } else {
                            currentPassword = line;
                            // 验证用户名和密码
                            if (username.equals(currentUsername) && password.equals(currentPassword)) {
                                loginSuccess = true;
                                break;
                            }
                            currentUsername = null; // 重置，继续检查下一个用户
                        }
                    }
                }

                if (loginSuccess) {
                    if (gameFrame != null) {
                        gameFrame.setVisible(true);
                        this.setVisible(false);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "用户名或密码错误！");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "登录失败：" + ex.getMessage());
                ex.printStackTrace();
            }
        });


        resetBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        this.setVisible(true);

        registerBtn.addActionListener(e -> {
            new RegisterFrame(); // 你需要自己实现这个窗口
            this.setVisible(false);
        });

        languageBtn.addActionListener(e -> {
            currentLanguage = (currentLanguage == Language.CHINESE) ? Language.ENGLISH : Language.CHINESE;
            updateLanguageTexts();
        });

        guestModeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                guestModeBtn.setForeground(new Color(255, 200, 0));  // 改为高亮色
            }

            public void mouseExited(MouseEvent e) {
                guestModeBtn.setForeground(new Color(150, 150, 150));  // 恢复默认色
            }

            public void mousePressed(MouseEvent e) {
                guestModeBtn.setForeground(new Color(255, 180, 0));  // 按下时的颜色
                // 触发游客模式登录的逻辑
                // 你可以在这里调用游客模式逻辑，进入游戏界面
                if (gameFrame != null) {
                    gameFrame.setVisible(true);
                    LoginFrame.this.setVisible(false);
                }
            }
        });
        guestModeBtn.addActionListener(e -> {
            // 这里可以添加游客模式逻辑，进入游戏界面
            if (gameFrame != null) {
                gameFrame.setVisible(true);
                gameFrame.setGuestMode(true); // 设置游戏框架为游客模式
                LoginFrame.this.setVisible(false); // 隐藏登录界面
            }
        });

    }

    private void updateLanguageTexts() {
        if (currentLanguage == Language.CHINESE) {
            submitBtn.setText("登 入");
            resetBtn.setText("清 空");
            registerBtn.setText("注 册");
            languageBtn.setText("中 / En");
            // 标签更新请提取为字段 userLabel, passLabel
            userLabel.setText("丹青名牒：");
            passLabel.setText("玄机密令：");
        } else {
            submitBtn.setText("Login");
            resetBtn.setText("Clear");
            registerBtn.setText("Register");
            languageBtn.setText("En / 中");
            userLabel.setText("Username:");
            passLabel.setText("Password:");
        }
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

        // 设置按钮文字
        button.setText(button.getText());
        button.setForeground(new Color(60, 30, 0)); // 文字颜色
        button.setHorizontalTextPosition(SwingConstants.CENTER); // 文字居中
        button.setVerticalTextPosition(SwingConstants.CENTER);  // 文字放在图标下方

        button.setPreferredSize(new Dimension(160, 120));  // 增加高度以容纳文字
        button.setMaximumSize(new Dimension(160, 120));

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

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }
}