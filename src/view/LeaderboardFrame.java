package view;

import view.Leaderboard;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import java.util.ArrayList;

public class LeaderboardFrame extends JFrame {
    private Leaderboard leaderboard;
    private static final String SAVE_FILE = "rank.txt";
    private static final int DISPLAY_COUNT = 10; // 显示前10名

    public LeaderboardFrame() {
        this.setTitle("排行榜");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 允许关闭窗口
        this.setResizable(false); // 禁止调整窗口大小

        // 加载排行榜数据
        leaderboard = Leaderboard.getInstance();

        // 创建背景面板
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 绘制背景图片
                ImageIcon bg = new ImageIcon(getClass().getClassLoader().getResource("victory_bg.gif"));
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());
        this.setContentPane(bgPanel);

        // 创建标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("排行榜");
        titleLabel.setFont(new Font("楷体", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // 创建排行榜内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        // 创建排行榜列表
        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // 获取并排序排行榜数据
        List<String> usersList = leaderboard.getUsersList();
        List<Integer> stepsList = leaderboard.getStepsList();
        List<Integer> timesList = leaderboard.getTimesList();
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < usersList.size(); i++) {
            records.add(new Record(stepsList.get(i), timesList.get(i), usersList.get(i)));
        }
        // 按得分排序
        records.sort((record1, record2) -> {
            int score1 = calculateScore(record1.steps, record1.time);
            int score2 = calculateScore(record2.steps, record2.time);
            return Integer.compare(score2, score1); // 降序排序
        });

        // 显示前10名
        int displayLimit = Math.min(DISPLAY_COUNT, records.size());
        for (int i = 0; i < displayLimit; i++) {
            Record record = records.get(i);
            int score = calculateScore(record.steps, record.time);
            JLabel recordLabel = new JLabel((i + 1) + ". 用户: " + record.user + " 得分: " + score + " 步数: " + record.steps + " 时间: " + formatTime(record.time));
            recordLabel.setFont(new Font("楷体", Font.PLAIN, 20));
            recordLabel.setForeground(Color.WHITE);
            recordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(recordLabel);
        }

        // 如果记录不足10条，不显示剩余条目
        if (records.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无记录");
            emptyLabel.setFont(new Font("楷体", Font.PLAIN, 20));
            emptyLabel.setForeground(Color.WHITE);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLabel);
        }

        contentPanel.add(listPanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("返回");
        backButton.setFont(new Font("楷体", Font.BOLD, 20));
        backButton.addActionListener(e -> {
            this.setVisible(false);
        });
        buttonPanel.add(backButton);

        // 组装整个界面
        bgPanel.add(titlePanel, BorderLayout.NORTH);
        bgPanel.add(contentPanel, BorderLayout.CENTER);
        bgPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    // 计算得分
    private int calculateScore(int steps, int time) {
        return 1000 - steps * 5 - time * 3;
    }

    // 格式化时间
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
}