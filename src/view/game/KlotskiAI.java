package view.game;

import model.Direction;

import java.util.*;

public class KlotskiAI {
    // 华容道的目标状态条件
    private static final int TARGET_ROW1 = 1;
    private static final int TARGET_ROW2 = 2;
    private static final int TARGET_COL1 = 3;
    private static final int TARGET_COL2 = 4;

    // 棋盘的大小
    private static final int BOARD_ROWS = 4;
    private static final int BOARD_COLS = 5;

    // 方向常量
    private static final int[][] DIRECTIONS = {
            {-1, 0}, // 上
            {1, 0},  // 下
            {0, -1}, // 左
            {0, 1}   // 右
    };
    public static String as(int[][] board){
        List<Move> solution = solveKlotski(board);
        return solution.get(0).toString();
    }
    public static void main(String[] args) {
        // 初始棋盘状态
        int[][] initialBoard = {
                {1,1,3,4,4},
                {1,1,3,4,4},
                {0,2,2,2,2},
                {0,2,2,2,2}
        };

        // 执行广度优先搜索
        List<Move> solution = solveKlotski(initialBoard);

        if (solution != null) {
            System.out.println("找到解决方案：");
            for (Move move : solution) {
                System.out.println(move);
            }
        } else {
            System.out.println("没有找到解决方案。");
        }
    }

    // 使用广度优先搜索解决华容道
    public static List<Move> solveKlotski(int[][] board) {
        Queue<State> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // 初始状态没有父状态，路径为空
        State initialState = new State(board, null);
        queue.add(initialState);
        visited.add(hashBoard(board));

        while (!queue.isEmpty()) {
            State currentState = queue.poll();

            // 检查是否达到目标状态
            if (isTargetState(currentState.getBoard())) {
                return currentState.getPath(); // 返回解决方案路径
            }

            // 生成所有可能的移动
            List<State> nextStates = generateNextStates(currentState.getBoard());

            for (State nextState : nextStates) {
                String nextHash = hashBoard(nextState.getBoard());
                if (!visited.contains(nextHash)) {
                    nextState.setParent(currentState);
                    queue.add(nextState);
                    visited.add(nextHash);
                }
            }
        }

        return null; // 没有找到解决方案
    }

    // 检查棋盘是否处于目标状态
    private static boolean isTargetState(int[][] board) {
        return board[TARGET_ROW1][TARGET_COL1] == 4 &&
                board[TARGET_ROW1][TARGET_COL2] == 4 &&
                board[TARGET_ROW2][TARGET_COL1] == 4 &&
                board[TARGET_ROW2][TARGET_COL2] == 4;
    }

