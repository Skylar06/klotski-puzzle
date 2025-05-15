package view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * This class is only to enable key events.
 */
public abstract class ListenerPanel extends JPanel {
    // 构造函数启用事件监听
    public ListenerPanel() {
        enableEvents(AWTEvent.KEY_EVENT_MASK);// 启用键盘事件
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);// 启用鼠标事件
        this.setFocusable(true);// 允许接收焦点
    }

    // 处理键盘事件
    @Override
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if (e.getID() == KeyEvent.KEY_PRESSED) { // 仅响应按下事件
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT -> doMoveRight();
                case KeyEvent.VK_LEFT -> doMoveLeft();
                case KeyEvent.VK_UP -> doMoveUp();
                case KeyEvent.VK_DOWN -> doMoveDown();
            }
        }
    }

    // 处理鼠标点击事件
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if(e.getButton() == MouseEvent.MOUSE_CLICKED) {
            doMouseClick(e.getPoint());
        }
        super.processMouseEvent(e);
    }

    // 定义抽象方法（由子类实现具体逻辑）
    public abstract void doMouseClick(Point point);

    public abstract void doMoveRight();

    public abstract void doMoveLeft();

    public abstract void doMoveUp();

    public abstract void doMoveDown();


}
