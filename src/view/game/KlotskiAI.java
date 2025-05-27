package view.game;

import model.Direction;

import java.util.*;

public class KlotskiAI {
    //目标
    private static final int TARGET_ROW1 = 1;
    private static final int TARGET_ROW2 = 2;
    private static final int TARGET_COL1 = 3;
    private static final int TARGET_COL2 = 4;
    private static final int BOARD_ROWS = 4;
    private static final int BOARD_COLS = 5;
    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };
    public static String as(int[][] board){
        List<Move> solution = solveKlotski(board);
        return solution.get(0).toString();
    }
    public static void main(String[] args) {
        int[][] initialBoard = {
                {1,1,3,4,4},
                {1,1,3,4,4},
                {0,2,2,2,2},
                {0,2,2,2,2}
        };

        //广度优先搜索
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

    public static List<Move> solveKlotski(int[][] board) {
        Queue<State> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        State initialState = new State(board, null);
        queue.add(initialState);
        visited.add(hashBoard(board));

        while (!queue.isEmpty()) {
            State currentState = queue.poll();

            //检查
            if (isTargetState(currentState.getBoard())) {
                return currentState.getPath();
            }

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

        return null;
    }

    private static boolean isTargetState(int[][] board) {
        return board[TARGET_ROW1][TARGET_COL1] == 4 &&
                board[TARGET_ROW1][TARGET_COL2] == 4 &&
                board[TARGET_ROW2][TARGET_COL1] == 4 &&
                board[TARGET_ROW2][TARGET_COL2] == 4;
    }

    private static List<State> generateNextStates(int[][] board) {
        List<State> nextStates = new ArrayList<>();

        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                if (board[i][j] != 0) {
                    int blockId = board[i][j];
                    for (int[] dir : DIRECTIONS) {
                        int newX = i + dir[0];
                        int newY = j + dir[1];


                        if (blockId == 1) {
                            if (newX >= 0 && newX < BOARD_ROWS && newY >= 0 && newY < BOARD_COLS && board[newX][newY] == 0) {
                                int[][] newBoard = copyBoard(board);
                                newBoard[i][j] = 0;
                                newBoard[newX][newY] = 1;
                                Move move = new Move(i, j, newX, newY);
                                nextStates.add(new State(newBoard, move));
                            }
                        } else if (blockId == 2) {
                            if (j + 1 < BOARD_COLS && board[i][j + 1] == 2) {
                                boolean temp = false;
                                if (dir[1] == -1) {
                                    if (newY >= 0 && newY < BOARD_COLS && board[newX][newY] == 0) {
                                        temp = true;
                                    }
                                } else if (dir[1] == 1) {
                                    if (newY + 1 < BOARD_COLS && board[newX][newY + 1] == 0) {
                                        temp = true;
                                    }
                                } else if (dir[0] == -1) {
                                    if (newX >= 0 && newX < BOARD_ROWS && board[newX][newY] == 0 && board[newX][newY + 1] == 0) {
                                        temp = true;
                                    }
                                } else if (dir[0] == 1) {
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
                        } else if (blockId == 3) {
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
                        } else if (blockId == 4) {
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

    private static String hashBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[BOARD_ROWS][BOARD_COLS];
        for (int i = 0; i < BOARD_ROWS; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, BOARD_COLS);
        }
        return newBoard;
    }

    private static class State {
        private int[][] board;
        private State parent;
        private Move move;

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
                path.add(0, currentState.move);
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