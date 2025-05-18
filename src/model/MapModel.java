package model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class is to record the map of one game. For example:
 */
public class MapModel implements Serializable {
    int[][] matrix;// 二维数组存储地图数据
    public MapModel(int[][] matrix) {
        this.matrix = matrix;
    }

    public int getWidth() {
        return this.matrix[0].length;
    }

    public int getHeight() {
        return this.matrix.length;
    }

    public int getId(int row, int col) {
        return matrix[row][col];
    } // 获取指定位置的值

    public int[][] getMatrix() {
        return matrix;
    }

    public boolean checkInWidthSize(int col) {
        return col >= 0 && col < matrix[0].length;
    }// 检查列是否越界

    public boolean checkInHeightSize(int row) {
        return row >= 0 && row < matrix.length;
    }

    public void setMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            this.matrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
    }
}
