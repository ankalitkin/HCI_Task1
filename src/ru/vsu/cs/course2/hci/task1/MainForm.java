package ru.vsu.cs.course2.hci.task1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.chain.FilterChain;
import ru.vsu.cs.course2.hci.task1.chain.FilterChains;

import javax.swing.*;
import java.awt.*;


public class MainForm {
    private static final String FORM_TITLE = "Задание 1";
    private JPanel rootPanel;
    private JPanel drawPanel;
    private JButton loadButton;
    private JButton alg1Button;
    private JButton alg2Button;
    private JButton alg3Button;
    private JCheckBox intermediateCheckBox;
    private ChartPanel chartPanel;

    private double[] data;
    private String title;
    private FilterChain currentFilterChain;

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
