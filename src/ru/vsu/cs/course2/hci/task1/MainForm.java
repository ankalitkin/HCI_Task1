package ru.vsu.cs.course2.hci.task1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.chain.FilterChain;
import ru.vsu.cs.course2.hci.task1.chain.FilterChains;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class MainForm {
    private static final String FORM_TITLE = "Задание 1";
    private static final int MIN_ACCEPTABLE_VALUE = 350;
    private static final int MAX_ACCEPTABLE_VALUE = 1500;
    private JPanel rootPanel;
    private JPanel drawPanel;
    private JButton loadButton;
    private JButton alg1Button;
    private JButton alg2Button;
    private JButton alg3Button;
    private JCheckBox intermediateCheckBox;
    private JButton monitorButton;
    private JButton recordButton;
    private JButton stopButton;
    private JSpinner limitSpinner;
    private JProgressBar maxBar;
    private JLabel maxLabel;
    private JSpinner thresholdSpinner;
    private ChartPanel chartPanel;

    private double[] data;
    private LinkedList<Integer> realtimeData;
    private String title;
    private FilterChain currentFilterChain = FilterChains.emptyFilterChain;

    private MainForm() {
        chartPanel = new ChartPanel(null);
        drawPanel.setLayout(new GridLayout());
        drawPanel.add(chartPanel);
        loadButton.addActionListener(e -> {
            loadData();
            displayChart(FilterChains.emptyFilterChain);
        });
        alg1Button.addActionListener(e -> displayChart(FilterChains.myFilterChain1));
        alg2Button.addActionListener(e -> displayChart(FilterChains.myFilterChain2));
        alg3Button.addActionListener(e -> displayChart(FilterChains.myFilterChain3));
        intermediateCheckBox.addActionListener(e -> displayChart());
        monitorButton.addActionListener(e -> {
            realtimeData = new LinkedList<>();
            Realtime.INSTANCE.captureAudio(this::processValue, this::processMaxValue);
        });
        recordButton.setEnabled(false);
        limitSpinner.setModel(new SpinnerNumberModel(100, 10, 10000, 1));
        SpinnerNumberModel thresholdModel = new SpinnerNumberModel(Realtime.THRESHOLD, 100, 10000, 1);
        thresholdSpinner.setModel(thresholdModel);
        thresholdModel.addChangeListener(e -> Realtime.THRESHOLD = (int) thresholdModel.getValue());
        stopButton.addActionListener(e -> Realtime.INSTANCE.stopCapture());
        maxBar.setMaximum(Realtime.THRESHOLD);
    }

    private void processValue(Integer value) {
        System.out.println(value);
        if (value <= MIN_ACCEPTABLE_VALUE || value >= MAX_ACCEPTABLE_VALUE)
            return;
        realtimeData.addLast(value);
        if (realtimeData.size() > (int) limitSpinner.getValue())
            realtimeData.removeFirst();
        data = realtimeData.stream().mapToDouble(x -> x).toArray();
        displayChart();
    }

    private void processMaxValue(int maxValue) {
        maxLabel.setText(String.format("%d/%d", maxValue, Realtime.THRESHOLD));
        maxBar.setValue(maxValue);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        MainForm mainForm = new MainForm();

        JFrame frame = new JFrame(FORM_TITLE);
        frame.setContentPane(mainForm.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtilities.updateComponentTreeUI(mainForm.rootPanel);
        frame.setMinimumSize(new Dimension(500, 400));
        frame.setSize(1100, 800);
        frame.setVisible(true);
    }

    private void loadData() {
        String filename = Util.browseOpenFile();
        if (filename == null)
            return;
        title = Util.getTitle(filename);
        data = Util.readFile(filename);
    }

    private void displayChart(FilterChain testFilterChain) {
        currentFilterChain = testFilterChain;
        displayChart();
    }

    private void displayChart() {
        if (currentFilterChain == null || data == null)
            return;
        DefaultXYDataset dataset = currentFilterChain.getDataset(data, intermediateCheckBox.isSelected());
        JFreeChart chart = ChartFactory.createXYLineChart(title, "Номер отсчёта", "Значение", dataset);
        chartPanel.setChart(chart);
    }
}
