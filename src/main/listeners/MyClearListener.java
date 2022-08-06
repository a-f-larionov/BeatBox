package main.listeners;

import main.BeatBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyClearListener implements ActionListener {
    private final BeatBox beatBox;
    public MyClearListener(BeatBox beatBox) {
        this.beatBox = beatBox;
    }

    public void actionPerformed(ActionEvent a) {
        beatBox.sequencer.stop();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                (beatBox.checkboxList.get(j + (16 * i))).setSelected(false);
            }
        }
    }
}
