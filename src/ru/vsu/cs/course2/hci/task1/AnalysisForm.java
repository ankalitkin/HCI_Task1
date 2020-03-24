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
    private JSpinner rightSpinner;
    private JButton updateButton;
    private Supplier<ChainBuilder> chainBuilder;
    private Supplier<double[]> data;

    public AnalysisForm(Supplier<ChainBuilder> chainBuilder, Supplier<double[]> data) {
        JFrame frame = new JFrame(FORM_TITLE);
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 400));
        frame.setSize(1200, 700);
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
        SpinnerNumberModel leftModel = new SpinnerNumberModel(Math.min(Math.max(1, left), length), 1, length, 1);
        leftSpinner.setModel(leftModel);
        leftModel.addChangeListener(e -> update());

        SpinnerNumberModel rightModel = new SpinnerNumberModel(Math.min(Math.max(1, right), length), 1, length, 1);
        rightSpinner.setModel(rightModel);
        rightModel.addChangeListener(e -> update());

        updateButton.addActionListener(e -> update());
        update();
    }

    private void update() {
        if (data == null)
            return;
        AtomicReference<DefaultXYDataset> sensSpecDataset = new AtomicReference<>();
        AtomicReference<DefaultXYDataset> rocDataset = new AtomicReference<>();

        left = (int) leftSpinner.getValue();
        right = (int) rightSpinner.getValue();
        Stats.getStat(chainBuilder.get(), data.get(), left, right, sensSpecDataset, rocDataset);

        JFreeChart sensChart = ChartFactory.createXYLineChart("Чувствительность/Специфичность", "Пороговое значение", "Чувствительность/Специфичность", sensSpecDataset.get());
        JFreeChart rocChart = ChartFactory.createXYLineChart("ROC-кривая", "Доля ложноположительных результатов", "Чувствительность", rocDataset.get());

        sensChartPanel.setChart(sensChart);
        rocChartPanel.setChart(rocChart);
    }
}
