package model;

import java.util.List;

public class LevelRepository {
    private static final List<int[][]> MAP_LIST = List.of(
            new int[][]{
                    {2, 2, 1, 4, 4},
                    {2, 2, 1, 4, 4},
                    {2, 2, 3, 0, 0},
                    {2, 2, 3, 1, 1}
            },
            new int[][]{
                    {2, 2, 1, 4, 4},
                    {2, 2, 1, 4, 4},
                    {2, 2, 3, 0, 0},
                    {2, 2, 3, 1, 0}
            }
            // ... 更多地图
    );

    private static int currentIndex = 0;

    public static MapModel getCurrentMapModel() {
        return new MapModel(MAP_LIST.get(currentIndex));
    }

    public static boolean hasNextLevel() {
        return currentIndex < MAP_LIST.size() - 1;
    }

    public static void nextLevel() {
        if (hasNextLevel()) {
            currentIndex++;
        }
    }

    public static int getCurrentLevelNumber() {
        return currentIndex + 1;
    }

    public static void reset() {
        currentIndex = 0;
    }
}
