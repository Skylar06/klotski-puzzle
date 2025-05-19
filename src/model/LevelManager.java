package model;

import java.util.Arrays;

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
                    {1, 2, 2, 2, 2},
                    {3, 4, 4, 1, 0},
                    {3, 4, 4, 1, 0},
                    {1, 2, 2, 2, 2}
            },
            {
                    {3,0,3,1,1},
                    {3,0,3,1,1},
                    {3,2,2,4,4},
                    {3,2,2,4,4}
            },
            {
                    {2,2,0,4,4},
                    {2,2,0,4,4},
                    {1,2,2,3,1},
                    {1,2,2,3,1}
            },
            {
                    {3,4,4,1,1},
                    {3,4,4,1,0},
                    {3,1,1,1,0},
                    {3,2,2,2,2}
            },
            {
                    {2,2,0,0,1},
                    {2,2,4,4,1},
                    {3,3,4,4,3},
                    {3,3,1,1,3}
            },
            {
                    {2,2,4,4,1},
                    {0,0,4,4,1},
                    {2,2,2,2,1},
                    {2,2,2,2,2}
            },
            {
                    {4,4,3,3,1},
                    {4,4,3,3,1},
                    {0,3,3,3,1},
                    {0,3,3,3,1}
            },
            {
                    {1,1,3,4,4},
                    {1,1,3,4,4},
                    {0,2,2,2,2},
                    {0,2,2,2,2}
            }
    };

    public static int[][] getCurrentMap() {
        int[][] tempMap = new int[maps[0].length][maps[0][0].length];
        for (int i = 0; i < maps[currentLevel].length; i++) {
            tempMap[i] = Arrays.copyOf(maps[currentLevel][i], maps[currentLevel][i].length);
        }
        return tempMap;
    }

    public static void nextLevel() {
        currentLevel = (currentLevel + 1) % maps.length;
    }

    public static int getCurrentLevelIndex() {
        return currentLevel;
    }
}
