package view.login;

import view.Language;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton submitBtn;
    private JButton backToLoginBtn;
    private JButton languageBtn;
    private Language currentLanguage = Language.CHINESE;
    private JLabel userLabel;
    private JLabel passLabel;
    private JLabel confirmPassLabel;
    private static final String USERNAME_PLACEHOLDER = "请输入用户名";
    private static final String PASSWORD_PLACEHOLDER = "请输入密码";
    private static final String CONFIRM_PASSWORD_PLACEHOLDER = "请确认密码";

    public RegisterFrame() {
        this.setTitle("华容道·注册");
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
        JLabel titleLabel = new JLabel("三国 · 华容道 注册", SwingConstants.CENTER);
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
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标签
        userLabel = new JLabel("丹青名牒：");
        passLabel = new JLabel("玄机密令：");
        confirmPassLabel = new JLabel("复述密令：");

        Font inputFont;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("inputFont.ttf")) {
            inputFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(28f);  // 字体更大
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(inputFont);
        } catch (Exception e) {
            inputFont = new Font("Serif", Font.PLAIN, 28);  // 字体更大
        }

        userLabel.setFont(inputFont);
        passLabel.setFont(inputFont);
        confirmPassLabel.setFont(inputFont);

        // 用户名输入框
        usernameField = new JTextField(15);
        styleTextField(usernameField);
        usernameField.setText(USERNAME_PLACEHOLDER);
        usernameField.setForeground(Color.GRAY);
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals(USERNAME_PLACEHOLDER)) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText(USERNAME_PLACEHOLDER);
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });

        // 密码输入框
        passwordField = new PlaceholderPasswordField(15);
        ((PlaceholderPasswordField) passwordField).setPlaceholder(PASSWORD_PLACEHOLDER);
        styleTextField(passwordField);
        passwordField.setForeground(Color.GRAY);
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals(PASSWORD_PLACEHOLDER)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText(PASSWORD_PLACEHOLDER);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        // 确认密码输入框
        confirmPasswordField = new PlaceholderPasswordField(15);
        ((PlaceholderPasswordField) confirmPasswordField).setPlaceholder(CONFIRM_PASSWORD_PLACEHOLDER);
        styleTextField(confirmPasswordField);
        confirmPasswordField.setForeground(Color.GRAY);
        confirmPasswordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(confirmPasswordField.getPassword()).equals(CONFIRM_PASSWORD_PLACEHOLDER)) {
                    confirmPasswordField.setText("");
                    confirmPasswordField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(confirmPasswordField.getPassword()).isEmpty()) {
                    confirmPasswordField.setText(CONFIRM_PASSWORD_PLACEHOLDER);
                    confirmPasswordField.setForeground(Color.GRAY);
                }
            }
        });

        // 添加到表单
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

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(confirmPassLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centerPanel.add(buttonPanel);

        submitBtn = new JButton("注册");
        backToLoginBtn = new JButton("返回登录");

        // 配置按钮样式
        setupButton(submitBtn);
        setupButton(backToLoginBtn);

        buttonPanel.add(submitBtn);
        buttonPanel.add(backToLoginBtn);

        // 按钮事件
        submitBtn.addActionListener(e -> handleSubmit());
        backToLoginBtn.addActionListener(e -> handleBackToLogin());

        this.setVisible(true);
    }

    private void updateLanguageTexts() {
        if (currentLanguage == Language.CHINESE) {
            submitBtn.setText("登 入");
            languageBtn.setText("中 / En");
            // 标签更新请提取为字段 userLabel, passLabel
            userLabel.setText("丹青名牒：");
            passLabel.setText("玄机密令：");
        } else {
            submitBtn.setText("Login");
            languageBtn.setText("En / 中");
            userLabel.setText("Username:");
            passLabel.setText("Password:");
        }
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Serif", Font.PLAIN, 28));
        textField.setBackground(new Color(255, 248, 220));
        textField.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 100), 3));
    }

    private void handleSubmit() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段必须填写！");
        } else if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "密码和确认密码不一致！");
        } else {
            JOptionPane.showMessageDialog(this, "注册成功！");
        }
    }

    private void handleBackToLogin() {
        this.dispose();  // 关闭当前注册界面
        new LoginFrame();  // 打开登录界面
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

        languageBtn.addActionListener(e -> {
            currentLanguage = (currentLanguage == Language.CHINESE) ? Language.ENGLISH : Language.CHINESE;
            updateLanguageTexts();
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

}

// 支持 Placeholder 的密码框
class PlaceholderPasswordField extends JPasswordField {
    private String placeholder;

    public PlaceholderPasswordField(int columns) {
        super(columns);
    }

    public void setPlaceholder(String text) {
        this.placeholder = text;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (placeholder != null && getPassword().length == 0 && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont());
            g2.setColor(Color.GRAY);
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left + 5, getHeight() / 2 + getFont().getSize() / 2 - 4);
            g2.dispose();
        }
    }
}

