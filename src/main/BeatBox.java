package main;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY;


public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList; //Храним флажки в массиве ArrayList
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    String[] instrumentName = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
            "Maracas", "Whistle", "Low Conga", "Cowbell",
            "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38,
            49, 39, 50, 60,
            70, 72, 64, 56,
            58, 47, 67, 63};

    public static void main(String[] args) {
        new BeatBox().buildGUI();
    }

    public void buildGUI() {
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);

        //Устанавливаем поля между краями панели и местом размещения компонентов (строка ниже)
        background.setBorder((BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        checkboxList = new ArrayList<JCheckBox>();
        JPanel buttonBox = new JPanel(new GridLayout(12,1));

        JButton start = new JButton("Play");
        start.setPreferredSize(new Dimension(100,30));
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.setPreferredSize(new Dimension(100,30));
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Temp +");
        upTempo.setPreferredSize(new Dimension(100,30));
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Temp -");
        downTempo.setPreferredSize(new Dimension(100, 30));
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton clear = new JButton("Clear");
        clear.setPreferredSize(new Dimension(100, 30));
        clear.addActionListener(new MyClear());
        buttonBox.add(clear);

        JButton save = new JButton("Save");
        save.setPreferredSize(new Dimension(100, 30));
        save.addActionListener(new MySendListener());
        buttonBox.add(save);

        JButton load = new JButton("Load");
        load.setPreferredSize(new Dimension(100, 30));
        load.addActionListener(new MyLoadListener());
        buttonBox.add(load);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentName[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        //Создаем фажки, присваиваем им значения false, добавляем их в массив ArrayList и на панель
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50,50, 300,300);
        theFrame.pack();
        theFrame.setVisible(true);
    } // Конец метода public void buildGUI()

    public void setUpMidi() {
        //MIDI-код для получения синтезатора, секвенсора и дорожки
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {e.printStackTrace();}
    } // Конец метода public void setUpMidi

    public void buildTrackAndStart() {
        // Основной метод для преобразования состояния флажков в MIDI-события и добавления их в дорожку
        int[] trackList; //массив из 16 элементов для хранения значений для каждого инструмента на все 16 тактов

        //Удаляем старую дорожку и создаем новую
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) { //цикл для каждого инструмента
            trackList = new int[16];
            int key = instruments[i]; //Задаем клавишу, представляющую инструмент. Массив содержит MIDI-числа для каждого инструмента

            for (int j = 0; j < 16; j++) { //внутренний цикл - для кажого такта
                JCheckBox jc = checkboxList.get(j + (16*i));
                //Проверка флажка (true/false) на данном такте
                //True: помещаем значение клавиши в текущую ячейку массива
                //False: инструмент не играем в данном такте (0)
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            } //Конец внутреенего цикла (j)

            makeTracks(trackList); //для инструмента i для всех 16 тактов создаем события и добавляем их в дорожку
            track.add(makeEvent(176, 1, 127, 0, 16));
        }//Конец внешнего цикла (i)
        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(LOOP_CONTINUOUSLY); //задаем количество повторений цикла (непрерывный цикл)
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {e.printStackTrace();}
    }//Конец метода public void buildTrackAndStart

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }//конец public void actionPerformed класса MyStartListener
    }//конец public class MyStartListener

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }//конец public void actionPerformed класса MyStopListener
    }//конец public class MyStopListener

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor*1.03));
        }//конец public void actionPerformed класса MyUpTempoListener
    }//конец public class MyUpTempoListener

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor*0.97));
        }//конец public void actionPerformed класса MyDownTempoListener
    }//конец public class MyDownTempoListener

    public class MyClear implements ActionListener {
        public void actionPerformed (ActionEvent a) {
            sequencer.stop();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    (checkboxList.get(j + (16*i))).setSelected(false);
                }
            }
        }
    }
    public class MySendListener implements ActionListener {
        public void actionPerformed (ActionEvent a) {
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            try {
                JFileChooser saveFile = new JFileChooser();
                saveFile.showOpenDialog(theFrame);
                BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.getSelectedFile()));
                for (int i = 0; i < 256; i++) {
                    if (checkboxState[i]) {
                        writer.write(1);
                    } else {
                        writer.write(0);
                    }
                }
                writer.close();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public class MyLoadListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    (checkboxList.get(j + (16*i))).setSelected(false);
                }
            }
            try {
                JFileChooser fileOpen = new JFileChooser();
                fileOpen.showOpenDialog(theFrame);
                BufferedReader reader = new BufferedReader(new FileReader(fileOpen.getSelectedFile()));
                String line = null;
                String s = null;
                char[] temp = new char[256];
                while ((line = reader.readLine()) != null) {
                    s = line;
                }
                reader.close();
                temp = s.toCharArray();
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        switch (temp[j+(16*i)]) {
                            case 0: {
                                checkboxList.get(j+(16*i)).setSelected(false);
                                break;
                            }
                            case 1: {
                                checkboxList.get(j+(16*i)).setSelected(true);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {ex.printStackTrace();}

        }
    }
    public void makeTracks(int[] list) {
        //Метод создает собития для одного инструмента за каждый проход цикла для всех 16 тактов
        //Можно получить int[] для Bass Drum, и каждый элемент массива будет содержать либо клавишу этого инструмента, либо 0.
        //Если это 0, то инструмент не играет на текущем такте
        //Если не 0, то создаем событие и добавляем его в дорожку

        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));      //Включает ноту
                track.add(makeEvent(128, 9, key, 100, i+1));    //Выключает ноту
            } //конец if (нет else)
        }//конец цикла (i)
    }//Конец метода public void makeTracks(int[] list)

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        //вспомогательный метод для формирования сообщений и возвращения MidiEvent
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {e.printStackTrace();}
        return event;
    }//конец метода public MidiEvent makeEvent(int comd, int chan, int ont, int two, int tick)
}//Конец класса BeatBox