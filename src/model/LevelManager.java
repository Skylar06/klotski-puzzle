package model;

public class LevelManager {
    private static int currentLevel = 0;
    private static final int[][][] maps = {
            {
                    {2, 2, 1, 4, 4},
                    {2, 2, 1, 4, 4},
                    {2, 2, 3, 0, 0},
                    {2, 2, 3, 1, 1}
            },
            {
                    {2, 2, 1, 4, 4},
                    {1, 1, 1, 4, 4},
                    {2, 2, 3, 0, 0},
                    {2, 2, 3, 0, 1}
            },
            // 可以继续添加更多地图
    };

    public static MapModel getCurrentMap() {
        return new MapModel(maps[currentLevel]);
    }

    public static void nextLevel() {
        currentLevel = (currentLevel + 1) % maps.length;
    }

    public static int getCurrentLevelIndex() {
        return currentLevel;
    }
}
