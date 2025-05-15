import controller.GameController;
import model.MapModel;
import view.game.GameFrame;
import view.game.GameFrame1;
import view.game.GamePanel;
import view.level.select.LevelSelectFrame;
import view.login.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {// 确保GUI创建在事件调度线程"EDT"，否则会卡顿
            LoginFrame loginFrame = new LoginFrame();// 创建登录窗口
            loginFrame.setVisible(true);// 显示登录窗口
            MapModel mapModel = new MapModel(new int[][]{// 创建游戏地图数据
                    {2, 2, 2, 2, 1},
                    {1, 3, 2, 2, 0},
                    {1, 3, 4, 4, 1},
                    {2, 2, 4, 4, 0}
            });

            LevelSelectFrame levelSelectFrame = new LevelSelectFrame();
            levelSelectFrame.setVisible(false);
            GameController gameController = new GameController(mapModel,levelSelectFrame,loginFrame);
//            GameFrame1 gameFrame = new GameFrame1(mapModel);// 创建游戏主窗口
//            gameFrame.setVisible(false);// 初始隐藏游戏窗口
//            loginFrame.setGameFrame(gameFrame);// 关联登录窗口和游戏窗口
        });
    }
}
