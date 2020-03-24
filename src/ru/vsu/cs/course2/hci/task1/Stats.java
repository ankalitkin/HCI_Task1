package ru.vsu.cs.course2.hci.task1;

import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.chain.ChainBuilder;
import ru.vsu.cs.course2.hci.task1.chain.FilterChain;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Stats {

    static void getStat(ChainBuilder builder, double[] data, int trueStart, int trueEnd,
                        AtomicReference<DefaultXYDataset> sensDataset,
                        AtomicReference<DefaultXYDataset> specDataset,
                        AtomicReference<DefaultXYDataset> rocDataset) {
        AtomicReference<double[]> xAxis = new AtomicReference<>();
        AtomicReference<double[]> sensitivity = new AtomicReference<>();
        AtomicReference<double[]> specificity = new AtomicReference<>();
        AtomicReference<double[]> falsePositiveRate = new AtomicReference<>();
        testFilterChain(builder, data, trueStart, trueEnd, xAxis, sensitivity, specificity, falsePositiveRate);
        sensDataset.set(getDataset(xAxis.get(), sensitivity.get(), "Чувствительность"));
        specDataset.set(getDataset(xAxis.get(), specificity.get(), "Специфичность"));
        rocDataset.set(getDataset(falsePositiveRate.get(), sensitivity.get(), "ROC-кривая"));
    }

    private static void testFilterChain(ChainBuilder builder, double[] data, int trueStart, int trueEnd,
                                        AtomicReference<double[]> xAxis, AtomicReference<double[]> sensitivity,
                                        AtomicReference<double[]> specificity, AtomicReference<double[]> falsePositiveRate) {
        int posCount = trueEnd - trueStart + 1;
        int negCount = data.length - posCount;
        double[] x = new double[101];
        double[] sens = new double[x.length];
        double[] spec = new double[x.length];
        double[] fpr = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = i / (double) (x.length - 1);
            FilterChain filterChain = builder.build(x[i]);
            AtomicInteger truePos = new AtomicInteger();
            AtomicInteger trueNeg = new AtomicInteger();
            filterChain.getStat(data, trueStart, trueEnd, truePos, trueNeg);
            sens[i] = truePos.get() / (double) posCount;
            spec[i] = trueNeg.get() / (double) negCount;
            fpr[i] = 1 - spec[i];
        }
        xAxis.set(x);
        sensitivity.set(sens);
        specificity.set(spec);
        falsePositiveRate.set(fpr);
    }

    private static DefaultXYDataset getDataset(double[] xAxis, double[] yAxis, String seriesKey) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] source = {xAxis, yAxis};
        dataset.addSeries(seriesKey, source);
        return dataset;
    }
}
