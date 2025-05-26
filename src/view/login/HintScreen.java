package view.login;

import view.Language;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class HintScreen extends JDialog {
    private JLabel messageLabel;
    private JButton confirmButton;
    private ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("lose_bg.gif"));

    public HintScreen(String message, Language currentLanguage) {
        // 根据语言设置标题
        if (currentLanguage == Language.CHINESE) {
            setTitle("提示");
        } else {
            setTitle("Hint");
        }
        setSize(300, 260);
        setLocationRelativeTo(null);
        setUndecorated(true); // 去除标题栏
        setLayout(new BorderLayout());

        // 背景面板
        JPanel backgroundPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(true);
        add(backgroundPanel);

        // 中央提示文字
        messageLabel = new JLabel("<html><div style='text-align:center; width:200px;'>" + message + "</div></html>", JLabel.CENTER);
        messageLabel.setFont(new Font("楷体", Font.BOLD, 24));
        messageLabel.setForeground(Color.WHITE);
        backgroundPanel.add(messageLabel, BorderLayout.CENTER);

        // 底部按钮
        confirmButton = new JButton();
        setupButton(confirmButton);
        if (currentLanguage == Language.CHINESE) {
            confirmButton.setText("确 认");
        } else {
            confirmButton.setText("Confirm");
        }
        confirmButton.addActionListener(e -> this.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(confirmButton);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("楷体", Font.BOLD, 20));
        button.setForeground(new Color(60, 30, 0));

        ImageIcon originalIcon1 = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage1));

        ImageIcon originalIcon2 = new ImageIcon(getClass().getClassLoader().getResource("btn3.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        ImageIcon originalIcon3 = new ImageIcon(getClass().getClassLoader().getResource("btn2.png"));
        Image scaledImage3 = originalIcon3.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(160, 100));
        button.setMaximumSize(new Dimension(160, 100));

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
}
