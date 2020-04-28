package ru.vsu.cs.course2.hci.task1.chain;

import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.Util;
import ru.vsu.cs.course2.hci.task1.filter.Filter;

import java.util.concurrent.atomic.AtomicInteger;

public class FilterChain {
    private final Filter[] filters;

    public FilterChain(Filter... filters) {
        this.filters = filters;
    }

    public DefaultXYDataset getDataset(double[] data, boolean saveIntermediateResults) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] source = {Util.generateXAxis(data.length), data};
        dataset.addSeries("Исходные данные", source);
        if (filters.length == 0)
            return dataset;
        if (saveIntermediateResults)
            saveAllResults(data, dataset);
        else
            saveLastResult(data, dataset);
        return dataset;
    }

    public void getStat(double[] data, Boolean[] orig, AtomicInteger truePositive, AtomicInteger trueNegative) {
        double[] res = data;
        for (Filter filter : filters) {
            res = filter.doFilter(res);
        }
        int truePos = 0;
        int trueNeg = 0;
        for (int i = 0; i < res.length; i++) {
            if (orig[i] == null)
                continue;
            if (res[i] > 0 && orig[i])
                truePos++;
            if (res[i] == 0 && !orig[i])
                trueNeg++;
        }
        truePositive.set(truePos);
        trueNegative.set(trueNeg);
    }

    private void saveAllResults(double[] data, DefaultXYDataset dataset) {
        int count = 1;
        for (Filter filter : filters) {
            data = filter.doFilter(data);
            double[][] xy = {Util.generateXAxis(data.length), data};
            String title = String.format("%d. %s", count++, filter.getName());
            dataset.addSeries(title, xy);
        }
    }

    private void saveLastResult(double[] data, DefaultXYDataset dataset) {
        for (Filter filter : filters) {
            data = filter.doFilter(data);
        }
        double[][] xy = {Util.generateXAxis(data.length), data};
        dataset.addSeries("Результат", xy);
    }
}
