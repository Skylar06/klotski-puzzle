import controller.GameController;
import model.MapModel;
import view.Leaderboard;
import view.game.GameFrame1;
import view.game.GamePanel;
import view.level.select.LevelSelectFrame;
import view.login.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            MapModel mapModel = new MapModel(new int[][]{
                    {2, 2, 4, 4, 1},
                    {2, 2, 4, 4, 1},
                    {0, 0, 2, 2, 3},
                    {2, 2, 1, 1, 3}
            });

            LevelSelectFrame levelSelectFrame = new LevelSelectFrame(loginFrame);
            levelSelectFrame.setVisible(false);
            levelSelectFrame.setModel(mapModel);
            GameController gameController = new GameController(mapModel,levelSelectFrame,loginFrame);
            Leaderboard.initialize();
//            GameFrame1 gameFrame = new GameFrame1(mapModel);
//            gameFrame.setVisible(false);
//            loginFrame.setGameFrame(gameFrame);
        });
    }
}
