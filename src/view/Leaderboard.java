package view;

import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Leaderboard {
    public static Leaderboard instance;
    private ArrayList<String> usersList;
    private ArrayList<Integer> stepsList;
    private ArrayList<Integer> timesList;
    private static final String SAVE_FILE = "rank.txt";

    private Leaderboard() {
        usersList = new ArrayList<>();
        stepsList = new ArrayList<>();
        timesList = new ArrayList<>();
        loadFromSystem();
    }

    //获取排行榜实例
    public static Leaderboard getInstance() {
        if (instance == null) {
            instance = new Leaderboard();
        }
        return instance;
    }

    //读取数据
    private void loadFromSystem() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                showErrorDialog("创建排行榜文件时出错", e);
                return;
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

    private int calculateScore(int steps, int time) {
        return 1000 - steps * 5 - time * 3;
    }

    public void updateRecord(String user, int steps, int time) {
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).equals(user)) {
                int currentScore = calculateScore(stepsList.get(i), timesList.get(i));
                int newScore = calculateScore(steps, time);
                if (newScore > currentScore) {
                    stepsList.set(i, steps);
                    timesList.set(i, time);
                }
                return;
            }
        }
        usersList.add(user);
        stepsList.add(steps);
        timesList.add(time);
    }

    public void addRecord(String user, int steps, int time) {
        updateRecord(user, steps, time);
    }

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

    private void showErrorDialog(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message + ": " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
    }

    public int getFastestTime() {
        if (timesList.isEmpty()) {
            return 2147483647;
        }
        return timesList.stream().min(Integer::compare).orElse(0);
    }

    public int getFastestSteps() {
        if (stepsList.isEmpty()) {
            return 2147483647;
        }
        return stepsList.stream().min(Integer::compare).orElse(0);
    }

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