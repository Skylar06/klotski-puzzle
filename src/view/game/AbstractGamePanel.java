package view.game;

import controller.GameController;
import model.Direction;
import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象游戏面板类，封装共有功能：地图渲染、移动逻辑、胜利判断等。
 */
public abstract class AbstractGamePanel extends ListenerPanel {
    protected List<BoxComponent> boxes;
    protected MapModel model;
    protected GameController controller;
    protected JLabel stepLabel;
    protected int steps = 0;
    public JLabel timeLabel;
    private Timer timer;
    private int elapsedTime = 0;
    protected final int GRID_SIZE = 90;
    protected BoxComponent selectedBox = null;
    private final int INITIAL_WIDTH = 1000;
    private final int INITIAL_HEIGHT = 750;

    protected JPanel boardPanel;   // 棋盘区域
    protected JPanel statusPanel;  // 状态区域（剧情）
    protected JPanel skillPanel;   // 技能区域
    protected JPanel outerPanel;   // 外框架，用于统一布局

    private Image boardBg;
    private Image statusBg;
    private Image skillBg;
    private Image globalBg;


    public AbstractGamePanel(MapModel model) {
        this.model = model;
        this.boxes = new ArrayList<>();
        setLayout(new BorderLayout());
        setFocusable(true);
        setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
        setOpaque(false);

        loadBackgrounds();
        initAllPanels();

        // 初始化计时器（1秒触发一次）
        timer = new Timer(1000, e -> updateTimeLabel());
        timer.start(); // 关键：启动计时器

        initialGame();
    }

    private void loadBackgrounds() {
        try {
            globalBg = loadImage("game_bg.png");
            boardBg = loadImage("board_bg.png");
            statusBg = loadImage("status_bg.png");
            skillBg = loadImage("skill_bg.png");
        } catch (Exception e) {
            System.err.println("背景图片加载失败：" + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private Image loadImage(String name) throws IOException {
        var url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            throw new IOException("资源未找到: " + name);
        }
        return new ImageIcon(url).getImage();
    }

    public void initAllPanels() {
        // 外框架使用 GridBagLayout 实现更灵活的布局
        outerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (globalBg != null) {
                    int xOffset = 30;
                    int yOffset = 30;
                    g.drawImage(globalBg, xOffset, yOffset,
                            getWidth() - 2 * xOffset,
                            getHeight() - 2 * yOffset, this);
                }
            }
        };
        outerPanel.setOpaque(false);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        add(outerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();

        // 左侧区域（状态区 + 技能区）
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        // 状态区
        // 状态区布局优化版
        statusPanel = new BackgroundPanel(statusBg);
        statusPanel.setPreferredSize(new Dimension(200, 160));
        statusPanel.setMaximumSize(new Dimension(200, 160));
        statusPanel.setOpaque(true);  // 必须设置为true才能显示背景色
        statusPanel.setBackground(new Color(30, 30, 30));  // 深色背景

// 使用BoxLayout垂直布局
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

// 创建带样式的标签
        stepLabel = createStyledLabel("步数: 0", "楷体", Font.BOLD, 22, Color.WHITE);
        timeLabel = createStyledLabel("时间: 00:00", "楷体", Font.BOLD, 20, Color.WHITE);

// 垂直布局配置
        statusPanel.add(Box.createVerticalGlue());  // 顶部弹性空间
        statusPanel.add(stepLabel);
        statusPanel.add(Box.createVerticalStrut(10));  // 标签间距
        statusPanel.add(timeLabel);
        statusPanel.add(Box.createVerticalGlue());  // 底部弹性空间

        leftPanel.add(statusPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20))); // 间距

        // 技能区
        skillPanel = new BackgroundPanel(skillBg);
        skillPanel.setPreferredSize(new Dimension(200, 200));
        skillPanel.setMaximumSize(new Dimension(200, 200));
        leftPanel.add(skillPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        outerPanel.add(leftPanel, gbc);

        // 棋盘区（使用固定比例）
        boardPanel = new BackgroundPanel(boardBg){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        boardPanel.setLayout(null);
        boardPanel.setPreferredSize(new Dimension(450, 360));
        boardPanel.setFocusable(true);
        // 新增：添加键盘监听器并请求焦点
        boardPanel.setFocusable(true);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 30, 0, 0); // 与左侧保持间距
        gbc.anchor = GridBagConstraints.CENTER;
        outerPanel.add(boardPanel, gbc);

        setPreferredSize(new Dimension(900, 600));
        setMaximumSize(new Dimension(900, 600));
    }

