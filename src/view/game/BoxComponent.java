package view.game;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.LinkedList;

public class BoxComponent extends JComponent {
    private int row;
    private int col;
    private boolean isSelected;
    private int type;
    private static String currentSkin = "classic";
    private int width1;
    private int height1;
    private static final int selectedBorder = 3;
    private static final int border = 1;
    private boolean isDisabled = false;
    private boolean isHighlighted = false;
    private LinkedList<Point> trail = new LinkedList<>();
    private boolean isDragging = false;
    private Point dragOffset;

    public BoxComponent(int type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.isSelected = false;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //皮肤
        Image skinImage = SkinManager.getBoxImage(currentSkin, type);
        if (skinImage != null) {
            g.drawImage(skinImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        //边框
        Border border = isSelected ?
                BorderFactory.createLineBorder(Color.RED, selectedBorder) :
                BorderFactory.createLineBorder(Color.DARK_GRAY, BoxComponent.border);
        this.setBorder(border);

        //禁用功能
        if (isDisabled) {
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
        }

        //高亮功能
        if (isHighlighted) {
            g.setColor(new Color(255, 255, 180, 120));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    //平滑
    public void setLocationSliding(int x1, int y1) {
        int x0 = getX();
        int y0 = getY();
        int dx = x1 - x0;
        int dy = y1 - y0;

        int frames = 15;
        Timer animTimer = new Timer(10, null);
        final int[] currentFrame = {0};

        animTimer.addActionListener(e -> {
            currentFrame[0]++;
            float progress = currentFrame[0] / (float) frames;
            int newX = x0 + Math.round(dx * progress);
            int newY = y0 + Math.round(dy * progress);
            setLocation(newX, newY);

//            trail.add(new Point(newX + getWidth() / 2, newY + getHeight() / 2));
//            if (trail.size() > 10) {
//                trail.removeFirst();
//            }

            repaint();

            if (currentFrame[0] >= frames) {
                animTimer.stop();
                setLocation(x1, y1);
                repaint();
                triggerShakeIfOutOfBounds();
            }
        });
        animTimer.start();
    }

    //缓动模式
    public void setLocationSlow(int x1, int y1) {
        int x0 = getX();
        int y0 = getY();
        int dx = x1 - x0;
        int dy = y1 - y0;

        int frames = 50;
        Timer animTimer = new Timer(15, null);  // 间隔时间变长，速度变慢
        final int[] currentFrame = {0};

        animTimer.addActionListener(e -> {
            currentFrame[0]++;
            float progress = currentFrame[0] / (float) frames;
            int newX = x0 + Math.round(dx * progress);
            int newY = y0 + Math.round(dy * progress);
            setLocation(newX, newY);

//            trail.add(new Point(newX + getWidth() / 2, newY + getHeight() / 2));
//            if (trail.size() > 10) {
//                trail.removeFirst();
//            }

            repaint();

            if (currentFrame[0] >= frames) {
                animTimer.stop();
                setLocation(x1, y1);
                repaint();
                triggerShakeIfOutOfBounds();
            }
        });
        animTimer.start();
    }

    public void shake() {
        int x0 = getX();
        Timer shakeTimer = new Timer(10, null);
        final int[] currentFrame = {0};
        shakeTimer.addActionListener(e -> {
            currentFrame[0]++;
            int offset = (currentFrame[0] % 2 == 0) ? 4 : -4;
            setLocation(x0 + offset, getY());
            if (currentFrame[0] >= 6) {
                shakeTimer.stop();
                setLocation(x0, getY());
            }
        });
        shakeTimer.start();
    }

    private void triggerShakeIfOutOfBounds() {
        Rectangle parentBounds = getParent().getBounds();
        boolean hitBoundary = false;
        if (getX() < 0 || getY() < 0 || getX() + getWidth() > parentBounds.width || getY() + getHeight() > parentBounds.height) {
            hitBoundary = true;
        }

        if (!hitBoundary) return;

        int x0 = getX();
        int y0 = getY();
        Timer shakeTimer = new Timer(10, null);
        final int[] currentFrame = {0};
        shakeTimer.addActionListener(e -> {
            currentFrame[0]++;
            int offsetX = (currentFrame[0] % 2 == 0) ? 2 : -2;
            int offsetY = (currentFrame[0] % 2 == 0) ? 1 : -1;
            setLocation(x0 + offsetX, y0 + offsetY);
            if (currentFrame[0] >= 6) {
                shakeTimer.stop();
                setLocation(x0, y0);
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

    public int getWidth1() {
        return width1;
    }

    public int getHeight1() {
        return height1;
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
