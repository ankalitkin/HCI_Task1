package ru.vsu.cs.course2.hci.task1;

import org.jfree.data.xy.DefaultXYDataset;
import ru.vsu.cs.course2.hci.task1.chain.ChainBuilder;
import ru.vsu.cs.course2.hci.task1.chain.FilterChain;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Stats {

    private static final int STEPS = 100;

    static void getStat(ChainBuilder builder, double[] data, Boolean[] orig,
                        AtomicReference<DefaultXYDataset> sensSpecDataset,
                        AtomicReference<DefaultXYDataset> rocDataset,
                        AtomicReference<Double> sensRef,
                        AtomicReference<Double> specRef,
                        AtomicReference<Double> thresRef) {
        AtomicReference<double[]> xAxis = new AtomicReference<>();
        AtomicReference<double[]> sensitivity = new AtomicReference<>();
        AtomicReference<double[]> specificity = new AtomicReference<>();
        AtomicReference<double[]> falsePositiveRate = new AtomicReference<>();

        testFilterChain(builder, data, orig, xAxis, sensitivity, specificity, falsePositiveRate);
        getCrossPoint(xAxis, sensitivity.get(), specificity.get(), sensRef, specRef, thresRef);
        DefaultXYDataset sensSpec = new DefaultXYDataset();
        sensSpec.addSeries("Чувствительность", new double[][]{xAxis.get(), sensitivity.get()});
        sensSpec.addSeries("Специфичность", new double[][]{xAxis.get(), specificity.get()});
        sensSpecDataset.set(sensSpec);
        DefaultXYDataset roc = new DefaultXYDataset();
        roc.addSeries("ROC-кривая", new double[][]{falsePositiveRate.get(), sensitivity.get()});
        rocDataset.set(roc);
    }

    private static void testFilterChain(ChainBuilder builder, double[] data, Boolean[] orig,
                                        AtomicReference<double[]> xAxis, AtomicReference<double[]> sensitivity,
                                        AtomicReference<double[]> specificity, AtomicReference<double[]> falsePositiveRate) {
        int posCount = 0;
        int negCount = 0;
        for (Boolean b : orig) {
            if (b == null) {
                continue;
            }
            if (b) {
                posCount++;
            } else {
                negCount++;
            }
        }
        double[] x = new double[STEPS + 1];
        double[] sens = new double[x.length];
        double[] spec = new double[x.length];
        double[] fpr = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = i / (double) (x.length - 1);
            FilterChain filterChain = builder.build(x[i]);
            AtomicInteger truePos = new AtomicInteger();
            AtomicInteger trueNeg = new AtomicInteger();
            filterChain.getStat(data, orig, truePos, trueNeg);
            sens[i] = truePos.get() / (double) posCount;
            spec[i] = trueNeg.get() / (double) negCount;
            fpr[i] = 1 - spec[i];
        }
        //sens[0] = fpr[0] = 0;
        //sens[STEPS] = fpr[STEPS] = 1;
        xAxis.set(x);
        sensitivity.set(sens);
        specificity.set(spec);
        falsePositiveRate.set(fpr);
    }

    static Boolean[] getIntervals(int length, int from, int to, String data, int mul) {
        Boolean[] res = new Boolean[length];
        for (int i = from; i < to; i++) {
            res[i] = false;
        }
        String[] lines = data.split("\n");
        for (String line : lines) {
            if (line.isEmpty())
                continue;
            String[] split = line.split("\t");
            int newFrom = Integer.parseInt(split[0]) * mul;
            int newTo = Integer.parseInt(split[1]) * mul;
            for (int i = newFrom - 1; i <= newTo - 1 && i < length; i++) {
                res[i] = true;
            }
        }
        return res;
    }

    private static void getCrossPoint(AtomicReference<double[]> xAxis, double[] sens, double[] spec, AtomicReference<Double> sensRef, AtomicReference<Double> specRef, AtomicReference<Double> thresRef) {
        double diff = Double.POSITIVE_INFINITY;
        for (int i = 0; i < sens.length; i++) {
            double newDiff = Math.abs(spec[i] - sens[i]);
            if (newDiff < diff) {
                diff = newDiff;
                thresRef.set(xAxis.get()[i]);
                sensRef.set(sens[i]);
                specRef.set(spec[i]);
            }
        }
    }
}