    // 辅助方法：创建统一样式的标签
    public JLabel createStyledLabel(String text, String fontName, int fontStyle, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(fontName, fontStyle, fontSize));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);  // 水平居中
        label.setVerticalAlignment(SwingConstants.CENTER);  // 垂直居中
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));  // 添加内边距
        return label;
    }

    private void updateTimeLabel() {
        elapsedTime++; // 每次触发增加1秒
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timeLabel.setText(String.format("时间: %02d:%02d", minutes, seconds));
    }


    public void initialGame() {
        boxes.clear();
        boardPanel.removeAll();

        int[][] map = new int[model.getHeight()][model.getWidth()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = model.getId(i, j);
            }
        }

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                BoxComponent box = null;
                switch (map[i][j]) {
                    case 1 -> {
                        box = new BoxComponent(Color.ORANGE, i, j);
                        box.setSize(GRID_SIZE, GRID_SIZE);
                    }
                    case 2 -> {
                        box = new BoxComponent(Color.PINK, i, j);
                        box.setSize(GRID_SIZE * 2, GRID_SIZE);
                        map[i][j + 1] = 0;
                    }
                    case 3 -> {
                        box = new BoxComponent(Color.BLUE, i, j);
                        box.setSize(GRID_SIZE, GRID_SIZE * 2);
                        map[i + 1][j] = 0;
                    }
                    case 4 -> {
                        box = new BoxComponent(Color.GREEN, i, j);
                        box.setSize(GRID_SIZE * 2, GRID_SIZE * 2);
                        map[i + 1][j] = map[i][j + 1] = map[i + 1][j + 1] = 0;
                    }
                }
                if (box != null) {
                    box.setLocation(j * GRID_SIZE, i * GRID_SIZE);
                    boxes.add(box);
                    boardPanel.add(box);
                }
            }
        }
        this.repaint();

        revalidate();
        repaint();

        boardPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (selectedBox == null) return;
                Direction direction = switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> Direction.LEFT;
                    case KeyEvent.VK_RIGHT -> Direction.RIGHT;
                    case KeyEvent.VK_UP -> Direction.UP;
                    case KeyEvent.VK_DOWN -> Direction.DOWN;
                    default -> null;
                };
                if (direction != null && controller.doMove(selectedBox.getRow(), selectedBox.getCol(), direction)) {
                    afterMove();
                }
            }
        });

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boardPanel.requestFocusInWindow();
                if(e.getButton() == MouseEvent.BUTTON1) {
                    doMouseClick(e.getPoint());
                }
            }
        });

        boardPanel.setFocusable(true);
        SwingUtilities.invokeLater(() -> {
            boardPanel.requestFocus();
        });
    }

    @Override
    public void doMouseClick(Point point) {
        Point p = SwingUtilities.convertPoint(this, point, boardPanel);
        System.out.println(point);
        Component c = boardPanel.getComponentAt(point);
        if (c instanceof BoxComponent clicked) {
            if (selectedBox == null) {
                selectedBox = clicked;
                selectedBox.setSelected(true);
            } else if (selectedBox != clicked) {
                selectedBox.setSelected(false);
                clicked.setSelected(true);
                selectedBox = clicked;
            } else {
                clicked.setSelected(false);
                selectedBox = null;
            }
        }
    }

    @Override
    public void doMoveRight() {
        System.out.println("Click VK_RIGHT");
        if (selectedBox != null) {
            if (controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.RIGHT)) {
                afterMove();// 移动成功后更新步数
            }
        }
    }

    @Override
    public void doMoveLeft() {
        if (selectedBox != null && controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.LEFT)) {
            afterMove();
        }
    }

    @Override
    public void doMoveUp() {
        if (selectedBox != null && controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.UP)) {
            afterMove();
        }
    }

    @Override
    public void doMoveDown() {
        if (selectedBox != null && controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.DOWN)) {
            afterMove();
        }
    }

    public BoxComponent afterMove() {
        this.steps++;
        if (stepLabel != null) stepLabel.setText(String.format("Step: %d", this.steps));
        //updateTimeLabel();
        checkWinCondition();
        return null;
    }

    public void undoMove() {
        controller.undoLastMove();
    }

    private void checkWinCondition() {
        if (model.getId(1, 4) == 4 && model.getId(2, 4) == 4) {
            JOptionPane.showMessageDialog(this, "恭喜你！成功将曹操移到了出口！");
        }
    }

    public void setStepLabel(JLabel stepLabel) {
        this.stepLabel = stepLabel;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public BoxComponent getSelectedBox() {
        return selectedBox;
    }

    public int getGRID_SIZE() {
        return GRID_SIZE;
    }

    public void loadGame(String path) {
        controller.loadGame(path);
    }

    public void saveGame(String path) {
        controller.saveGame(path);
    }

    /**
     * 用于带背景图的面板组件
     */
    protected static class BackgroundPanel extends JPanel {
        private final Image bg;

        public BackgroundPanel(Image bg) {
            this.bg = bg;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public JLabel getStepLabel() {
        return stepLabel;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }
    public void pauseTimer(){
        this.timer.stop();
    }
    public void restartTimer(){
        this.timer.start();
    }
}
