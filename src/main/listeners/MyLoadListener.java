package main.listeners;

import main.BeatBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;

public class MyLoadListener implements ActionListener {
    private final BeatBox beatBox;
    public MyLoadListener(BeatBox beatBox) {
        this.beatBox = beatBox;
    }

    public void actionPerformed(ActionEvent e) {
        beatBox.sequencer.stop();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                (beatBox.checkboxList.get(j + (16 * i))).setSelected(false);
            }
        }
        try {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(beatBox.theFrame);
            BufferedReader reader = new BufferedReader(new FileReader(fileOpen.getSelectedFile()));
            String line;
            String s = null;
            char[] temp;
            while ((line = reader.readLine()) != null) {
                s = line;
            }
            reader.close();
            temp = s.toCharArray();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    switch (temp[j + (16 * i)]) {
                        case 0: {
                            beatBox.checkboxList.get(j + (16 * i)).setSelected(false);
                            break;
                        }
                        case 1: {
                            beatBox.checkboxList.get(j + (16 * i)).setSelected(true);
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
