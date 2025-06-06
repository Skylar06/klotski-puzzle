//package view.game;
//
//import controller.GameController;
//import model.MapModel;
//import view.FrameUtil;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class GameFrame extends JFrame {
//
//    private GameController controller;// 游戏控制器
//    private JButton restartBtn;// 重启按键
//    private JButton saveBtn;//
//    private JButton loadBtn;// 保存按键
//    private boolean isGuest = false;
//    private JLabel stepLabel;// 计数标签
//    private GamePanel gamePanel;// 游戏画板
//
//    public GameFrame(int width, int height, MapModel mapModel) {
//        // 界面布局
//        this.setTitle("2025 CS109 Project Demo");
//        this.setLayout(null);// 绝对布局（需手动定位组件）
//        this.setSize(width, height);
//        //gamePanel = new GamePanel(mapModel,1,);// 创建游戏画板
//        gamePanel.setLocation(30, height / 2 - gamePanel.getHeight() / 2);// 垂直居中
//        this.add(gamePanel);// 垂直居中
//        //this.controller = new GameController(gamePanel, mapModel);// 垂直居中
//
//        // 创建重启按钮（右侧间距80像素）
//        this.restartBtn = FrameUtil.createButton(this, "Restart", new Point(gamePanel.getWidth() + 80, 120), 80, 50);
//        // 创建加载按钮（纵向间距90像素）
//        this.loadBtn = FrameUtil.createButton(this, "Load", new Point(gamePanel.getWidth() + 80, 210), 80, 50);
//        // 创建步数标签（显示操作计数）
//        this.stepLabel = FrameUtil.createJLabel(this, "Start", new Font("serif", Font.ITALIC, 22), new Point(gamePanel.getWidth() + 80, 70), 180, 50);
//        gamePanel.setStepLabel(stepLabel); // 将标签传递给游戏面板
//
//
//        // 重启按钮点击事件
//        this.restartBtn.addActionListener(e -> {
//            controller.restartGame();// 调用控制器重启游戏
//            gamePanel.requestFocusInWindow();//enable key listener 让画布重新获取焦点（保证键盘监听有效）
//        });
//        // 加载按钮点击事件
//        this.loadBtn.addActionListener(e -> {
//            String string = JOptionPane.showInputDialog(this, "Input path:");// 弹出输入框
//            System.out.println(string);// 打印路径（待实现实际加载逻辑）
//            gamePanel.requestFocusInWindow();//enable key listener 焦点回到画布
//        });
//        //todo: add other button here
//        this.setLocationRelativeTo(null); // 窗口居中屏幕
//        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);// 关闭时退出程序
//        JButton undoBtn = FrameUtil.createButton(this, "Undo", new Point(gamePanel.getWidth() + 80, 300), 80, 50);
//        undoBtn.addActionListener(e -> gamePanel.undoMove());
//        this.loadBtn.addActionListener(e -> {
//            String path = JOptionPane.showInputDialog(this, "./load.txt");
//            if (path != null && !path.isEmpty()) {
//                gamePanel.loadGame(path);
//                gamePanel.requestFocusInWindow();
//            }
//        });
//        // 创建保存按钮
//        this.saveBtn = FrameUtil.createButton(this, "Save", new Point(gamePanel.getWidth() + 240, 120), 80, 50);
//        this.loadBtn = FrameUtil.createButton(this, "Load", new Point(gamePanel.getWidth() + 240, 210), 80, 50);
//        this.loadBtn.addActionListener(e -> {
//            if (!isGuest) {
//                String string = JOptionPane.showInputDialog(this, "./load.txt");
//                if (string != null && !string.isEmpty()) {
//                    gamePanel.loadGame(string);
//                    gamePanel.requestFocusInWindow();
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Guests cannot load games.");
//            }
//        });
//        this.saveBtn.addActionListener(e -> {
//            if (!isGuest) {
//                gamePanel.saveGame("./load.txt");
//                gamePanel.requestFocusInWindow();
//            } else {
//                JOptionPane.showMessageDialog(this, "Guests cannot load games.");
//            }
//        });
//
//    }
//
//    public void setGuestMode(boolean guest) {
//        this.isGuest = guest;
//        // 根据是否为游客模式启用或禁用保存和加载按钮
//        updateSaveLoadButtons();
//    }
//
//    private void updateSaveLoadButtons() {
//        saveBtn.setEnabled(!isGuest);
//        loadBtn.setEnabled(!isGuest);
//    }
//}
