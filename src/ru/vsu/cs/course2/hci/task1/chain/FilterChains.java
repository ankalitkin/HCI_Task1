package ru.vsu.cs.course2.hci.task1.chain;

import ru.vsu.cs.course2.hci.task1.filter.*;

public class FilterChains {
    public static FilterChain emptyFilterChain = new FilterChain();

    public static FilterChain myFilterChain1 = new FilterChain(
            new SmoothFilter(15),
            new ThresholdFilter(725)
    );

    public static FilterChain myFilterChain2 = new FilterChain(
            new DiameterFilter(10),
            new ThresholdFilter(200)
    );

    public static FilterChain myFilterChain3 = new FilterChain(
            new DeltaFilter(),
            new AbsoluteFilter(),
            new LocalMaxFilter(10),
            new ThresholdFilter(100)
    );
}
