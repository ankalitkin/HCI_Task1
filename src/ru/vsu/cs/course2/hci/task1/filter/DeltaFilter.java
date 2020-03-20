package ru.vsu.cs.course2.hci.task1.filter;

public class DeltaFilter implements Filter {
    @Override
    public String getName() {
        return "Дельта-фильтр";
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        for (int i = 0; i < source.length - 1; i++) {
            res[i] = source[i + 1] - source[i];
        }
        return res;
    }
}
