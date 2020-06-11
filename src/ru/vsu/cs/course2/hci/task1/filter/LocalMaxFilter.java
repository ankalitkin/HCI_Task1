package ru.vsu.cs.course2.hci.task1.filter;

import java.util.LinkedList;

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
        LinkedList<Double> buffer = new LinkedList<>();
        for (int i = 0; i < source.length; i++) {
            if (buffer.size() == width) {
                buffer.removeFirst();
            }
            buffer.addLast(source[i]);
            res[i] = buffer.stream().max(Double::compareTo).get();
        }
        return res;
    }
}
