package controller;

import model.Direction;
import model.MapModel;
import view.game.BoxComponent;
import view.game.GamePanel;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */
public class GameController {
    private final GamePanel view;// 持有游戏界面引用
    private final MapModel model;// 持有数据模型引用

    public GameController(GamePanel view, MapModel model) {
        this.view = view;
        this.model = model;
        view.setController(this);// 将控制器反向注入视图
    }

    public void restartGame() {
        System.out.println("Do restart game here");
    }// 待实现具体逻辑

    public boolean doMove(int row, int col, Direction direction) {
        if (model.getId(row, col) == 1) {// 检查当前格子是否有可移动方块
            int nextRow = row + direction.getRow();// 计算目标位置
            int nextCol = col + direction.getCol();
            if (model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol)) {// 边界检查
                if (model.getId(nextRow, nextCol) == 0) {// 边界检查
                    // 更新模型数据
                    model.getMatrix()[row][col] = 0;
                    model.getMatrix()[nextRow][nextCol] = 1;
                    // 更新视图
                    BoxComponent box = view.getSelectedBox();
                    box.setRow(nextRow);
                    box.setCol(nextCol);
                    box.setLocation(box.getCol() * view.getGRID_SIZE() + 2, box.getRow() * view.getGRID_SIZE() + 2);// 计算新坐标
                    box.repaint();// 重新画出移动后的格子
                    return true;
                }
            }
        }
        return false;
    }

    //todo: add other methods such as loadGame, saveGame...

}
