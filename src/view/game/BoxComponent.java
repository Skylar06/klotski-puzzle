package view.game;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BoxComponent extends JComponent {
    private int row;
    private int col;// 在网格中的行列位置
    private boolean isSelected;// 选中状态
    private int type; // 类型：1-4，用于判断是哪种方块（如横2、竖2、大方块等）
    private static String currentSkin = "classic"; // 当前皮肤，默认是古风
    private int logicalWidth;  // 单元格宽
    private int logicalHeight; // 单元格高

    private static final int BORDER_WIDTH_SELECTED = 3;
    private static final int BORDER_WIDTH_DEFAULT = 1;

    private boolean isDisabled = false;
    private boolean isHighlighted = false;
    private LinkedList<Point> trailPoints = new LinkedList<>();

    private boolean isDragging = false;
    private Point dragOffset;

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
        Graphics2D g2 = (Graphics2D) g.create();
        for (int i = 0; i < trailPoints.size(); i++) {
            Point p = trailPoints.get(i);
            float alpha = (float) i / trailPoints.size();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.3f));
            g2.setColor(Color.GRAY);
            g2.fillOval(p.x - getX() - 4, p.y - getY() - 4, 8, 8);
        }

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

        if (isDisabled) {
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
        }

        if (isHighlighted) {
            g.setColor(new Color(255, 255, 180, 120)); // 半透明淡黄色
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void startDrag(Point point) {
        dragOffset = new Point(point.x - getX(), point.y - getY());
        isDragging = true;
    }

    public void drag(Point point) {
        if (isDragging) {
            int newX = point.x - dragOffset.x;
            int newY = point.y - dragOffset.y;

            // 边界检查
            newX = Math.max(0, Math.min(newX, getParent().getWidth() - getWidth()));
            newY = Math.max(0, Math.min(newY, getParent().getHeight() - getHeight()));

            setLocation(newX, newY);
        }
    }

    public void endDrag() {
        isDragging = false;
        dragOffset = null;
    }

    public void setLocationAnimated(int targetX, int targetY) {
        int startX = getX();
        int startY = getY();
        int dx = targetX - startX;
        int dy = targetY - startY;

        int frames = 15;
        Timer animTimer = new Timer(10, null);
        final int[] currentFrame = {0};

        animTimer.addActionListener(e -> {
            currentFrame[0]++;
            float progress = currentFrame[0] / (float) frames;
            int newX = startX + Math.round(dx * progress);
            int newY = startY + Math.round(dy * progress);
            setLocation(newX, newY);

            // 拖尾
            trailPoints.add(new Point(newX + getWidth() / 2, newY + getHeight() / 2));
            if (trailPoints.size() > 10) {
                trailPoints.removeFirst();
            }

            repaint(); // <---- 加强刷帧

            if (currentFrame[0] >= frames) {
                animTimer.stop();
                setLocation(targetX, targetY);
                repaint();
                triggerShakeIfOutOfBounds(); // <- 确保调用
            }
        });

        animTimer.start();
    }

    public void setLocationAnimatedSlow(int targetX, int targetY) {
        int startX = getX();
        int startY = getY();
        int dx = targetX - startX;
        int dy = targetY - startY;

        int frames = 50;  // 帧数多，动画时间更长
        Timer animTimer = new Timer(15, null);  // 间隔时间变长，速度变慢
        final int[] currentFrame = {0};

        animTimer.addActionListener(e -> {
            currentFrame[0]++;
            float progress = currentFrame[0] / (float) frames;
            int newX = startX + Math.round(dx * progress);
            int newY = startY + Math.round(dy * progress);
            setLocation(newX, newY);

            // 拖尾效果如果有的话，照旧
            trailPoints.add(new Point(newX + getWidth() / 2, newY + getHeight() / 2));
            if (trailPoints.size() > 10) {
                trailPoints.removeFirst();
            }

            repaint();

            if (currentFrame[0] >= frames) {
                animTimer.stop();
                setLocation(targetX, targetY);
                repaint();
                triggerShakeIfOutOfBounds();
            }
        });

        animTimer.start();
    }

    public void shake() {
        int originalX = getX();
        Timer shakeTimer = new Timer(10, null);
        final int[] count = {0};
        shakeTimer.addActionListener(e -> {
            count[0]++;
            int offset = (count[0] % 2 == 0) ? 4 : -4;
            setLocation(originalX + offset, getY());
            if (count[0] >= 6) {
                shakeTimer.stop();
                setLocation(originalX, getY());
            }
        });
        shakeTimer.start();
    }

    private void triggerShakeIfOutOfBounds() {
        Rectangle parentBounds = getParent().getBounds();
        boolean hitBoundary = false;
        if (getX() < 0 || getY() < 0 ||
                getX() + getWidth() > parentBounds.width ||
                getY() + getHeight() > parentBounds.height) {
            hitBoundary = true;
        }

        if (!hitBoundary) return;

        int originalX = getX();
        int originalY = getY();
        Timer shakeTimer = new Timer(10, null);
        final int[] count = {0};
        shakeTimer.addActionListener(e -> {
            count[0]++;
            int offsetX = (count[0] % 2 == 0) ? 2 : -2;
            int offsetY = (count[0] % 2 == 0) ? 1 : -1;
            setLocation(originalX + offsetX, originalY + offsetY);
            if (count[0] >= 6) {
                shakeTimer.stop();
                setLocation(originalX, originalY);
            }
        });
        shakeTimer.start();
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
        repaint();
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
        repaint();
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        this.repaint();
    }

    public int getLogicalWidth() {
        return logicalWidth;
    }

    public int getLogicalHeight() {
        return logicalHeight;
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
