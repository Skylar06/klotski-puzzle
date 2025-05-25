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

public abstract class AbstractGamePanel extends ListenerPanel {
    protected List<BoxComponent> boxes;
    protected MapModel model;
    protected GameController controller;
    protected JLabel stepLabel;
    protected int steps = 0;
    public JLabel timeLabel;
    private Timer timer;
    protected int elapsedTime = 0;
    protected final int girdSize = 90;
    protected BoxComponent selectedBox = null;
    private final int initialWidth = 1000;
    private final int intitalHeight = 750;
    private JButton skinToggleBtn;
    private int skillCount = 0;
    protected JPanel boardPanel;
    protected JPanel statusPanel;
    protected JPanel skillPanel;
    protected JPanel outerPanel;
    private Image boardBg;
    private Image statusBg;
    private Image skillBg;
    private Image globalBg;
    private Language currentLanguage = Language.CHINESE;
    private JLabel[] skillLabels = new JLabel[4];
    private final String[] skillNames = {"破阵", "摘星", "风云", "无常"};
    private final String[] iconPaths = {"skill_remove.png", "skill_highlight.png", "skill_shuffle.png", "skill_random.png"};
    private boolean[] skillUsed = new boolean[4];
    private JLabel[] centerLabels = new JLabel[4];
    private static final int rows = 4;
    private static final int columns = 5;
//    private BoxComponent draggedBox;
//    private int originalRow;
//    private int originalCol;

