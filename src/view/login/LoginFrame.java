package view.login;

import view.game.GameFrame;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitBtn;
    private JButton resetBtn;
    private GameFrame gameFrame;

    public LoginFrame() {
        this.setTitle("华容道·登营");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(800, 600);
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
        centerPanel.add(Box.createVerticalStrut(50));  // 标题向下放一点
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
        JLabel userLabel = new JLabel("丹青名牒：");
        userLabel.setForeground(new Color(60, 30, 0));
        JLabel passLabel = new JLabel("玄机密令：");
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

        // 输入框大小调整和加粗边框
        usernameField = new JTextField(15);  // 缩短输入框长度
        usernameField.setFont(inputFont);
        usernameField.setBackground(new Color(255, 248, 220));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 100), 3));  // 边框加粗

        passwordField = new JPasswordField(15);  // 缩短输入框长度
        passwordField.setFont(inputFont);
        passwordField.setBackground(new Color(255, 248, 220));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 100), 3));  // 边框加粗

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
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalStrut(0));  // 按钮向上放一点
        centerPanel.add(buttonPanel);

        submitBtn = new JButton("登 入");
        resetBtn = new JButton("清 空");

        setupButton(submitBtn);
        setupButton(resetBtn);

        buttonPanel.add(submitBtn);
        buttonPanel.add(Box.createHorizontalStrut(40));
        buttonPanel.add(resetBtn);

        // 按钮事件
        submitBtn.addActionListener(e -> {
            System.out.println("Username = " + usernameField.getText());
            System.out.println("Password = " + new String(passwordField.getPassword()));
            if (gameFrame != null) {
                gameFrame.setVisible(true);
                this.setVisible(false);
            }
        });

        resetBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        this.setVisible(true);
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

        ImageIcon originalIcon2 = new ImageIcon(getClass().getClassLoader().getResource("btn2.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        ImageIcon originalIcon3 = new ImageIcon(getClass().getClassLoader().getResource("btn3.png"));
        Image scaledImage3 = originalIcon3.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        button.setMargin(new Insets(5, 10, 5, 10));

        // 显示按钮文字
        button.setText(button.getText());
        button.setForeground(new Color(60, 30, 0)); // 文字颜色
        button.setHorizontalTextPosition(SwingConstants.CENTER); // 文字居中
        button.setVerticalTextPosition(SwingConstants.CENTER);  // 文字垂直居中

        button.setPreferredSize(new Dimension(160, 65));
        button.setMaximumSize(new Dimension(160, 65));

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
                button.setIcon(new ImageIcon(scaledImage2));
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
