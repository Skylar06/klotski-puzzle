package view.game;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BoxComponent extends JComponent {
    private int row;
    private int col;// 在网格中的行列位置
    private boolean isSelected;// 选中状态
    private int type; // 类型：1-4，用于判断是哪种方块（如横2、竖2、大方块等）
    private static String currentSkin = "classic"; // 当前皮肤，默认是古风

    private static final int BORDER_WIDTH_SELECTED = 3;
    private static final int BORDER_WIDTH_DEFAULT = 1;

    public BoxComponent(int type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.isSelected = false;
        setOpaque(false); // 让背景透明，便于绘图
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 加载皮肤图片
        Image skinImage = SkinManager.getBoxImage(currentSkin, type);
        if (skinImage != null) {
            g.drawImage(skinImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 如果没图片，就用默认色块
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 设置边框
        Border border = isSelected ?
                BorderFactory.createLineBorder(Color.RED, BORDER_WIDTH_SELECTED) :
                BorderFactory.createLineBorder(Color.DARK_GRAY, BORDER_WIDTH_DEFAULT);
        this.setBorder(border);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        this.repaint();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getType() {
        return type;
    }

    public static void setCurrentSkin(String skinName) {
        currentSkin = skinName;
    }

    public static String getCurrentSkin() {
        return currentSkin;
    }
}