    public AbstractGamePanel(MapModel model) {
        this.model = model;
        this.boxes = new ArrayList<>();
        setLayout(new BorderLayout());
        setFocusable(true);
        setPreferredSize(new Dimension(initialWidth, intitalHeight));
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
//            System.err.println("背景图片加载失败：" + e.getMessage());
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
        outerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (globalBg != null) {
                    int xOffset = 30;
                    int yOffset = 30;
                    g.drawImage(globalBg, xOffset, yOffset, getWidth() - 2 * xOffset, getHeight() - 2 * yOffset, this);
                }
            }
        };
        outerPanel.setOpaque(false);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        add(outerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        //状态
        statusPanel = new BackgroundPanel(statusBg);
        statusPanel.setPreferredSize(new Dimension(200, 160));
        statusPanel.setMaximumSize(new Dimension(200, 160));
        statusPanel.setOpaque(true);
        statusPanel.setBackground(new Color(30, 30, 30));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        stepLabel = createStyledLabel("步数: 0", "楷体", Font.BOLD, 22, Color.WHITE);
        timeLabel = createStyledLabel("时间: 00:00", "楷体", Font.BOLD, 20, Color.WHITE);

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(stepLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(timeLabel);
        statusPanel.add(Box.createVerticalGlue());

        leftPanel.add(statusPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        //技能
        skillPanel = new BackgroundPanel(skillBg);
        skillPanel.setPreferredSize(new Dimension(200, 200));
        skillPanel.setMaximumSize(new Dimension(200, 200));
        skillPanel.setLayout(new GridLayout(2, 2, 5, 5));
        for (int i = 0; i < 4; i++) {
            String skillName = skillNames[i];
            String iconPath = iconPaths[i];
            JPanel buttonPanel = createSkillButton(skillName, iconPath);
            skillPanel.add(buttonPanel);
        }

        leftPanel.add(skillPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        outerPanel.add(leftPanel, gbc);

        boardPanel = new BackgroundPanel(boardBg) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        boardPanel.setLayout(null);
        boardPanel.setPreferredSize(new Dimension(450, 360));
        boardPanel.setFocusable(true);
//        boardPanel.setFocusable(true);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 30, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        outerPanel.add(boardPanel, gbc);

        setPreferredSize(new Dimension(900, 600));
        setMaximumSize(new Dimension(900, 600));

        //换皮肤
        String[] skinList = SkinManager.getAvailableSkins();
        int[] currentSkinIndex = {0};

        skinToggleBtn = new JButton("当前皮肤：" + skinList[currentSkinIndex[0]]);
        skinToggleBtn.setFont(new Font("楷体", Font.PLAIN, 15));
        skinToggleBtn.setForeground(new Color(255, 248, 220));
        skinToggleBtn.setBorderPainted(false);
        skinToggleBtn.setContentAreaFilled(false);
        skinToggleBtn.setFocusPainted(false);
        skinToggleBtn.setOpaque(false);

        skinToggleBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                skinToggleBtn.setForeground(new Color(250, 250, 0));
            }

            public void mouseExited(MouseEvent e) {
                skinToggleBtn.setForeground(new Color(255, 248, 220));
            }

            public void mousePressed(MouseEvent e) {
                skinToggleBtn.setForeground(new Color(255, 180, 0));
                currentSkinIndex[0] = (currentSkinIndex[0] + 1) % skinList.length;
                String newSkin = skinList[currentSkinIndex[0]];
                BoxComponent.setCurrentSkin(newSkin);
                skinToggleBtn.setText("当前皮肤：" + newSkin);

                boardPanel.repaint();
//                System.out.println("切换皮肤：" + newSkin);
            }
        });
        skinToggleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(skinToggleBtn, BorderLayout.SOUTH);

    }

    protected void clearSkillButtons() {
        if (skillPanel != null) {
            skillPanel.removeAll();
            skillPanel.revalidate();
            skillPanel.repaint();
        }
    }

    public void updateCommonLabels(Language currentLanguage) {
        if (currentLanguage == Language.CHINESE) {
            skinToggleBtn.setText("当前皮肤：" + BoxComponent.getCurrentSkin());
            updateSkillLabels(Language.CHINESE);
        } else {
            skinToggleBtn.setText("Current Skin: " + BoxComponent.getCurrentSkin());
            updateSkillLabels(Language.ENGLISH);
        }
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
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

    private String getChineseSkillName(String key) {
        switch (key) {
            case "破阵": return "破阵";
            case "摘星": return "摘星";
            case "风云": return "风云";
            case "无常": return "无常";
            default: return key;
        }
    }

    private String getEnglishSkillName(String key) {
        switch (key) {
            case "破阵": return "Remove";
            case "摘星": return "Highlight";
            case "风云": return "Shuffle";
            case "无常": return "Random";
            default: return key;
        }
    }

    public abstract void updateLanguageTexts(Language currentLanguage);

    private JPanel createSkillButton(String text, String iconPath) {
        int index = skillCount;
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

        JLabel countLabel = new JLabel("1", SwingConstants.CENTER);
        countLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        countLabel.setForeground(Color.YELLOW);
        countLabel.setVisible(false);
        centerLabels[skillCount] = countLabel;

        skillCount++;

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(button, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.SOUTH);
        panel.add(countLabel, BorderLayout.NORTH);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(null);
                textLabel.setVisible(true);
                countLabel.setText(skillUsed[index] ? "+" : "1");
                countLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(icon);
                textLabel.setVisible(false);
                countLabel.setVisible(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!skillUsed[index]) {
                    handleSkill(text);
                    skillUsed[index] = true;
                } else {
                    showAddvertisement(index);
                }
                //模拟再次悬停！！！
                MouseEvent fakeHover = new MouseEvent(button, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, 0, 0, 0, false);
                for (MouseListener ml : button.getMouseListeners()) {
                    ml.mouseEntered(fakeHover);
                }
            }
        });
        return panel;
    }

    private void showAddvertisement(int index) {
        JDialog adDialog = new JDialog((Frame) null, "观看广告", true);
        adDialog.setSize(400, 300);
        adDialog.setLocationRelativeTo(null);
        adDialog.setLayout(null);

        JLabel imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, 400, 300);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        JLabel countdownLabel = new JLabel("10");
        countdownLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        countdownLabel.setForeground(Color.WHITE);
        countdownLabel.setBounds(10, 10, 100, 30);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));
        layeredPane.add(imageLabel, Integer.valueOf(0));
        layeredPane.add(countdownLabel, Integer.valueOf(1));
        adDialog.setContentPane(layeredPane);

        String[] adImages = {"ad1.png", "ad2.png", "ad3.png"};
        ImageIcon[] adIcons = new ImageIcon[adImages.length];
        for (int i = 0; i < adImages.length; i++) {
            java.net.URL url = getClass().getClassLoader().getResource(adImages[i]);
            if (url != null) {
                Image scaled = new ImageIcon(url).getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                adIcons[i] = new ImageIcon(scaled);
            }
        }

        final int[] imageIndex = {0};
        Timer imageTimer = new Timer(1000, e -> {
            imageLabel.setIcon(adIcons[imageIndex[0]]);
            imageIndex[0] = (imageIndex[0] + 1) % adIcons.length;
        });
        imageTimer.start();

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
            case "破阵" -> eliminateBlock();
            case "摘星" -> {
                clearHighlight();
                highlightBlock();
            }
            case "风云" -> shuffleBoxes();
            case "无常" -> {
                int index = (int) (Math.random() * 3);
                handleSkill(skillNames[index]);
            }
        }
    }

    private void eliminateBlock() {
        List<BoxComponent> boxes = new ArrayList<>();
        for (BoxComponent box : new ArrayList<>(this.boxes)) {
            if (box.getType() == 1) {
                boxes.add(box);
            }
        }
        if (boxes.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "没有可以消除的方块了！");
            return;
        }

        BoxComponent removed = boxes.get((int) (Math.random() * boxes.size()));
        int row = removed.getRow();
        int col = removed.getCol();
        model.getMatrix()[row][col] = 0;

        boardPanel.remove(removed);
        this.boxes.remove(removed);
        removed.setVisible(false);
        boardPanel.revalidate();
        boardPanel.repaint();

        clearHighlight();
    }

    private void highlightBlock() {
        for (BoxComponent box : boxes) {
            if (isMovable(box)) {
                box.setHighlighted(true);
            }
        }
        new javax.swing.Timer(5000, e -> clearHighlight()).start();
    }

    private boolean isMovable(BoxComponent box) {
        int row = box.getRow();
        int col = box.getCol();
        Rectangle bounds = box.getBounds();
        int width = (int) Math.ceil(bounds.getWidth() / (float) girdSize);
        int height = (int) Math.ceil(bounds.getHeight() / (float) girdSize);

        boolean[][] occupied = new boolean[rows][columns];
        for (BoxComponent b : boxes) {
            if (b == box) continue;
            Rectangle bBounds = b.getBounds();
            int br = b.getRow();
            int bc = b.getCol();
            int bw = (int) Math.ceil(bBounds.getWidth() / (float) girdSize);
            int bh = (int) Math.ceil(bBounds.getHeight() / (float) girdSize);

            for (int i = 0; i < bh; i++) {
                for (int j = 0; j < bw; j++) {
                    int occupiedRow = br + i;
                    int occupiedCol = bc + j;
                    if (occupiedRow < 0 || occupiedRow >= rows) continue;
                    if (occupiedCol < 0 || occupiedCol >= columns) continue;
                    occupied[occupiedRow][occupiedCol] = true;
                }
            }
        }
        return canMoveTo(row - 1, col, width, height, occupied) || canMoveTo(row + 1, col, width, height, occupied) || canMoveTo(row, col - 1, width, height, occupied) || canMoveTo(row, col + 1, width, height, occupied);
    }

    private boolean canMoveTo(int r, int c, int w, int h, boolean[][] occupied) {
        if (r < 0 || r + h > rows) return false;
        if (c < 0 || c + w > columns) return false;
        for (int row = r; row < r + h; row++) {
            for (int col = c; col < c + w; col++) {
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

//    private static class BlockInfo {
//        int id;
//        int type;
//
//        BlockInfo(int id, int type) {
//            this.id = id;
//            this.type = type;
//        }
//    }

    public boolean shuffleBoxes() {
        int rows = model.getHeight();
        int cols = model.getWidth();
        int[][] oldMatrix = model.getMatrix();
        List<Integer> blockTypes = new ArrayList<>();
        boolean[][] occuiped = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!occuiped[i][j] && oldMatrix[i][j] > 0) {
                    int type = oldMatrix[i][j];
                    blockTypes.add(type);
                    markOccupied(occuiped, i, j, type);
                }
            }
        }
        Collections.shuffle(blockTypes);

        int[][] newMatrix = new int[rows][cols];

        if (isWin()) {
            // 如果打乱后不满足胜利条件，返回成功
            return false;
        }

        if (!tryPlaceBlocks(newMatrix, blockTypes, 0)) {
//            System.err.println("无法放置所有方块！");
            return false;
        }

        model.setMatrix(newMatrix);
        rebuildBoxesFromMatrix(newMatrix);
        return true;
    }

    private boolean isWin() {
        return model.getId(1, 4) == 4 && model.getId(2, 4) == 4;
    }

    private boolean tryPlaceBlocks(int[][] matrix, List<Integer> blockTypes, int index) {
        if (index >= blockTypes.size()) return true;

        int type = blockTypes.get(index);
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (canPlace(matrix, i, j, type)) {
                    placeBlock(matrix, i, j, type);
                    if (tryPlaceBlocks(matrix, blockTypes, index + 1)) {
                        return true;
                    }
                    removeBlock(matrix, i, j, type);
                }
            }
        }
        return false;
    }

    private void removeBlock(int[][] matrix, int row, int col, int type) {
        switch (type) {
            case 1:
                matrix[row][col] = 0;
                break;
            case 2:
                matrix[row][col] = 0;
                if (col + 1 < matrix[0].length) matrix[row][col + 1] = 0;
                break;
            case 3:
                matrix[row][col] = 0;
                if (row + 1 < matrix.length) matrix[row + 1][col] = 0;
                break;
            case 4:
                matrix[row][col] = 0;
                if (row + 1 < matrix.length && col + 1 < matrix[0].length) {
                    matrix[row][col + 1] = 0;
                    matrix[row + 1][col] = 0;
                    matrix[row + 1][col + 1] = 0;
                }
                break;
        }
    }

    private void markOccupied(boolean[][] occupied, int row, int col, int type) {
        occupied[row][col] = true;
        switch (type) {
            case 2 -> occupied[row][col + 1] = true;
            case 3 -> occupied[row + 1][col] = true;
            case 4 -> {
                occupied[row][col + 1] = true;
                occupied[row + 1][col] = true;
                occupied[row + 1][col + 1] = true;
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
            case 1 -> 1;
            case 2 -> {
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix[0].length; j++) {
                        if (matrix[i][j] == id) {
                            if (j + 1 < matrix[0].length && matrix[i][j + 1] == id) yield 2;
                            else yield 3;
                        }
                    }
                }
                yield 3;
            }
            case 4 -> 4;
            default -> 1;
        };
    }

    private void placeBlock(int[][] matrix, int row, int col, int type) {
        switch (type) {
            case 1:
                matrix[row][col] = type;
                break;
            case 2:
                matrix[row][col] = type;
                matrix[row][col + 1] = type;
                break;
            case 3:
                matrix[row][col] = type;
                matrix[row + 1][col] = type;
                break;
            case 4:
                matrix[row][col] = type;
                matrix[row][col + 1] = type;
                matrix[row + 1][col] = type;
                matrix[row + 1][col + 1] = type;
                break;
        }
    }

    private boolean canPlace(int[][] matrix, int row, int col, int type) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        switch (type) {
            case 1:
                return row >= 0 && row < rows && col >= 0 && col < cols && matrix[row][col] == 0;
            case 2:
                return row >= 0 && row < rows && col >= 0 && col + 1 < cols
                        && matrix[row][col] == 0 && matrix[row][col + 1] == 0;
            case 3:
                return row >= 0 && row + 1 < rows && col >= 0 && col < cols
                        && matrix[row][col] == 0 && matrix[row + 1][col] == 0;
            case 4:
                return row >= 0 && row + 1 < rows && col >= 0 && col + 1 < cols
                        && matrix[row][col] == 0 && matrix[row][col + 1] == 0
                        && matrix[row + 1][col] == 0 && matrix[row + 1][col + 1] == 0;
            default:
                return false;
        }
    }

    private void rebuildBoxesFromMatrix(int[][] matrix) {
        boxes.clear();
        boardPanel.removeAll();
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] occuiped = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int type = matrix[i][j];
                if (type != 0 && !occuiped[i][j]) {
                    BoxComponent box = new BoxComponent(type, i, j);
                    switch (type) {
                        case 1 -> box.setSize(girdSize, girdSize);
                        case 2 -> {
                            box.setSize(girdSize * 2, girdSize);
                            occuiped[i][j + 1] = true;
                        }
                        case 3 -> {
                            box.setSize(girdSize, girdSize * 2);
                            occuiped[i + 1][j] = true;
                        }
                        case 4 -> {
                            box.setSize(girdSize * 2, girdSize * 2);
                            occuiped[i][j + 1] = true;
                            occuiped[i + 1][j] = true;
                            occuiped[i + 1][j + 1] = true;
                        }
                    }
                    box.setLocation(j * girdSize, i * girdSize);
                    boxes.add(box);
                    boardPanel.add(box);
                    occuiped[i][j] = true;
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
//                System.err.println("找不到图标: " + path);
                return null;
            }
        } catch (Exception e) {
//            System.err.println("图标加载失败: " + path);
            return null;
        }
    }

    private JPanel wrapWithPanel(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }

    public JLabel createStyledLabel(String text, String fontName, int fontStyle, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(fontName, fontStyle, fontSize));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    public void updateTimeLabel() {
        elapsedTime++;
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";
        timeLabel.setText(String.format("%s%02d:%02d", prefix, minutes, seconds));
        if (elapsedTime % 30 == 29 &&! this.controller.isVisitor()) {
            this.controller.saveGame("./"+this.controller.getUser()+".txt");
        }
    }

    protected void resetEffects() {
    }

    public void initialGame() {
        resetEffects();
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
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
                        box.setSize(girdSize, girdSize);
                    }
                    case 2 -> {
                        box = new BoxComponent(2, i, j);
                        box.setSize(girdSize * 2, girdSize);
                        map[i][j + 1] = 0;
                    }
                    case 3 -> {
                        box = new BoxComponent(3, i, j);
                        box.setSize(girdSize, girdSize * 2);
                        map[i + 1][j] = 0;
                    }
                    case 4 -> {
                        box = new BoxComponent(4, i, j);
                        box.setSize(girdSize * 2, girdSize * 2);
                        map[i + 1][j] = map[i][j + 1] = map[i + 1][j + 1] = 0;
                    }
                }
                if (box != null) {
                    box.setLocation(j * girdSize, i * girdSize);
                    boxes.add(box);
                    boardPanel.add(box);
                }
            }
        }
        this.repaint();

        revalidate();
        repaint();

        //鼠标拖拽（未完成）
        boardPanel.addMouseMotionListener(new MouseAdapter() {
            private Point startPoint;
            private BoxComponent activeBox;

            @Override
            public void mousePressed(MouseEvent e) {
                activeBox = (BoxComponent) boardPanel.getComponentAt(e.getPoint());
                if (activeBox != null) {
                    activeBox.setSelected(true);
                    startPoint = e.getPoint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (activeBox == null || startPoint == null) return;

                int dx = e.getX() - startPoint.x;
                int dy = e.getY() - startPoint.y;

                activeBox.setLocationSliding(
                        activeBox.getX() + dx,
                        activeBox.getY() + dy
                );
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (activeBox == null) return;

                Point endPoint = e.getPoint();
                int newRow = activeBox.getRow() + (endPoint.y - startPoint.y) / girdSize;
                int newCol = activeBox.getCol() + (endPoint.x - startPoint.x) / girdSize;

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

        timer = new Timer(1000, e -> updateTimeLabel());
        timer.start();
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
//        System.out.println("Click VK_RIGHT");
        if (selectedBox != null) {
            if (controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.RIGHT)) {
                afterMove();
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
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
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

    public int getGirdSize() {
        return girdSize;
    }

    public void loadGame(String path) {
        controller.loadGame(path);
    }

    public void saveGame(String path) {
        controller.saveGame(path);
    }

    public void resetSkills() {
        for (int i = 0; i < skillUsed.length; i++) {
            skillUsed[i] = false;
        }
        for (JLabel label : centerLabels) {
            if (label != null) {
                label.setText("1");
            }
        }
    }

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
        stepLabel.setText((currentLanguage == Language.CHINESE ? "步数：" : "Steps: ") + steps);
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
        String prefix = currentLanguage == Language.CHINESE ? "时间: " : "Time: ";
        timeLabel.setText(String.format("%s%02d:%02d", prefix, elapsedTime/60, elapsedTime % 60));
    }

    public void setCurrentLanguage(Language currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
