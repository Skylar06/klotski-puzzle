package controller;

import model.Direction;
import model.MapModel;
import view.game.BoxComponent;
import view.game.GameFrame1;
import view.game.GamePanel;
import view.level.select.LevelSelectFrame;
import view.login.LoginFrame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */
public class GameController {
    public LevelSelectFrame levelSelectFrame;
    public LoginFrame loginFrame;
    public GameFrame1 gameFrame1;// 持有游戏界面引用
    private GamePanel view;
    private MapModel model;// 持有数据模型引用
    private String user;
    private int mode;
    private boolean mirrorMode = false; // 是否为镜像模式

    public GameController(MapModel model, LevelSelectFrame levelSelectFrame, LoginFrame loginFrame) {
        this.model = model;
        this.loginFrame = loginFrame;
        this.levelSelectFrame = levelSelectFrame;
        this.levelSelectFrame.setGameController(this);
        this.loginFrame.setGameController(this);
        this.loginFrame.setLevelSelectFrame(this.levelSelectFrame);
    }

    public void setMirrorMode(boolean mirrorMode) {
        this.mirrorMode = mirrorMode;
    }

    public void restartGame() {
        this.view.setVisible(true);
        // 重置模型
        model.setMatrix(new int[][]{
                {2, 2, 2, 2, 1},
                {1, 3, 2, 2, 0},
                {1, 3, 4, 4, 1},
                {2, 2, 4, 4, 0}
        }); // 这里替换成初始棋盘数据

        // 清空移动历史
        moveHistory.clear();
        view.getCurrentPanel().repaint();
        // 重置视图
        view.initialGame(); // 调用视图的初始化方法
        view.setSteps(0); // 重置步数
        view.getStepLabel().setText(String.format("Step: %d", view.getSteps()));
    }

