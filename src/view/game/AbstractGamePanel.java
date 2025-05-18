package view.game;

import controller.GameController;
import model.Direction;
import model.LevelManager;
import model.MapModel;
import view.Language;
import view.Leaderboard;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
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
    protected int elapsedTime = 0;
    protected final int GRID_SIZE = 90;
    protected BoxComponent selectedBox = null;
    private final int INITIAL_WIDTH = 1000;
    private final int INITIAL_HEIGHT = 750;
    private JButton skinToggleBtn;
    private int skillCount = 0;

    protected JPanel boardPanel;   // 棋盘区域
    protected JPanel statusPanel;  // 状态区域（剧情）
    protected JPanel skillPanel;   // 技能区域
    protected JPanel outerPanel;   // 外框架，用于统一布局

    private Image boardBg;
    private Image statusBg;
    private Image skillBg;
    private Image globalBg;
    private Language currentLanguage = Language.CHINESE;
    private JLabel[] skillLabels = new JLabel[4];
    private final String[] skillNames = {"破阵", "摘星", "风云", "无常"};
    private final String[] iconPaths = {
            "skill_remove.png",
            "skill_highlight.png",
            "skill_shuffle.png",
            "skill_random.png"
    };
    private boolean[] skillUsed = new boolean[4]; // 每个技能是否用过
    private JLabel[] centerLabels = new JLabel[4]; // 显示在按钮中间的 "1"/"+" 标签
    // 建议加在类的成员变量中统一配置
    private static final int BOARD_ROWS = 4; // 高
    private static final int BOARD_COLS = 5; // 宽
    private BoxComponent draggedBox;
    private int originalRow;  // 新增：记录拖拽起始位置的行
    private int originalCol;  // 新增：记录拖拽起始位置的列

    public AbstractGamePanel(MapModel model) {
        this.model = model;
        this.boxes = new ArrayList<>();
        setLayout(new BorderLayout());
        setFocusable(true);
        setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
        setOpaque(false);

        loadBackgrounds();
        initAllPanels();

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
        skillPanel.setLayout(new GridLayout(2, 2, 5, 5));  // 四宫格布局
        for (int i = 0; i < 4; i++) {
            String skillName = skillNames[i];
            String iconPath = iconPaths[i];
            JPanel buttonPanel = createSkillButton(skillName, iconPath);  // 修改返回类型为 JPanel
            skillPanel.add(buttonPanel);
        }

        leftPanel.add(skillPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20))); // 间距

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        outerPanel.add(leftPanel, gbc);

        // 棋盘区（使用固定比例）
        boardPanel = new BackgroundPanel(boardBg) {
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

        String[] skinList = SkinManager.getAvailableSkins();
        int[] currentSkinIndex = {0};

        skinToggleBtn = new JButton("当前皮肤：" + skinList[currentSkinIndex[0]]);
        skinToggleBtn.setFont(new Font("楷体", Font.PLAIN, 15));
        skinToggleBtn.setForeground(new Color(255, 248, 220)); // 淡灰色
        skinToggleBtn.setBorderPainted(false);
        skinToggleBtn.setContentAreaFilled(false);
        skinToggleBtn.setFocusPainted(false);
        skinToggleBtn.setOpaque(false);


        skinToggleBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                skinToggleBtn.setForeground(new Color(250, 250, 0));  // 改为高亮色
            }

            public void mouseExited(MouseEvent e) {
                skinToggleBtn.setForeground(new Color(255, 248, 220));  // 恢复默认色
            }

            public void mousePressed(MouseEvent e) {
                skinToggleBtn.setForeground(new Color(255, 180, 0));  // 按下时的颜色
                // 切换到下一个皮肤
                currentSkinIndex[0] = (currentSkinIndex[0] + 1) % skinList.length;
                String newSkin = skinList[currentSkinIndex[0]];
                BoxComponent.setCurrentSkin(newSkin);
                skinToggleBtn.setText("当前皮肤：" + newSkin);

                // 刷新所有 BoxComponent（假设你有 boardPanel 或类似容器）
                boardPanel.repaint(); // 或者你可以遍历所有 box：box.repaint()

                System.out.println("切换皮肤：" + newSkin);
            }
        });
        skinToggleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(skinToggleBtn, BorderLayout.SOUTH);

    }

    public void updateCommonLabels(Language currentLanguage) {
        if (currentLanguage == Language.CHINESE) {
            skinToggleBtn.setText("当前皮肤：" + BoxComponent.getCurrentSkin());
            updateSkillLabels(Language.CHINESE);
        } else { // English
            skinToggleBtn.setText("Current Skin: " + BoxComponent.getCurrentSkin());
            updateSkillLabels(Language.ENGLISH);
        }
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";

        // 更新步数标签、时间标签的文本
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
    }

    public void updateSkillLabels(Language language) {
        for (int i = 0; i < skillLabels.length; i++) {
            String key = skillNames[i];
            String text = (language == Language.CHINESE)
                    ? getChineseSkillName(key)
                    : getEnglishSkillName(key);
            skillLabels[i].setText(text);
        }
    }

    // 辅助方法：获取对应语言的技能名称
    private String getChineseSkillName(String key) {
        switch (key) {
            case "破阵":
                return "破阵";
            case "摘星":
                return "摘星";
            case "风云":
                return "风云";
            case "无常":
                return "无常";
            default:
                return key;
        }
    }

    private String getEnglishSkillName(String key) {
        switch (key) {
            case "破阵":
                return "Remove";
            case "摘星":
                return "Highlight";
            case "风云":
                return "Shuffle";
            case "无常":
                return "Random";
            default:
                return key;
        }
    }

    public abstract void updateLanguageTexts(Language currentLanguage);

    private JPanel createSkillButton(String text, String iconPath) {
        int index = skillCount; // 保存当前下标

        JButton button = new JButton();
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        ImageIcon icon = loadIcon(iconPath);
        button.setIcon(icon);

        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setFont(new Font("楷体", Font.BOLD, 20));
        textLabel.setForeground(Color.WHITE);
        textLabel.setVisible(false);
        skillLabels[skillCount] = textLabel;

        // 中心数字标签（初始为"1"）
        JLabel centerLabel = new JLabel("1", SwingConstants.CENTER);
        centerLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        centerLabel.setForeground(Color.YELLOW);
        centerLabel.setVisible(false);
        centerLabels[skillCount] = centerLabel;

        skillCount++;

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(button, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.SOUTH);
        panel.add(centerLabel, BorderLayout.NORTH);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(null);
                textLabel.setVisible(true);
                centerLabel.setText(skillUsed[index] ? "+" : "1");
                centerLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(icon);
                textLabel.setVisible(false);
                centerLabel.setVisible(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!skillUsed[index]) {
                    handleSkill(text);
                    skillUsed[index] = true;
                } else {
                    showAdPopup(index);
                    // 弹出广告
                }
                // 🔄 模拟再次悬停以刷新按钮状态
                MouseEvent fakeHover = new MouseEvent(button, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, 0, 0, 0, false);
                for (MouseListener ml : button.getMouseListeners()) {
                    ml.mouseEntered(fakeHover);
                }
            }
        });

        return panel;
    }

    private void showAdPopup(int index) {
        JDialog adDialog = new JDialog((Frame) null, "观看广告", true);
        adDialog.setSize(400, 300);
        adDialog.setLocationRelativeTo(null);
        adDialog.setLayout(null);

        // ---- 大图广告区域 ----
        JLabel imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, 400, 300);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // ---- 倒计时标签，叠加在图片左上角 ----
        JLabel countdownLabel = new JLabel("10");
        countdownLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        countdownLabel.setForeground(Color.WHITE);
        countdownLabel.setBounds(10, 10, 100, 30); // 位置在左上角

        // ---- 分层面板：图片底层，倒计时顶层 ----
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));
        layeredPane.add(imageLabel, Integer.valueOf(0));
        layeredPane.add(countdownLabel, Integer.valueOf(1));

        adDialog.setContentPane(layeredPane);

        // ---- 加载广告图片并缩放 ----
        String[] adImages = {"ad1.png", "ad2.png", "ad3.png"};
        ImageIcon[] adIcons = new ImageIcon[adImages.length];
        for (int i = 0; i < adImages.length; i++) {
            java.net.URL url = getClass().getClassLoader().getResource(adImages[i]);
            if (url != null) {
                Image scaled = new ImageIcon(url).getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                adIcons[i] = new ImageIcon(scaled);
            } else {
                System.err.println("资源未找到: " + adImages[i]);
            }
        }

        // ---- 大图轮播 ----
        final int[] imageIndex = {0};
        Timer imageTimer = new Timer(1000, e -> {
            imageLabel.setIcon(adIcons[imageIndex[0]]);
            imageIndex[0] = (imageIndex[0] + 1) % adIcons.length;
        });
        imageTimer.start();

        // ---- 倒计时逻辑 ----
        Timer countdownTimer = new Timer(1000, null);
        countdownTimer.addActionListener(new ActionListener() {
            int secondsLeft = 10;

            @Override
            public void actionPerformed(ActionEvent e) {
                secondsLeft--;
                countdownLabel.setText(String.valueOf(secondsLeft));
                if (secondsLeft <= 0) {
                    countdownTimer.stop();
                    imageTimer.stop();
                    adDialog.dispose();
                    skillUsed[index] = false;
                }
            }
        });
        countdownTimer.start();

        adDialog.setVisible(true);
    }


    private void handleSkill(String skillName) {
        switch (skillName) {
            case "破阵" -> eliminateRandomBlock();
            case "摘星" -> {
                clearHighlight(); // 如果想重复使用，先清除
                highlightMovableBlocks();
            }
            case "风云" -> shuffleBoxes();
            case "无常" -> {
                int idx = (int) (Math.random() * 3);
                handleSkill(skillNames[idx]);
            }
        }
    }

    private void eliminateRandomBlock() {
        List<BoxComponent> candidates = new ArrayList<>();
        for (BoxComponent box : new ArrayList<>(boxes)) {
            if (box.getType() == 1) {
                candidates.add(box);
            }
        }
        if (candidates.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可以消除的方块了！");
            return;
        }

        BoxComponent toRemove = candidates.get((int) (Math.random() * candidates.size()));

        // 假设 BoxComponent 有 getRow() 和 getCol() 方法
        int row = toRemove.getRow();
        int col = toRemove.getCol();

        // 1. 更新模型数据，设为0表示空格
        model.getMatrix()[row][col] = 0;

        // 2. 移除视图和状态
        boardPanel.remove(toRemove);
        boxes.remove(toRemove);
        toRemove.setVisible(false);

        boardPanel.revalidate();
        boardPanel.repaint();

        clearHighlight();
    }


    private void highlightMovableBlocks() {
        for (BoxComponent box : boxes) {
            if (isMovable(box)) {
                box.setHighlighted(true);
            }
        }
        new javax.swing.Timer(5000, e -> clearHighlight()).start(); // 5 秒后自动清除
    }

    private boolean isMovable(BoxComponent box) {
        int row = box.getRow();
        int col = box.getCol();

        Rectangle bounds = box.getBounds();
        int w = (int) Math.ceil(bounds.getWidth() / (float) GRID_SIZE); // 精确计算占位格数
        int h = (int) Math.ceil(bounds.getHeight() / (float) GRID_SIZE);

        boolean[][] occupied = new boolean[BOARD_ROWS][BOARD_COLS];
        for (BoxComponent b : boxes) {
            if (b == box) continue; // ✨ 关键修复：排除自身
            Rectangle bBounds = b.getBounds();
            int r = b.getRow();
            int c = b.getCol();
            int bw = (int) Math.ceil(bBounds.getWidth() / (float) GRID_SIZE);
            int bh = (int) Math.ceil(bBounds.getHeight() / (float) GRID_SIZE);

            for (int i = 0; i < bh; i++) {
                for (int j = 0; j < bw; j++) {
                    int occupiedRow = r + i;
                    int occupiedCol = c + j;
                    if (occupiedRow < 0 || occupiedRow >= BOARD_ROWS) continue;
                    if (occupiedCol < 0 || occupiedCol >= BOARD_COLS) continue;
                    occupied[occupiedRow][occupiedCol] = true;
                }
            }
        }

        // 检查四个方向移动一格后的可行性
        return canMoveTo(row - 1, col, w, h, occupied) || // 上
                canMoveTo(row + 1, col, w, h, occupied) || // 下
                canMoveTo(row, col - 1, w, h, occupied) || // 左
                canMoveTo(row, col + 1, w, h, occupied);  // 右
    }

    private boolean canMoveTo(int targetRow, int targetCol, int w, int h, boolean[][] occupied) {
        // 边界检查
        if (targetRow < 0 || targetRow + h > BOARD_ROWS) return false;
        if (targetCol < 0 || targetCol + w > BOARD_COLS) return false;

        // 检查目标区域是否全部可移动
        for (int row = targetRow; row < targetRow + h; row++) {
            for (int col = targetCol; col < targetCol + w; col++) {
                if (occupied[row][col]) return false;
            }
        }
        return true;
    }

    private void clearHighlight() {
        for (BoxComponent box : boxes) {
            box.setHighlighted(false);
        }
    }

    private static class BlockInfo {
        int id;
        int type;

        BlockInfo(int id, int type) {
            this.id = id;
            this.type = type;
        }
    }

    public void shuffleBoxes() {
        int rows = model.getHeight();
        int cols = model.getWidth();

        // 原始模型中，关卡配置就是 type 值（1~4）
        int[][] oldMatrix = model.getMatrix();

        // 1. 统计原始方块类型（type），仅记录一次
        List<Integer> blockTypes = new ArrayList<>();
        boolean[][] visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!visited[i][j] && oldMatrix[i][j] > 0) {
                    int type = oldMatrix[i][j];
                    blockTypes.add(type);
                    markVisited(visited, i, j, type); // 标记已访问方块区域
                }
            }
        }

        // 2. 打乱类型顺序
        Collections.shuffle(blockTypes);

        // 3. 初始化空矩阵
        int[][] newMatrix = new int[rows][cols];

        // 4. 依次尝试放入新的位置
        int idx = 0;
        outer:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (idx >= blockTypes.size()) break outer;

                int type = blockTypes.get(idx);
                if (canPlace(newMatrix, i, j, type)) {
                    placeBlock(newMatrix, i, j, type);
                    idx++;
                }
            }
        }

        // 5. 更新模型并重建显示
        model.setMatrix(newMatrix);
        rebuildBoxesFromMatrix(newMatrix);
    }

    private void markVisited(boolean[][] visited, int row, int col, int type) {
        visited[row][col] = true;
        switch (type) {
            case 2 -> visited[row][col + 1] = true;
            case 3 -> visited[row + 1][col] = true;
            case 4 -> {
                visited[row][col + 1] = true;
                visited[row + 1][col] = true;
                visited[row + 1][col + 1] = true;
            }
        }
    }

    private int getBlockType(int[][] matrix, int id) {
        int count = 0;
        for (int[] row : matrix) {
            for (int cell : row) {
                if (cell == id) count++;
            }
        }

        return switch (count) {
            case 1 -> 1;     // 单格
            case 2 -> {      // 横2 或 竖2
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix[0].length; j++) {
                        if (matrix[i][j] == id) {
                            if (j + 1 < matrix[0].length && matrix[i][j + 1] == id) yield 2; // 横2
                            else yield 3; // 竖2
                        }
                    }
                }
                yield 3; // fallback
            }
            case 4 -> 4;     // 2x2
            default -> 1;    // fallback
        };
    }

    private void placeBlock(int[][] matrix, int row, int col, int type) {
        matrix[row][col] = type;
        switch (type) {
            case 2 -> matrix[row][col + 1] = type;
            case 3 -> matrix[row + 1][col] = type;
            case 4 -> {
                matrix[row][col + 1] = type;
                matrix[row + 1][col] = type;
                matrix[row + 1][col + 1] = type;
            }
        }
    }

    private boolean canPlace(int[][] matrix, int row, int col, int type) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        switch (type) {
            case 1:
                return matrix[row][col] == 0;
            case 2:
                return col + 1 < cols && matrix[row][col] == 0 && matrix[row][col + 1] == 0;
            case 3:
                return row + 1 < rows && matrix[row][col] == 0 && matrix[row + 1][col] == 0;
            case 4:
                return row + 1 < rows && col + 1 < cols
                        && matrix[row][col] == 0
                        && matrix[row][col + 1] == 0
                        && matrix[row + 1][col] == 0
                        && matrix[row + 1][col + 1] == 0;
            default:
                return false;
        }
    }

    private void rebuildBoxesFromMatrix(int[][] matrix) {
        boxes.clear();
        boardPanel.removeAll();
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int type = matrix[i][j];
                if (type != 0 && !visited[i][j]) {
                    BoxComponent box = new BoxComponent(type, i, j);
                    switch (type) {
                        case 1 -> box.setSize(GRID_SIZE, GRID_SIZE);
                        case 2 -> {
                            box.setSize(GRID_SIZE * 2, GRID_SIZE);
                            visited[i][j + 1] = true;
                        }
                        case 3 -> {
                            box.setSize(GRID_SIZE, GRID_SIZE * 2);
                            visited[i + 1][j] = true;
                        }
                        case 4 -> {
                            box.setSize(GRID_SIZE * 2, GRID_SIZE * 2);
                            visited[i][j + 1] = true;
                            visited[i + 1][j] = true;
                            visited[i + 1][j + 1] = true;
                        }
                    }
                    box.setLocation(j * GRID_SIZE, i * GRID_SIZE);
                    boxes.add(box);
                    boardPanel.add(box);
                    visited[i][j] = true;
                }
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private ImageIcon loadIcon(String path) {
        try {
            var url = getClass().getClassLoader().getResource(path);
            if (url != null) {
                Image image = new ImageIcon(url).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            } else {
                System.err.println("找不到图标: " + path);
                return null;
            }
        } catch (Exception e) {
            System.err.println("图标加载失败: " + path);
            return null;
        }
    }

    // 包装按钮为一个透明面板，方便布局与美观
    private JPanel wrapWithPanel(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(comp, BorderLayout.CENTER);
        return panel;
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

    public void updateTimeLabel() {
        elapsedTime++; // 每次触发增加1秒
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timeLabel.setText(String.format("时间: %02d:%02d", minutes, seconds));
        if (elapsedTime % 60 == 0&&this.controller.isVisitor()) {
            this.controller.saveGame("./"+this.controller.getUser()+".txt");
        }
    }


    public void initialGame() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        // 移除旧的键盘和鼠标监听器（需要保留对监听器的引用）
        for (KeyListener listener : boardPanel.getKeyListeners()) {
            boardPanel.removeKeyListener(listener);
        }
        for (MouseListener listener : boardPanel.getMouseListeners()) {
            boardPanel.removeMouseListener(listener);
        }
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
                        box = new BoxComponent(1, i, j);
                        box.setSize(GRID_SIZE, GRID_SIZE);
                    }
                    case 2 -> {
                        box = new BoxComponent(2, i, j);
                        box.setSize(GRID_SIZE * 2, GRID_SIZE);
                        map[i][j + 1] = 0;
                    }
                    case 3 -> {
                        box = new BoxComponent(3, i, j);
                        box.setSize(GRID_SIZE, GRID_SIZE * 2);
                        map[i + 1][j] = 0;
                    }
                    case 4 -> {
                        box = new BoxComponent(4, i, j);
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

        // 在AbstractGamePanel.java中修改MouseAdapter实现
        boardPanel.addMouseMotionListener(new MouseAdapter() {
            private Point startPoint; // 起始坐标
            private BoxComponent activeBox; // 当前激活的方块

            @Override
            public void mousePressed(MouseEvent e) {
                activeBox = (BoxComponent) boardPanel.getComponentAt(e.getPoint());
                if (activeBox != null) {
                    activeBox.setSelected(true);
                    startPoint = e.getPoint(); // 初始化起始坐标
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (activeBox == null || startPoint == null) return;

                // 计算相对位移
                int dx = e.getX() - startPoint.x;
                int dy = e.getY() - startPoint.y;

                // 动画移动方块
                activeBox.setLocationAnimated(
                        activeBox.getX() + dx,
                        activeBox.getY() + dy
                );

                // 更新起始坐标为当前位置（持续追踪）
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (activeBox == null) return;

                // 计算拖拽后的最终位置
                Point endPoint = e.getPoint();
                int newRow = activeBox.getRow() + (endPoint.y - startPoint.y) / GRID_SIZE;
                int newCol = activeBox.getCol() + (endPoint.x - startPoint.x) / GRID_SIZE;

                // 验证移动合法性（上下左右一格）
                Direction direction = null;
                if (newRow != activeBox.getRow() && newCol == activeBox.getCol()) {
                    direction = newRow > activeBox.getRow() ? Direction.DOWN : Direction.UP;
                } else if (newCol != activeBox.getCol() && newRow == activeBox.getRow()) {
                    direction = newCol > activeBox.getCol() ? Direction.RIGHT : Direction.LEFT;
                }

                if (direction != null && controller.doMove(activeBox.getRow(), activeBox.getCol(), direction)) {
                    afterMove();
                }

                activeBox.setSelected(false);
                activeBox = null;
                startPoint = null;
            }
        });

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
                if (e.getButton() == MouseEvent.BUTTON1) {
                    doMouseClick(e.getPoint());
                }
            }
        });

        boardPanel.setFocusable(true);
        SwingUtilities.invokeLater(() -> {
            boardPanel.requestFocus();
        });


        // 初始化计时器（1秒触发一次）
        timer = new Timer(1000, e -> updateTimeLabel());
        timer.start(); // 关键：启动计时器
    }

    @Override
    public void doMouseClick(Point point) {
        Point p = SwingUtilities.convertPoint(this, point, boardPanel);
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
            this.pauseTimer();
            if (LevelManager.getCurrentLevelIndex()==0){
                Leaderboard.instance.addRecord(this.controller.getUser(),this.steps, this.elapsedTime);
            }
            VictoryScreen v = new VictoryScreen(1000-this.elapsedTime*3-this.steps*5, String.format("%2d:%2d", this.elapsedTime / 60, this.elapsedTime % 60), String.format("%d", this.steps),
                    String.format("%2d:%2d",Leaderboard.instance.getFastestTime()/60,Leaderboard.instance.getFastestTime()%60), String.format("%d", Leaderboard.instance.getFastestSteps()), this.currentLanguage);
            v.setGameController(controller);
            v.setVisible(true);
            this.setVisible(false);
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

    public void pauseTimer() {
        if (this.timer != null) {
            this.timer.stop();
        }
    }

    public void restartTimer() {
        if (this.timer != null) {
            this.timer.start();
        }
    }

    public void setModel(MapModel model) {
        this.model = model;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setCurrentLanguage(Language currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
