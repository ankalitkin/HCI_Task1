package ru.vsu.cs.course2.hci.task1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.chain.ChainBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AnalysisForm {
    private static final String FORM_TITLE = "Анализ алгоритма";
    private static int left;
    private static int right;
    private ChartPanel sensChartPanel;
    private ChartPanel rocChartPanel;
    private JPanel rootPanel;
    private JSpinner leftSpinner;
    private JPanel sensPanel;
    private JPanel rocPanel;
    private JTextArea infoTextArea;
    private JButton updateButton;
    private JSpinner rightSpinner;
    private JCheckBox a5kHzCheckBox;
    private JTextArea classTextArea;
    private Supplier<ChainBuilder> chainBuilder;
    private Supplier<double[]> data;

    public AnalysisForm(Supplier<ChainBuilder> chainBuilder, Supplier<double[]> data) {
        JFrame frame = new JFrame(FORM_TITLE);
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 400));
        frame.setSize(1500, 700);
        frame.setVisible(true);

        this.chainBuilder = chainBuilder;
        this.data = data;

        sensChartPanel = new ChartPanel(null);
        sensPanel.setLayout(new GridLayout());
        sensPanel.add(sensChartPanel);
        rocChartPanel = new ChartPanel(null);
        rocPanel.setLayout(new GridLayout());
        rocPanel.add(rocChartPanel);

        int length = data != null ? data.get().length : 1;
        SpinnerNumberModel leftModel = new SpinnerNumberModel(1, 1, length, 1);
        leftSpinner.setModel(leftModel);

        SpinnerNumberModel rightModel = new SpinnerNumberModel(length, 1, length, 1);
        rightSpinner.setModel(rightModel);
        rightModel.addChangeListener(e -> update());

        updateButton.addActionListener(e -> update());
    }

    private void update() {
        if (data == null)
            return;
        AtomicReference<DefaultXYDataset> sensSpecDataset = new AtomicReference<>();
        AtomicReference<DefaultXYDataset> rocDataset = new AtomicReference<>();

        left = (int) leftSpinner.getValue();
        right = (int) rightSpinner.getValue();
        Boolean[] orig = Stats.getIntervals(data.get().length, left - 1, right - 1, infoTextArea.getText(), a5kHzCheckBox.isSelected() ? 5 : 1);
        AtomicReference<Double> sensRef = new AtomicReference<>();
        AtomicReference<Double> specRef = new AtomicReference<>();
        AtomicReference<Double> thresRef = new AtomicReference<>();
        Stats.getStat(chainBuilder.get(), data.get(), orig, sensSpecDataset, rocDataset, sensRef, specRef, thresRef);
        classTextArea.setText(String.format("Пороговое значение: %3f \r\nЧувствительность: %3f \r\nСпецифичность: %3f", thresRef.get(), sensRef.get(), specRef.get()));

        JFreeChart sensChart = ChartFactory.createXYLineChart("Чувствительность/Специфичность", "Пороговое значение", "Чувствительность/Специфичность", sensSpecDataset.get());
        JFreeChart rocChart = ChartFactory.createXYLineChart("ROC-кривая", "Доля ложноположительных результатов", "Чувствительность", rocDataset.get());

        sensChartPanel.setChart(sensChart);
        rocChartPanel.setChart(rocChart);
    }
}
