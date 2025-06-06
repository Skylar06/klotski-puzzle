package view;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class LeaderboardFrame extends JFrame {
    private Leaderboard leaderboard;
    private static final String SAVE_FILE = "rank.txt";
    private static final int DISPLAY_COUNT = 10;
    private JPanel listPanel;

    public LeaderboardFrame() {
        this.setTitle("排行榜");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        //加载数据
        leaderboard = Leaderboard.getInstance();

        //背景
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bg = new ImageIcon(getClass().getClassLoader().getResource("victory_bg.gif"));
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());
        this.setContentPane(bgPanel);

        //标题
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon scrollBg = new ImageIcon(getClass().getClassLoader().getResource("btn1.png"));
                int imgWidth = (int)(getWidth() * 0.4);
                int imgHeight = (int)(getHeight() * 0.8);
                int x = (getWidth() - imgWidth) / 2;
                int y = (getHeight() - imgHeight) / 2;
                g.drawImage(scrollBg.getImage(), x, y, imgWidth, imgHeight, this);
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setPreferredSize(new Dimension(200, 160));
        titlePanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("排行榜");
        titleLabel.setFont(new Font("楷体", Font.BOLD, 36));
        titleLabel.setForeground(new Color(90, 50, 20));
        titlePanel.add(titleLabel);

        //列表
        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JScrollBar bar = scrollPane.getVerticalScrollBar();
                int delta = e.getWheelRotation() * bar.getUnitIncrement();
                int newValue = bar.getValue() + delta;
                newValue = Math.max(0, Math.min(newValue, bar.getMaximum()));
                bar.setValue(newValue);
            }
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 80, 20, 80));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        //按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("归返");
        setupButton(backButton);
        backButton.setFont(new Font("楷体", Font.BOLD, 20));
        backButton.addActionListener(e -> this.setVisible(false));
        buttonPanel.add(backButton);

        bgPanel.add(titlePanel, BorderLayout.NORTH);
        bgPanel.add(contentPanel, BorderLayout.CENTER);
        bgPanel.add(buttonPanel, BorderLayout.SOUTH);

        startDisplayRecords();
        this.setVisible(true);
    }

    private void startDisplayRecords() {
        List<String> usersList = leaderboard.getUsersList();
        List<Integer> stepsList = leaderboard.getStepsList();
        List<Integer> timesList = leaderboard.getTimesList();
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < usersList.size(); i++) {
            records.add(new Record(stepsList.get(i), timesList.get(i), usersList.get(i)));
        }

        //只取每个用户的最高成绩
        Map<String, Record> bestRecords = new HashMap<>();
        for (Record record : records) {
            if (!bestRecords.containsKey(record.user)) {
                bestRecords.put(record.user, record);
            } else {
                int currentScore = calculateScore(bestRecords.get(record.user).steps, bestRecords.get(record.user).time);
                int newScore = calculateScore(record.steps, record.time);
                if (newScore > currentScore) {
                    bestRecords.put(record.user, record);
                }
            }
        }

        //排序
        List<Record> sortedRecords = new ArrayList<>(bestRecords.values());
        sortedRecords.sort((record1, record2) -> {
            int score1 = calculateScore(record1.steps, record1.time);
            int score2 = calculateScore(record2.steps, record2.time);
            return Integer.compare(score2, score1);
        });

        int displayLimit = Math.min(DISPLAY_COUNT, sortedRecords.size());
        for (int i = 0; i < displayLimit; i++) {
            Record record = sortedRecords.get(i);
            int score = calculateScore(record.steps, record.time);
            JPanel recordItem = new JPanel();
            recordItem.setLayout(new BoxLayout(recordItem, BoxLayout.Y_AXIS));
            recordItem.setOpaque(false);
            recordItem.setBackground(new Color(255, 255, 255, 120));
            recordItem.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            Color deepText = new Color(90, 50, 20);

            JLabel topLine = new JLabel("第 " + (i + 1) + " 名   用户: " + record.user + "   得分: " + score);
            topLine.setFont(new Font("楷体", Font.BOLD, 20));
            topLine.setForeground(deepText);
            topLine.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel bottomLine = new JLabel("步数: " + record.steps + "   时间: " + formatTime(record.time));
            bottomLine.setFont(new Font("楷体", Font.PLAIN, 18));
            bottomLine.setForeground(deepText);
            bottomLine.setAlignmentX(Component.LEFT_ALIGNMENT);

            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            separator.setForeground(Color.WHITE);
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            separator.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (i == 0) {
                recordItem.setBackground(new Color(255, 255, 153, 160)); // 金色调
                recordItem.setOpaque(true);
            } else if (i == 1) {
                recordItem.setBackground(new Color(200, 200, 200, 160)); // 银色调
                recordItem.setOpaque(true);
            } else if (i == 2) {
                recordItem.setBackground(new Color(205, 127, 50, 160)); // 铜色调
                recordItem.setOpaque(true);
            }

            recordItem.add(topLine);
            recordItem.add(Box.createVerticalStrut(5));
            recordItem.add(bottomLine);
            recordItem.add(Box.createVerticalStrut(5));
            recordItem.add(separator);

            listPanel.add(Box.createVerticalStrut(8));
            listPanel.add(recordItem);
        }
    }

    private static class Record {
        int steps;
        int time;
        String user;

        public Record(int steps, int time, String user) {
            this.steps = steps;
            this.time = time;
            this.user = user;
        }
    }

    private int calculateScore(int steps, int time) {
        return 1000 - steps * 5 - time * 3;
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LeaderboardFrame frame = new LeaderboardFrame();
        });
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
}