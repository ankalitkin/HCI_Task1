package ru.vsu.cs.course2.hci.task1.filter;

import java.util.LinkedList;

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
        LinkedList<Double> buffer = new LinkedList<>();
        for (int i = 0; i < source.length; i++) {
            if (buffer.size() == width) {
                buffer.removeFirst();
            }
            buffer.addLast(source[i]);
            res[i] = buffer.stream().min(Double::compareTo).get();
        }
        return res;
    }
}
