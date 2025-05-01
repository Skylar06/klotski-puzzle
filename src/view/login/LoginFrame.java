package view.login;

import view.FrameUtil;
import view.game.GameFrame;

import javax.swing.*;
import java.awt.*;


public class LoginFrame extends JFrame {
    private JTextField username;// 用户名输入框
    private JTextField password;// 密码输入框
    private JButton submitBtn;// 确认按钮
    private JButton resetBtn;// 重置按钮
    private GameFrame gameFrame;// 关联游戏界面


    public LoginFrame(int width, int height) {
        // 界面布局代码
        this.setTitle("Login Frame");
        this.setLayout(null);
        this.setSize(width, height);
        JLabel userLabel = FrameUtil.createJLabel(this, new Point(50, 20), 70, 40, "username:");
        JLabel passLabel = FrameUtil.createJLabel(this, new Point(50, 80), 70, 40, "password:");
        username = FrameUtil.createJTextField(this, new Point(120, 20), 120, 40);
        password = FrameUtil.createJTextField(this, new Point(120, 80), 120, 40);

        submitBtn = FrameUtil.createButton(this, "Confirm", new Point(40, 140), 100, 40);
        resetBtn = FrameUtil.createButton(this, "Reset", new Point(160, 140), 100, 40);

        submitBtn.addActionListener(e -> {
            // 打印调试信息
            System.out.println("Username = " + username.getText());
            System.out.println("Password = " + password.getText());
            // 打印调试信息
            if (this.gameFrame != null) {
                this.gameFrame.setVisible(true);// 打印调试信息
                this.setVisible(false);// 打印调试信息
            }
            //todo: check login info 需要在此处添加实际的登录验证逻辑

        });
        resetBtn.addActionListener(e -> {
            username.setText("");// 清空用户名输入框
            password.setText("");// 清空密码输入框
        });

        this.setLocationRelativeTo(null); // 窗口居中屏幕
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);// 关闭时退出程序
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }// 关联登录后的游戏界面
}
