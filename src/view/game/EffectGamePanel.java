package view.game;

import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 滑动特效模式：在棋盘区加入简单的移动视觉特效
 */
public class EffectGamePanel extends AbstractGamePanel {

    public EffectGamePanel(MapModel model) {
        super(model);
    }

    @Override
    public void afterMove() {
        super.afterMove();
        playMoveEffect();
    }

    /**
     * 移动后的特效：所有方块闪烁一次
     */
    private void playMoveEffect() {
        for (BoxComponent box : boxes) {
            Color original = box.getBackground();
            box.setBackground(Color.YELLOW); // 特效颜色

            // 延迟恢复原颜色（模拟闪烁）
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> box.setBackground(original));
                }
            }, 100); // 0.1秒后恢复
        }
    }
}

