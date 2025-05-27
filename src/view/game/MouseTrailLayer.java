package view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;

public class MouseTrailLayer extends JPanel {
    private final java.util.List<Point> trailPoints = new LinkedList<>();

    public MouseTrailLayer() {
        setOpaque(false);

        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e instanceof MouseEvent mouseEvent && mouseEvent.getID() == MouseEvent.MOUSE_MOVED) {
                SwingUtilities.invokeLater(() -> {
                    Point p = SwingUtilities.convertPoint(
                            ((MouseEvent) e).getComponent(),
                            mouseEvent.getPoint(),
                            this
                    );
                    trailPoints.add(p);
                    if (trailPoints.size() > 15) trailPoints.remove(0);
                    repaint();
                });
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

        new Timer(30, e -> repaint()).start();
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        for (int i = 0; i < trailPoints.size(); i++) {
            Point p = trailPoints.get(i);
            float alpha = (float) i / trailPoints.size();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(Color.CYAN);
            g2d.fillOval(p.x - 5, p.y - 5, 10, 10);
        }
        g2d.dispose();
    }
}