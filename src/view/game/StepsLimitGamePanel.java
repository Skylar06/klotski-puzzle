package view.game;

import model.MapModel;

import javax.swing.*;
import java.awt.*;

public class StepsLimitGamePanel extends AbstractGamePanel {

    private int maxSteps;  // 最大剩余步数
    private JLabel remainingStepsLabel;

    public StepsLimitGamePanel(MapModel model, int maxSteps) {
        super(model);               // super 会调用 initialGame()
        this.maxSteps = maxSteps;  // 设置最大步数
        initRemainingStepsLabel(); // 添加 UI 标签，但暂不调用 update
    }


    private void initRemainingStepsLabel() {
        // 先清空状态区再重新布局
        statusPanel.removeAll();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        // 新的标签
        remainingStepsLabel = createStyledLabel("剩余步数: " + maxSteps, "楷体", Font.BOLD, 22, Color.RED);
        timeLabel = createStyledLabel("时间: 00:00", "楷体", Font.BOLD, 20, Color.WHITE);

        // 布局顺序
        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(remainingStepsLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(timeLabel);
        statusPanel.add(Box.createVerticalGlue());

        statusPanel.revalidate();
        statusPanel.repaint();
    }


    // 每次成功走一步调用此方法
    @Override
    public BoxComponent afterMove() {
        // 先调用父类方法更新步数和界面
        BoxComponent result = super.afterMove();

        int remaining = maxSteps - steps;

        if (remaining >= 0) {
            updateRemainingStepsLabel();
        } else {
            // 步数用尽时的处理
            JOptionPane.showMessageDialog(this, "步数用尽，游戏失败！");
            boardPanel.setEnabled(false);
            // 如果需要可以返回特定组件，这里保持与父类一致的null
        }

        return result; // 返回父类方法的返回值
    }

    private void updateRemainingStepsLabel() {
        if (remainingStepsLabel != null) {
            int remaining = maxSteps - steps;
            remainingStepsLabel.setText("剩余: " + Math.max(0, remaining));
        }
    }

    // 你可以根据需要重写初始化游戏的方法，重置步数
    @Override
    public void initialGame() {
        super.initialGame();
        setSteps(0);
        updateRemainingStepsLabel();

        if (!boardPanel.isEnabled()) {
            boardPanel.setEnabled(true);
        }
    }
}