    // 生成所有可能的移动
    private static List<State> generateNextStates(int[][] board) {
        List<State> nextStates = new ArrayList<>();

        // 找到可以移动的块
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                if (board[i][j] != 0) {
                    int blockId = board[i][j];
                    for (int[] dir : DIRECTIONS) {
                        int newX = i + dir[0];
                        int newY = j + dir[1];

                        // 根据块的类型进行不同的移动逻辑
                        if (blockId == 1) { // 1x1的块
                            if (newX >= 0 && newX < BOARD_ROWS && newY >= 0 && newY < BOARD_COLS && board[newX][newY] == 0) {
                                int[][] newBoard = copyBoard(board);
                                newBoard[i][j] = 0;
                                newBoard[newX][newY] = 1;
                                Move move = new Move(i, j, newX, newY);
                                nextStates.add(new State(newBoard, move));
                            }
                        } else if (blockId == 2) { // 1x2的块
                            if (j + 1 < BOARD_COLS && board[i][j + 1] == 2) {
                                boolean temp = false;
                                if (dir[1] == -1) { // 向左移动
                                    if (newY >= 0 && newY < BOARD_COLS && board[newX][newY] == 0) {
                                        temp = true;
                                    }
                                } else if (dir[1] == 1) { // 向右移动
                                    if (newY + 1 < BOARD_COLS && board[newX][newY + 1] == 0) {
                                        temp = true;
                                    }
                                } else if (dir[0] == -1) { // 向上移动
                                    if (newX >= 0 && newX < BOARD_ROWS && board[newX][newY] == 0 && board[newX][newY + 1] == 0) {
                                        temp = true;
                                    }
                                } else if (dir[0] == 1) { // 向下移动
                                    if (newX + 1 < BOARD_ROWS && board[newX][newY] == 0 && board[newX + 1][newY] == 0) {
                                        temp = true;
                                    }
                                }
                                if (temp) {
                                    int[][] newBoard = copyBoard(board);
                                    newBoard[i][j] = 0;
                                    newBoard[i][j + 1] = 0;
                                    newBoard[newX][newY] = 2;
                                    newBoard[newX][newY + 1] = 2;
                                    Move move = new Move(i, j, newX, newY);
                                    nextStates.add(new State(newBoard, move));
                                }
                            }
                        } else if (blockId == 3) { // 2x1的块
                            if (i + 1 < BOARD_ROWS && board[i + 1][j] == 3) {
                                if (newX >= 0 && newX + 1 < BOARD_ROWS && newY >= 0 && newY < BOARD_COLS && (board[newX][newY] == 0 || board[newX][newY] == 3) && (board[newX + 1][newY] == 0 || board[newX + 1][newY] == 3)) {
                                    int[][] newBoard = copyBoard(board);
                                    newBoard[i][j] = 0;
                                    newBoard[i + 1][j] = 0;
                                    newBoard[newX][newY] = 3;
                                    newBoard[newX + 1][newY] = 3;
                                    Move move = new Move(i, j, newX, newY);
                                    nextStates.add(new State(newBoard, move));
                                }
                            }
                        } else if (blockId == 4) { // 2x2的块
                            if (i + 1 < BOARD_ROWS && j + 1 < BOARD_COLS &&
                                    board[i][j + 1] == 4 && board[i + 1][j] == 4 && board[i + 1][j + 1] == 4) {
                                if (newX >= 0 && newX < BOARD_ROWS - 1 && newY >= 0 && newY < BOARD_COLS - 1 &&
                                        (board[newX][newY] == 0 || board[newX][newY] == 4) &&
                                        (board[newX + 1][newY] == 0 || board[newX + 1][newY] == 4) &&
                                        (board[newX][newY + 1] == 0 || board[newX][newY + 1] == 4) &&
                                        (board[newX + 1][newY + 1] == 0 || board[newX + 1][newY + 1] == 4)) {
                                    int[][] newBoard = copyBoard(board);
                                    newBoard[i][j] = 0;
                                    newBoard[i + 1][j] = 0;
                                    newBoard[i][j + 1] = 0;
                                    newBoard[i + 1][j + 1] = 0;
                                    newBoard[newX][newY] = 4;
                                    newBoard[newX + 1][newY] = 4;
                                    newBoard[newX][newY + 1] = 4;
                                    newBoard[newX + 1][newY + 1] = 4;
                                    Move move = new Move(i, j, newX, newY);
                                    nextStates.add(new State(newBoard, move));
                                }
                            }
                        }
                    }
                }
            }
        }

        return nextStates;
    }

    // 将棋盘转换为字符串哈希
    private static String hashBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    // 复制棋盘
    private static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[BOARD_ROWS][BOARD_COLS];
        for (int i = 0; i < BOARD_ROWS; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, BOARD_COLS);
        }
        return newBoard;
    }

    // 状态类，表示棋盘的一个状态
    private static class State {
        private int[][] board;
        private State parent;
        private Move move; // 记录当前状态对应的移动

        public State(int[][] board, Move move) {
            this.board = board;
            this.move = move;
            this.parent = null;
        }

        public State(int[][] board, State parent, Move move) {
            this.board = board;
            this.parent = parent;
            this.move = move;
        }

        public int[][] getBoard() {
            return board;
        }

        public List<Move> getPath() {
            List<Move> path = new ArrayList<>();
            State currentState = this;
            while (currentState != null && currentState.move != null) {
                path.add(0, currentState.move); // 将移动步骤添加到路径的前面
                currentState = currentState.parent;
            }
            return path;
        }

        public void setParent(State parent) {
            this.parent = parent;
        }

        public Move getMove() {
            return move;
        }
    }

    // 移动类
    public static class Move {
        int fromX;
        int fromY;
        int toX;
        int toY;

        public Move(int fromX, int fromY, int toX, int toY) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        @Override
        public String toString() {
            return String.format("从 (%d, %d) 移动到 (%d, %d)", fromX, fromY, toX, toY);
        }
    }
}