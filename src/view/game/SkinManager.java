package view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class SkinManager {
    private static final String[] SKIN_NAMES = {
            "classic", "soldier", "cat", "dragon", "cartoon", "pixel", "sustech"
    };

    private static final Map<String, Map<Integer, Image>> skinCache = new HashMap<>();

    public static Image getBoxImage(String skin, int type) {
        if (!skinCache.containsKey(skin)) {
            skinCache.put(skin, new HashMap<>());
        }

        Map<Integer, Image> skinSet = skinCache.get(skin);
        if (!skinSet.containsKey(type)) {
            try {
                String path = "/skins/" + skin + "/" + type + ".png";
                BufferedImage img = ImageIO.read(SkinManager.class.getResourceAsStream(path));
                skinSet.put(type, img);
            } catch (Exception e) {
                System.out.println("加载皮肤失败: " + skin + " 类型: " + type);
                return null;
            }
        }
        return skinSet.get(type);
    }

    public static String[] getAvailableSkins() {
        return SKIN_NAMES;
    }
}
