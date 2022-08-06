package main;

import main.listeners.*;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY;

public class BeatBox {
    private JPanel mainPanel;
    public ArrayList<JCheckBox> checkboxList = new ArrayList<>(); //Храним флажки в массиве ArrayList
    public Sequencer sequencer;
    public Sequence sequence;
    private Track track;
    public JFrame theFrame;

    // @todo Map or List<InstrumentDescription>;
    private String[] instrumentName = {
            "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
            "Maracas", "Whistle", "Low Conga", "Cowbell",
            "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"
    };
    private int[] instrumentsCode = {
            35, 42, 46, 38,
            49, 39, 50, 60,
            70, 72, 64, 56,
            58, 47, 67, 63
    };

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

        JPanel buttonBox = new JPanel(new GridLayout(12, 1));

        createControlButtons(buttonBox);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentName[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        //Создаем флажки, присваиваем им значения false, добавляем их в массив ArrayList и на панель
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    } // Конец метода public void buildGUI()

    private void createControlButtons(JPanel buttonBox) {
        createButton(buttonBox, "Play", new MyStartListener(this));
        createButton(buttonBox, "Stop", new MyStopListener(this));
        createButton(buttonBox, "Temp +", new MyUpTempoListener(this));
        createButton(buttonBox, "Temp -", new MyDownTempoListener(this));
        createButton(buttonBox, "Clear", new MyClearListener(this));
        createButton(buttonBox, "Save", new MySendListener(this));
        createButton(buttonBox, "Load", new MyLoadListener(this));
    }

    private void createButton(JPanel buttonBox, String title, ActionListener listener) {
        JButton button = new JButton(title);
        button.setPreferredSize(new Dimension(100, 30));
        button.addActionListener(listener);
        buttonBox.add(button);
    }

    public void setUpMidi() {
        //MIDI-код для получения синтезатора, секвенсора и дорожки
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // Конец метода public void setUpMidi

    public void buildTrackAndStart() {
        // Основной метод для преобразования состояния флажков в MIDI-события и добавления их в дорожку
        int[] trackList; //массив из 16 элементов для хранения значений для каждого инструмента на все 16 тактов

        //Удаляем старую дорожку и создаем новую
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) { //цикл для каждого инструмента
            trackList = new int[16];
            int key = instrumentsCode[i]; //Задаем клавишу, представляющую инструмент. Массив содержит MIDI-числа для каждого инструмента

            for (int j = 0; j < 16; j++) { //внутренний цикл - для кажого такта
                JCheckBox jc = checkboxList.get(j + (16 * i));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//Конец метода public void buildTrackAndStart

    public void makeTracks(int[] list) {
        //Метод создает собития для одного инструмента за каждый проход цикла для всех 16 тактов
        //Можно получить int[] для Bass Drum, и каждый элемент массива будет содержать либо клавишу этого инструмента, либо 0.
        //Если это 0, то инструмент не играет на текущем такте
        //Если не 0, то создаем событие и добавляем его в дорожку

        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));      //Включает ноту
                track.add(makeEvent(128, 9, key, 100, i + 1));    //Выключает ноту
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }//конец метода public MidiEvent makeEvent(int comd, int chan, int ont, int two, int tick)
}//Конец класса BeatBox