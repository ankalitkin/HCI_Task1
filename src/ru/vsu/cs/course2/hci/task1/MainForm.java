package ru.vsu.cs.course2.hci.task1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.chain.ChainBuilder;
import ru.vsu.cs.course2.hci.task1.chain.FilterChain;
import ru.vsu.cs.course2.hci.task1.chain.FilterChains;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class MainForm {
    private static final String FORM_TITLE = "Задание 1";
    private JPanel rootPanel;
    private JPanel drawPanel;
    private JButton loadButton;
    private JButton alg1Button;
    private JButton alg2Button;
    private JButton alg3Button;
    private JCheckBox intermediateCheckBox;
    private JButton monitorButton;
    private JButton stopButton;
    private JSpinner limitSpinner;
    private JSpinner coefSpinner;
    private JButton analysisButton;
    private JButton noneButton;
    private JButton cardioButton;
    private ChartPanel chartPanel;

    private double[] data;
    private LinkedList<Integer> realtimeData;
    private String title;
    private ChainBuilder currentChainBuilder = FilterChains.emptyChainBuilder;
    private Process provider;

    private MainForm() {
        chartPanel = new ChartPanel(null);
        drawPanel.setLayout(new GridLayout());
        drawPanel.add(chartPanel);
        loadButton.addActionListener(e -> {
            loadData();
            displayChart(FilterChains.emptyChainBuilder);
        });
        noneButton.addActionListener(e -> displayChart(FilterChains.emptyChainBuilder));
        alg1Button.addActionListener(e -> displayChart(FilterChains.chain1Builder));
        alg2Button.addActionListener(e -> displayChart(FilterChains.chain2Builder));
        alg3Button.addActionListener(e -> displayChart(FilterChains.chain3Builder));
        cardioButton.addActionListener(e -> displayChart(FilterChains.cardioChainBuilder));
        intermediateCheckBox.addActionListener(e -> displayChart());
        monitorButton.addActionListener(e -> {
            realtimeData = new LinkedList<>();
            Realtime.INSTANCE.startCapture(this::processValue);
        });
        stopButton.addActionListener(e -> Realtime.INSTANCE.stopCapture());
        limitSpinner.setModel(new SpinnerNumberModel(1000, 10, 1000000, 1));
        SpinnerNumberModel coefModel = new SpinnerNumberModel(0.5, 0, 1, 0.001);
        coefModel.addChangeListener(e -> displayChart());
        coefSpinner.setModel(coefModel);
        analysisButton.addActionListener(e -> new AnalysisForm(() -> currentChainBuilder, () -> data));
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
        frame.setMinimumSize(new Dimension(500, 400));
        frame.setSize(1300, 1000);
        frame.setVisible(true);
    }

    private void processValue(Integer value) {
//        if (value < 420)
//            return;
        realtimeData.addLast(value);
        while (realtimeData.size() > (int) limitSpinner.getValue())
            realtimeData.removeFirst();
        data = realtimeData.stream().mapToDouble(x -> x).toArray();
        //displayChart();
    }

    private void loadData() {
        String filename = Util.browseOpenFile();
        if (filename == null)
            return;
        title = Util.getTitle(filename);
        if (filename.endsWith(".exe") || filename.endsWith(".com")) {
            provider = Util.startProvider(filename);
            monitorButton.doClick();
        } else {
            Realtime.INSTANCE.stopCapture();
            if (provider != null) {
                provider.destroy();
                provider = null;
            }
            data = Util.readFile(filename);
        }
    }

    private void displayChart(ChainBuilder chainBuilder) {
        currentChainBuilder = chainBuilder;
        displayChart();
    }

    private void displayChart() {
        if (currentChainBuilder == null || data == null)
            return;
        FilterChain chain = currentChainBuilder.build((double) coefSpinner.getValue());
        DefaultXYDataset dataset = chain.getDataset(data, intermediateCheckBox.isSelected());
        JFreeChart chart = ChartFactory.createXYLineChart(title, "Номер отсчёта", "Значение", dataset);
        chart.getXYPlot().setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);
        chartPanel.setChart(chart);

    }
}
