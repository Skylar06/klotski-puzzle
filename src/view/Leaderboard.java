package view;

import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Leaderboard {
    public static Leaderboard instance;
    private ArrayList<String> usersList; // 用户名列表
    private ArrayList<Integer> stepsList; // 步数列表
    private ArrayList<Integer> timesList;  // 时间列表
    private static final String SAVE_FILE = "rank.txt";

    // 私有构造函数，确保单例模式
    private Leaderboard() {
        usersList = new ArrayList<>();
        stepsList = new ArrayList<>();
        timesList = new ArrayList<>();
        loadFromSystem();
    }

    // 获取排行榜实例
    public static Leaderboard getInstance() {
        if (instance == null) {
            instance = new Leaderboard();
        }
        return instance;
    }

    // 从系统中读取已有的排行榜数据
    private void loadFromSystem() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            try {
                // 如果文件不存在，创建一个新文件
                file.createNewFile();
            } catch (IOException e) {
                showErrorDialog("创建排行榜文件时出错", e);
                return; // 创建文件失败，返回
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String user = parts[0].trim();
                    int steps = Integer.parseInt(parts[1].trim());
                    int time = Integer.parseInt(parts[2].trim());
                    usersList.add(user);
                    stepsList.add(steps);
                    timesList.add(time);
                }
            }
        } catch (IOException e) {
            showErrorDialog("读取排行榜文件时出错", e);
        } catch (NumberFormatException e) {
            showErrorDialog("排行榜文件格式错误", e);
        }
    }

    // 当用户获胜时，将步数和时间添加到排行榜
    public void addRecord(String user, int steps, int time) {
        usersList.add(user);
        stepsList.add(steps);
        timesList.add(time);
        saveToSystem(); // 添加新记录后立即保存
    }

    // 将排行榜数据保存到系统
    private void saveToSystem() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            for (int i = 0; i < usersList.size(); i++) {
                bw.write(usersList.get(i) + "," + stepsList.get(i) + "," + timesList.get(i));
                bw.newLine();
            }
        } catch (IOException e) {
            showErrorDialog("保存排行榜文件时出错", e);
        }
    }

    // 显示错误对话框
    private void showErrorDialog(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message + ": " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
    }

    // 获取最快时间
    public int getFastestTime() {
        if (timesList.isEmpty()) {
            return 2147483647; // 如果没有记录，返回0或其他默认值
        }
        return timesList.stream().min(Integer::compare).orElse(0);
    }

    // 获取最快步数
    public int getFastestSteps() {
        if (stepsList.isEmpty()) {
            return 2147483647; // 如果没有记录，返回0或其他默认值
        }
        return stepsList.stream().min(Integer::compare).orElse(0);
    }

    // 获取用户名列表
    public ArrayList<String> getUsersList() {
        return usersList;
    }

    public ArrayList<Integer> getStepsList() {
        return stepsList;
    }

    public ArrayList<Integer> getTimesList() {
        return timesList;
    }
    public static void initialize() {
        if (instance == null) {
            instance = new Leaderboard();
        }
    }
}