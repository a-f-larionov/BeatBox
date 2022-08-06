package main.listeners;

import main.BeatBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyUpTempoListener implements ActionListener {
    private final BeatBox beatBox;
    public MyUpTempoListener(BeatBox beatBox) {
        this.beatBox = beatBox;
    }

    public void actionPerformed(ActionEvent a) {
        float tempoFactor = beatBox.sequencer.getTempoFactor();
        beatBox.sequencer.setTempoFactor((float) (tempoFactor * 1.03));
    }//конец public void actionPerformed класса MyUpTempoListener
}//конец public class MyUpTempoListener
