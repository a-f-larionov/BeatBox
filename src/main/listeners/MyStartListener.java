package main.listeners;

import main.BeatBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyStartListener implements ActionListener {
    private final BeatBox beatBox;
    public MyStartListener(BeatBox beatBox) {
        this.beatBox = beatBox;
    }

    public void actionPerformed(ActionEvent a) {
        beatBox.buildTrackAndStart();
    }//конец public void actionPerformed класса MyStartListener
}//конец public class MyStartListener