    public boolean doMove(int row, int col, Direction direction) {
        if (mirrorMode) {
            if (direction == Direction.LEFT) {
                direction = Direction.RIGHT;
            } else if (direction == Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (direction == Direction.UP) {
                direction = Direction.DOWN;
            } else if (direction == Direction.DOWN) {
                direction = Direction.UP;
            }
        }

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
                    box.setLocation(box.getCol() * view.getGRID_SIZE(), box.getRow() * view.getGRID_SIZE());// 计算新坐标
                    box.repaint();// 重新画出移动后的格子
                    return true;
                }
            }
        } else if (model.getId(row, col) == 2) {
            int nextRow = row + direction.getRow();// 计算目标位置
            int nextCol = col + direction.getCol();
            if (direction == Direction.RIGHT){
                nextCol++;
            }
            if (model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol)) {// 边界检查
                if (model.getId(nextRow, nextCol) == 0) {// 边界检查
                    // 更新模型数据
                    if (direction == Direction.RIGHT) nextCol--;
                    model.getMatrix()[row][col] = 0;
                    model.getMatrix()[row][col + 1] = 0;
                    model.getMatrix()[nextRow][nextCol] = 2;
                    model.getMatrix()[nextRow][nextCol + 1] = 2;
                    // 更新视图
                    BoxComponent box = view.getSelectedBox();
                    box.setRow(nextRow);
                    box.setCol(nextCol);
                    box.setLocation(box.getCol() * view.getGRID_SIZE(), box.getRow() * view.getGRID_SIZE());// 计算新坐标
                    box.repaint();// 重新画出移动后的格子
                    return true;
                }
            }
        } else if (model.getId(row, col) == 3) {
            int nextRow = row + direction.getRow();// 计算目标位置
            int nextCol = col + direction.getCol();
            if (direction == Direction.DOWN) {
                nextRow++;
            }
            if (model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol)) {// 边界检查
                if (model.getId(nextRow, nextCol) == 0) {// 边界检查
                    // 更新模型数据
                    if (direction == Direction.DOWN) nextRow--;
                    model.getMatrix()[row][col] = 0;
                    model.getMatrix()[row + 1][col] = 0;
                    model.getMatrix()[nextRow][nextCol] = 3;
                    model.getMatrix()[nextRow + 1][nextCol] = 3;
                    // 更新视图
                    BoxComponent box = view.getSelectedBox();
                    box.setRow(nextRow);
                    box.setCol(nextCol);
                    box.setLocation(box.getCol() * view.getGRID_SIZE(), box.getRow() * view.getGRID_SIZE());// 计算新坐标
                    box.repaint();// 重新画出移动后的格子
                    return true;
                }
            }
        } else if (model.getId(row, col) == 4) {
            int nextRow = row + direction.getRow();// 计算目标位置
            int nextCol = col + direction.getCol();
            if (model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol)
                    && model.checkInHeightSize(nextRow + 1) && model.checkInWidthSize(nextCol)
                    && model.checkInHeightSize(nextRow) && model.checkInWidthSize(nextCol + 1)
                    && model.checkInHeightSize(nextRow + 1) && model.checkInWidthSize(nextCol + 1)) {// 边界检查
                if ((model.getId(nextRow, nextCol) == 0 || model.getId(nextRow, nextCol) == 4)
                        && (model.getId(nextRow + 1, nextCol) == 0 || model.getId(nextRow + 1, nextCol) == 4)
                        && (model.getId(nextRow, nextCol + 1) == 0 || model.getId(nextRow, nextCol + 1) == 4)
                        && (model.getId(nextRow + 1, nextCol + 1) == 0 || model.getId(nextRow + 1, nextCol + 1) == 4)) {// 边界检查
                    // 更新模型数据
                    model.getMatrix()[row][col] = 0;
                    model.getMatrix()[row + 1][col] = 0;
                    model.getMatrix()[row][col + 1] = 0;
                    model.getMatrix()[row + 1][col + 1] = 0;
                    model.getMatrix()[nextRow][nextCol] = 4;
                    model.getMatrix()[nextRow + 1][nextCol] = 4;
                    model.getMatrix()[nextRow][nextCol + 1] = 4;
                    model.getMatrix()[nextRow + 1][nextCol + 1] = 4;
                    // 更新视图
                    BoxComponent box = view.getSelectedBox();
                    box.setRow(nextRow);
                    box.setCol(nextCol);
                    box.setLocation(box.getCol() * view.getGRID_SIZE(), box.getRow() * view.getGRID_SIZE());// 计算新坐标
                    box.repaint();// 重新画出移动后的格子
                    return true;
                }
            }
        }
        return false;
    }
    //todo: add other methods such as loadGame, saveGame...

    private List<Move> moveHistory = new ArrayList<>();

    public void recordMove(Move move) {
        moveHistory.add(move);
    }

    public void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.remove(moveHistory.size() - 1);
            model.getMatrix()[lastMove.getFromRow()][lastMove.getFromCol()] = 1;
            model.getMatrix()[lastMove.getToRow()][lastMove.getToCol()] = 0;
            BoxComponent box = view.getSelectedBox();
            box.setRow(lastMove.getFromRow());
            box.setCol(lastMove.getFromCol());
            box.setLocation(lastMove.getFromCol() * view.getGRID_SIZE() + 2, lastMove.getFromRow() * view.getGRID_SIZE() + 2);
            box.repaint();
            view.setSteps(view.getSteps() - 1);
            view.getStepLabel().setText(String.format("Step: %d", view.getSteps()));
        }
    }

    public static class Move {
        private int fromRow;
        private int fromCol;
        private int toRow;
        private int toCol;

        public Move(int fromRow, int fromCol, int toRow, int toCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
        }

        public int getFromRow() {
            return fromRow;
        }

        public int getFromCol() {
            return fromCol;
        }

        public int getToRow() {
            return toRow;
        }

        public int getToCol() {
            return toCol;
        }
    }

    public void loadGame(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            Save temp = (Save) in.readObject();
            // 加载游戏状态
            int[][] savedMatrix = temp.model.getMatrix();
            model.setMatrix(savedMatrix);
            view.setSteps(temp.step);
            this.mode = temp.mode;
            this.user = temp.user;
            view.getStepLabel().setText(String.format("Step: %d", view.getSteps()));
            view.initialGame(); // 重新初始化游戏界面
            view.getCurrentPanel().setElapsedTime(temp.time);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setView(GamePanel view) {
        this.view = view;
    }

    public void saveGame(String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            // 保存游戏状态，包括地图和步数
            Save newSave = new Save(model,mode,user,this.view.getCurrentPanel().getElapsedTime(), view.getSteps());
            out.writeObject(newSave);
        } catch (IOException e) {
            throw new RuntimeException("保存游戏失败", e);
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void pauseTimer() {
        this.view.getCurrentPanel().pauseTimer();
        this.view.setVisible(false);
    }

    public void restartTimer() {
        this.view.getCurrentPanel().restartTimer();
        this.view.setVisible(true);
    }

    public void returnToMenu() {
        this.levelSelectFrame.setVisible(true);
        this.gameFrame1.setVisible(false);
    }
}
class Save implements Serializable{
    public MapModel model;
    public int mode;
    public String user;
    public int time;
    public int step;
    public Save(MapModel model, int mode, String user, int time, int step) {
        this.model = model;
        this.user = user;
        this.mode = mode;
        this.time = time;
        this.step = step;
    }
}