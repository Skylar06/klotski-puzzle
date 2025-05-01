package view.game;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BoxComponent extends JComponent {
    private Color color;// 方块颜色
    private int row;
    private int col;// 在网格中的行列位置
    private boolean isSelected;// 选中状态


    public BoxComponent(Color color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        isSelected = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);// 调用父类绘制（清空背景）
        // 绘制纯色背景
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
        // 根据选中状态设置边框
        Border border;
        if (isSelected) {
            border = BorderFactory.createLineBorder(Color.red, 3);// 选中时红色粗边框
        } else {
            border = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);// 默认灰色细边框
        }
        this.setBorder(border);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        this.repaint();// 触发重绘以更新边框样式
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
}
