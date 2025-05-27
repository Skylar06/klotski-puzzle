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

public class VictoryScreen extends JFrame {

    private JPanel victoryPanel;
    private JLabel victoryLabel;
    private JLabel scoreTextLabel;
    private JLabel scoreLabel;
    private JLabel fastestTimeLabel;
    private JLabel leastMovesLabel;
    private JButton nextLevelButton;
    private JButton restartButton;
    private JButton mainMenuButton;
    private JButton leaderboardButton;
    private GameController gameController;
    private ImageIcon backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("victory_bg.gif"));

    public VictoryScreen(int score, String time, String moves, String fastestTime, String leastMoves,Language currentLanguage) {
        setSize(600, 410);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(new BorderLayout());

        //背景
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

        //胜利面板
        victoryPanel = new JPanel();
        victoryPanel.setLayout(new BoxLayout(victoryPanel, BoxLayout.Y_AXIS));
        victoryPanel.setOpaque(false);

        //提示
        String message = "";
        if (currentLanguage == Language.CHINESE) {
            message = "贺捷！天机已破";
        } else {
            message = "Victory!";
        }
        victoryLabel = new JLabel(message, JLabel.CENTER);
        victoryLabel.setFont(new Font("楷体", Font.BOLD, 30));
        victoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel victoryLabelPanel = new JPanel();
        victoryLabelPanel.setOpaque(false);
        victoryLabelPanel.add(victoryLabel);
        victoryLabelPanel.setPreferredSize(new Dimension(getWidth(), 50));

        //信息
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());
        scorePanel.setOpaque(false);

        //得分
        JPanel scoreValuePanel = new JPanel();
        scoreValuePanel.setLayout(new GridLayout(2, 1));
        scoreValuePanel.setOpaque(false);
        scoreTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"得分":"Score", JLabel.CENTER);
        scoreTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        scoreLabel = new JLabel(String.valueOf(score), JLabel.CENTER);
        scoreLabel.setFont(new Font("楷体", Font.PLAIN, 40));
        scoreValuePanel.add(scoreTextLabel);
        scoreValuePanel.add(scoreLabel);

        //最快通关
        JPanel timeValuePanel = new JPanel();
        timeValuePanel.setLayout(new GridLayout(2, 1));
        timeValuePanel.setOpaque(false);
        JLabel timeTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"用时":"Time", JLabel.CENTER);
        timeTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel timeValueLabel = new JLabel(time, JLabel.CENTER);
        timeValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        timeValuePanel.add(timeTextLabel);
        timeValuePanel.add(timeValueLabel);

        //最快通关
        JPanel fastestValuePanel = new JPanel();
        fastestValuePanel.setLayout(new GridLayout(2, 1));
        fastestValuePanel.setOpaque(false);
        JLabel fastestTimeTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"最快通关":"Fastest", JLabel.CENTER);
        fastestTimeTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel fastestTimeValueLabel = new JLabel(fastestTime, JLabel.CENTER);
        fastestTimeValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        fastestValuePanel.add(fastestTimeTextLabel);
        fastestValuePanel.add(fastestTimeValueLabel);

        JPanel movesValuePanel = new JPanel();
        movesValuePanel.setLayout(new GridLayout(2, 1));
        movesValuePanel.setOpaque(false);
        JLabel movesTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"步数":"Steps", JLabel.CENTER);
        movesTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel movesValueLabel = new JLabel(moves, JLabel.CENTER);
        movesValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        movesValuePanel.add(movesTextLabel);
        movesValuePanel.add(movesValueLabel);

        //最少步数
        JPanel leastValuePanel = new JPanel();
        leastValuePanel.setLayout(new GridLayout(2, 1));
        leastValuePanel.setOpaque(false);
        JLabel leastMovesTextLabel = new JLabel((currentLanguage == Language.CHINESE)?"最少步数":"Least", JLabel.CENTER);
        leastMovesTextLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        JLabel leastMovesValueLabel = new JLabel(leastMoves, JLabel.CENTER);
        leastMovesValueLabel.setFont(new Font("楷体", Font.PLAIN, 20));
        leastValuePanel.add(leastMovesTextLabel);
        leastValuePanel.add(leastMovesValueLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 45));
        infoPanel.setOpaque(false);
        infoPanel.add(timeValuePanel);
        infoPanel.add(fastestValuePanel);
        infoPanel.add(scoreValuePanel);
        infoPanel.add(leastValuePanel);
        infoPanel.add(movesValuePanel);

        //按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JPanel controlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlButtonsPanel.setOpaque(false);

        restartButton = new JButton();
        nextLevelButton = new JButton();
        mainMenuButton = new JButton();

        setupButton(restartButton);
        setupButton(nextLevelButton);
        setupButton(mainMenuButton);

        if (currentLanguage == Language.CHINESE) {
            restartButton.setText("再启");
        } else {
            restartButton.setText("Restart");
        }

        if (currentLanguage == Language.CHINESE) {
            nextLevelButton.setText("转战");
        } else {
            nextLevelButton.setText("Continue");
        }

        if (currentLanguage == Language.CHINESE) {
            mainMenuButton.setText("归返");
        } else {
            mainMenuButton.setText("Back");
        }

        controlButtonsPanel.add(restartButton);
        controlButtonsPanel.add(nextLevelButton);
        controlButtonsPanel.add(mainMenuButton);

        buttonPanel.add(controlButtonsPanel);

        //排行榜按钮
        JPanel leaderboardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leaderboardPanel.setOpaque(false);

        leaderboardButton = new JButton();
        if (currentLanguage == Language.CHINESE) {
            leaderboardButton.setText("封神榜");
        } else {
            leaderboardButton.setText("Leader Board");
        }

        leaderboardButton.setFont(new Font("楷体", Font.PLAIN, 18));
        leaderboardButton.setForeground(new Color(50, 50, 50));
        leaderboardButton.setContentAreaFilled(false);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setOpaque(false);
        leaderboardButton.setMargin(new Insets(0, 0, 0, 0));

        buttonPanel.add(Box.createVerticalStrut(0));
        leaderboardPanel.add(leaderboardButton);
        buttonPanel.add(leaderboardPanel);

        nextLevelButton.addActionListener(e ->{
            GamePanel.nextLevel();
            this.setVisible(false);

        });

        restartButton.addActionListener(e -> {
            this.setVisible(false);
            this.gameController.restartGame();
        });

        mainMenuButton.addActionListener(e -> {
            this.setVisible(false);
            this.dispose();
            if (gameController.gameFrame1 != null) {
                gameController.gameFrame1.dispose();
            }
            this.gameController.levelSelectFrame.setVisible(true);
        });

        leaderboardButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 100, 0));
            }

            public void mouseExited(MouseEvent e) {
                leaderboardButton.setForeground(new Color(50, 50, 50));  // 恢复默认色
            }

            public void mousePressed(MouseEvent e) {
                leaderboardButton.setForeground(new Color(255, 10, 0));
                LeaderboardFrame frame = new LeaderboardFrame();
                frame.setVisible(true);
            }
        });

        victoryPanel.add(Box.createVerticalStrut(10));
        victoryPanel.add(victoryLabelPanel);
        victoryPanel.add(infoPanel);
        victoryPanel.add(buttonPanel);

        backgroundPanel.add(victoryPanel, BorderLayout.CENTER);
    }

    private void setupButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("楷体", Font.BOLD, 22));

        ImageIcon originalIcon1 = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
        Image scaledImage1 = originalIcon1.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage1));

        ImageIcon originalIcon2 = new ImageIcon(getClass().getClassLoader().getResource("btn3.png"));
        Image scaledImage2 = originalIcon2.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

        ImageIcon originalIcon3 = new ImageIcon(getClass().getClassLoader().getResource("btn2.png"));
        Image scaledImage3 = originalIcon3.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VictoryScreen victoryScreen = new VictoryScreen(1000,"2:30", "25步", "2:30", "25步",Language.CHINESE);
                victoryScreen.setVisible(true);
            }
        });
    }
}
