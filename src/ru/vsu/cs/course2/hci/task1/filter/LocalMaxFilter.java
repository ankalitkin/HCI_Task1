package ru.vsu.cs.course2.hci.task1.filter;

public class LocalMaxFilter implements Filter {
    private final int width;

    public LocalMaxFilter(int width) {
        this.width = width;
    }

    @Override
    public String getName() {
        return String.format("Примитивный фильтр максимума (%d)", width);
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            int start = Math.max(0, i - width);
            int end = i;
            double max = 0;
            for (int j = start; j < end; j++) {
                if (source[j] > max) {
                    max = source[j];
                }
            }
            res[i] = max;
        }
        return res;
    }
}
