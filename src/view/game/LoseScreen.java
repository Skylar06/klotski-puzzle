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
    private JLabel loseLabel;
    private JButton restartButton;
    private JButton mainMenuButton;
    private JButton leaderboardButton;
    private GameController gameController;
    private ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("lose_bg.gif"));

    public LoseScreen(String time, String steps,Language currentLanguage) {
        setTitle("游戏失败");
        setSize(500, 330);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setOpaque(true);
        add(backgroundPanel);

//        JPanel scorePanel = new JPanel();
//        scorePanel.setLayout(new BorderLayout());
//        scorePanel.setOpaque(false);

        //失败提示
        losePanel = new JPanel();
        losePanel.setLayout(new BoxLayout(losePanel, BoxLayout.Y_AXIS));
        losePanel.setOpaque(false);

        JPanel loseHintPanel = new JPanel();
        loseHintPanel.setLayout(new GridLayout(1, 1));
        loseHintPanel.setOpaque(false);
        loseLabel = new JLabel((currentLanguage == Language.CHINESE)?"失败！":"Lose!", JLabel.CENTER);
        loseLabel.setFont(new Font("楷体", Font.BOLD, 40));
        loseLabel.setForeground(Color.WHITE);
        loseHintPanel.add(loseLabel);

        //用时
        JPanel timeCountPanel = new JPanel();
        timeCountPanel.setLayout(new GridLayout(2, 1));
        timeCountPanel.setOpaque(false);
        JLabel timeCountTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"用时":"Time", JLabel.CENTER);
        timeCountTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeCountTextLabel.setForeground(Color.WHITE);
        JLabel timeCountValueLabel = new JLabel(time, JLabel.CENTER);
        timeCountValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeCountValueLabel.setForeground(Color.WHITE);
        timeCountPanel.add(timeCountTextLabel);
        timeCountPanel.add(timeCountValueLabel);

        //步数
        JPanel stepCountPanel = new JPanel();
        stepCountPanel.setLayout(new GridLayout(2, 1));
        stepCountPanel.setOpaque(false);
        JLabel stepCountTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"步数":"Steps", JLabel.CENTER);
        stepCountTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        stepCountTextLabel.setForeground(Color.WHITE);
        JLabel stepCountValueLabel = new JLabel(steps, JLabel.CENTER);
        stepCountValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        stepCountValueLabel.setForeground(Color.WHITE);
        stepCountPanel.add(stepCountTextLabel);
        stepCountPanel.add(stepCountValueLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 45));
        infoPanel.setOpaque(false);
        infoPanel.add(timeCountPanel);
        infoPanel.add(loseHintPanel);
        infoPanel.add(stepCountPanel);

        //按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

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

        //排行榜
        leaderboardButton = new JButton();
        if (currentLanguage == Language.CHINESE) {
            leaderboardButton.setText("封神榜");
        } else {
            leaderboardButton.setText("Leader Board");
        }

        leaderboardButton.setFont(new Font("楷体", Font.PLAIN, 18));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setBorderPainted(false);
        leaderboardButton.setContentAreaFilled(false);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setOpaque(false);
        leaderboardButton.setMargin(new Insets(0, 0, 0, 0));

        buttonPanel.add(Box.createVerticalStrut(0));
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
                gameController.gameFrame1.dispose();
            }
        });

        leaderboardButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 100, 0));
            }

            public void mouseExited(MouseEvent e) {
                leaderboardButton.setForeground(Color.WHITE);
            }

            public void mousePressed(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 10, 0));
                LeaderboardFrame frame = new LeaderboardFrame();
                frame.setVisible(true);
            }
        });

        losePanel.add(Box.createVerticalStrut(30));
        losePanel.add(infoPanel,BorderLayout.NORTH);
        losePanel.add(infoPanel,BorderLayout.CENTER);
        losePanel.add(buttonPanel,BorderLayout.SOUTH);
        backgroundPanel.add(losePanel, BorderLayout.CENTER);
    }

    private void setupButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("楷体", Font.BOLD, 22));

        //图片
        ImageIcon originalIcon1 = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage1));

        ImageIcon originalIcon2 = new ImageIcon(getClass().getClassLoader().getResource("btn3.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        ImageIcon originalIcon3 = new ImageIcon(getClass().getClassLoader().getResource("btn2.png"));
        Image scaledImage3 = originalIcon3.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        //文字
        button.setText(button.getText());
        button.setForeground(new Color(60, 30, 0));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(160, 120));
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

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                view.game.LoseScreen loseScreen = new view.game.LoseScreen("2:30", "25步",Language.CHINESE);
//                loseScreen.setVisible(true);
//            }
//        });
//    }
}


