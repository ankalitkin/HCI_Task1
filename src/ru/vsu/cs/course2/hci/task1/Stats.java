package ru.vsu.cs.course2.hci.task1;

import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.chain.ChainBuilder;
import ru.vsu.cs.course2.hci.task1.chain.FilterChain;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Stats {

    static void getStat(ChainBuilder builder, double[] data, int trueStart, int trueEnd,
                        AtomicReference<DefaultXYDataset> sensSpecDataset,
                        AtomicReference<DefaultXYDataset> rocDataset) {
        AtomicReference<double[]> xAxis = new AtomicReference<>();
        AtomicReference<double[]> sensitivity = new AtomicReference<>();
        AtomicReference<double[]> specificity = new AtomicReference<>();
        AtomicReference<double[]> falsePositiveRate = new AtomicReference<>();

        testFilterChain(builder, data, trueStart, trueEnd, xAxis, sensitivity, specificity, falsePositiveRate);

        DefaultXYDataset sensSpec = new DefaultXYDataset();
        sensSpec.addSeries("Чувствительность", new double[][]{xAxis.get(), sensitivity.get()});
        sensSpec.addSeries("Специфичность", new double[][]{xAxis.get(), specificity.get()});
        sensSpecDataset.set(sensSpec);
        DefaultXYDataset roc = new DefaultXYDataset();
        roc.addSeries("ROC-кривая", new double[][]{falsePositiveRate.get(), sensitivity.get()});
        rocDataset.set(roc);
    }

    private static void testFilterChain(ChainBuilder builder, double[] data, int trueStart, int trueEnd,
                                        AtomicReference<double[]> xAxis, AtomicReference<double[]> sensitivity,
                                        AtomicReference<double[]> specificity, AtomicReference<double[]> falsePositiveRate) {
        int posCount = trueEnd - trueStart + 1;
        int negCount = data.length - posCount;
        double[] x = new double[1001];
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
        sens[0] = fpr[0] = 0;
        sens[1000] = fpr[1000] = 1;
        xAxis.set(x);
        sensitivity.set(sens);
        specificity.set(spec);
        falsePositiveRate.set(fpr);
    }

}
