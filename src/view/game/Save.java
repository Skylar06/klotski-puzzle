package view.game;

import model.MapModel;

import java.io.Serializable;

public class Save implements Serializable {
    public MapModel model;
    public int mode;
    public String user;
    public int time;
    public int step;
    public Save(MapModel model, int mode, String user, int time, int step) {
        this.model = model;
        this.user = user;
        this.mode = mode;
        this.time = time;
        this.step = step;
    }
}
