package main.listeners;

import main.BeatBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class MySendListener implements ActionListener {
    private final BeatBox beatBox;
    public MySendListener(BeatBox beatBox) {
        this.beatBox = beatBox;
    }

    public void actionPerformed(ActionEvent a) {
        boolean[] checkboxState = new boolean[256];
        for (int i = 0; i < 256; i++) {
            JCheckBox check = beatBox.checkboxList.get(i);
            if (check.isSelected()) {
                checkboxState[i] = true;
            }
        }
        try {
            JFileChooser saveFile = new JFileChooser();
            saveFile.showOpenDialog(beatBox.theFrame);
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.getSelectedFile()));
            for (int i = 0; i < 256; i++) {
                if (checkboxState[i]) {
                    writer.write(1);
                } else {
                    writer.write(0);
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
