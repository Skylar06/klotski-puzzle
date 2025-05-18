package model;

public class LevelManager {
    private static int currentLevel = 0;
    private static final int[][][] maps = {
            {
                    {2, 2, 4, 4, 1},
                    {2, 2, 4, 4, 1},
                    {0, 0, 2, 2, 3},
                    {2, 2, 1, 1, 3}
            },
            {
                    {2, 2, 1, 1, 1},
                    {2, 2, 1, 0, 0},
                    {2, 2, 3, 4, 4},
                    {2, 2, 3, 4, 4}
            }
    };

    public static int[][] getCurrentMap() {
        return maps[currentLevel];
    }

    public static void nextLevel() {
        currentLevel = (currentLevel + 1) % maps.length;
    }

    public static int getCurrentLevelIndex() {
        return currentLevel;
    }
}
