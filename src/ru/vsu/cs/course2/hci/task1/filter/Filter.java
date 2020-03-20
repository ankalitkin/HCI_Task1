package ru.vsu.cs.course2.hci.task1.filter;

public interface Filter {
    String getName();

    double[] doFilter(double[] source);
}
