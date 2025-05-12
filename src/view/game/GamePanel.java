package view.game;

import controller.GameController;
import model.Direction;
import model.MapModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * It is the subclass of ListenerPanel, so that it should implement those four methods: do move left, up, down ,right.
 * The class contains a grids, which is the corresponding GUI view of the matrix variable in MapMatrix.
 */
public class GamePanel extends ListenerPanel {
    private List<BoxComponent> boxes;// 所有方块的集合
    private MapModel model;// 地图数据模型
    private GameController controller;// 游戏控制器
    public JLabel stepLabel;// 步数标签
    public int steps;// 步数
    private final int GRID_SIZE = 50;// 网格单元尺寸：每个游戏网格的宽度和高度均为50像素（类似棋盘格的尺寸）
    private BoxComponent selectedBox;// 当前选中的方块


    public GamePanel(MapModel model) {
        boxes = new ArrayList<>();
        this.setVisible(true); // 立即显示面板
        this.setFocusable(true);// 允许接收键盘事件
        this.setLayout(null);// 绝对定位布局
        this.setSize(model.getWidth() * GRID_SIZE + 4, model.getHeight() * GRID_SIZE + 4);// 计算画布尺寸
        this.model = model;
        this.selectedBox = null;
        initialGame(); // 初始化游戏元素
    }

    /*
                        {1, 2, 2, 1, 1},
                        {3, 4, 4, 2, 2},
                        {3, 4, 4, 1, 0},
                        {1, 2, 2, 1, 0},
                        {1, 1, 1, 1, 1}
     */
    public void initialGame() {
        this.steps = 0;
        //copy a map 拷贝地图数据
        int[][] map = new int[model.getHeight()][model.getWidth()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = model.getId(i, j);
            }
        }
        //build Component
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {// 第一个被读取的一定是左上角的
                BoxComponent box = null;
                if (map[i][j] == 1) {// 1x1 橙色方块
                    box = new BoxComponent(Color.ORANGE, i, j);
                    box.setSize(GRID_SIZE, GRID_SIZE);
                    map[i][j] = 0;
                } else if (map[i][j] == 2) {// 2x1 粉色横向方块
                    box = new BoxComponent(Color.PINK, i, j);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE);
                    map[i][j] = 0;
                    map[i][j + 1] = 0;// 标记右侧格子为已占用
                } else if (map[i][j] == 3) {// 1x2 蓝色纵向方块
                    box = new BoxComponent(Color.BLUE, i, j);
                    box.setSize(GRID_SIZE, GRID_SIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;// 标记下方格子为已占用
                } else if (map[i][j] == 4) {// 2x2 绿色大方块
                    box = new BoxComponent(Color.GREEN, i, j);
                    box.setSize(GRID_SIZE * 2, GRID_SIZE * 2);
                    map[i][j] = 0;
                    map[i + 1][j] = 0;
                    map[i][j + 1] = 0;
                    map[i + 1][j + 1] = 0;
                }
                if (box != null) {
                    box.setLocation(j * GRID_SIZE + 2, i * GRID_SIZE + 2);// 定位+2像素边距 让方块之间有间隙（避免紧密贴合）
                    boxes.add(box);
                    this.add(box);
                }
            }
        }
        this.repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());// 填充背景
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
        this.setBorder(border);// 设置面板边框
    }

    @Override
    public void doMouseClick(Point point) {
        Component component = this.getComponentAt(point);
        if (component instanceof BoxComponent clickedComponent) {
            if (selectedBox == null) {// 首次选中
                selectedBox = clickedComponent;
                selectedBox.setSelected(true);// 高亮当前方块
            } else if (selectedBox != clickedComponent) {// 切换选中
                selectedBox.setSelected(false); // 取消旧选中
                clickedComponent.setSelected(true);// 高亮新方块
                selectedBox = clickedComponent;
            } else {// 取消选中
                clickedComponent.setSelected(false);// 取消高亮
                selectedBox = null;// 取消高亮
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
        System.out.println("Click VK_LEFT");
        if (selectedBox != null) {
            if (controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.LEFT)) {
                afterMove();
            }
        }
    }

    @Override
    public void doMoveUp() {
        System.out.println("Click VK_Up");
        if (selectedBox != null) {
            if (controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.UP)) {
                afterMove();
            }
        }
    }

    @Override
    public void doMoveDown() {
        System.out.println("Click VK_DOWN");
        if (selectedBox != null) {
            if (controller.doMove(selectedBox.getRow(), selectedBox.getCol(), Direction.DOWN)) {
                afterMove();
            }
        }
    }

    public void afterMove() {
        this.steps++;
        this.stepLabel.setText(String.format("Step: %d", this.steps));// 每次有效移动后，外部标签显示递增的步数
        checkWinCondition();
    }
    public void undoMove() {
        controller.undoLastMove();
    }
    private void checkWinCondition() {
        // 假设出口位置在底部中间，具体位置根据实际地图调整
        int exitRow = model.getHeight() - 1;
        int exitCol = model.getWidth() / 2;

        // 检查"曹操"块（假设为2x2的绿色块，值为4）是否到达出口位置
        if (model.getId(exitRow, exitCol) == 4 && model.getId(exitRow, exitCol + 1) == 4) {
            JOptionPane.showMessageDialog(this, "恭喜你！成功将曹操移到了出口！");
            // 可以在这里添加其他胜利后的逻辑，比如记录成绩等
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
    public void saveGame(String path){
        controller.saveGame(path);
    }
}


