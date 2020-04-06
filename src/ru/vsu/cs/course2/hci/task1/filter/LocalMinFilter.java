package ru.vsu.cs.course2.hci.task1.filter;

public class LocalMinFilter implements Filter {
    private final int width;

    public LocalMinFilter(int width) {
        this.width = width;
    }

    @Override
    public String getName() {
        return String.format("Примитивный фильтр минимума (%d)", width);
    }

    @Override
    public double[] doFilter(double[] source) {
        double[] res = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            int start = Math.max(0, i - width);
            int end = i;
            double min = source[start];
            for (int j = start + 1; j < end; j++) {
                if (source[j] < min) {
                    min = source[j];
                }
            }
            res[i] = min;
        }
        return res;
    }
}
