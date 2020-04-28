package ru.vsu.cs.course2.hci.task1.chain;

import ru.vsu.cs.course2.hci.task1.filter.*;

public class FilterChains {
    public static ChainBuilder emptyChainBuilder = val -> new FilterChain();


    public static ChainBuilder chain1Builder = coef ->
            new FilterChain(
                    new SmoothFilter(15),
                    new ThresholdFilter(350 + (int) (1000 * coef))
            );


    public static ChainBuilder chain2Builder = coef ->
            new FilterChain(
                    new DiameterFilter(10),
                    new ThresholdFilter((int) (1000 * coef))
            );


    public static ChainBuilder chain3Builder = coef ->
            new FilterChain(
                    new DeltaFilter(),
                    new AbsoluteFilter(),
                    new LocalMaxFilter(10),
                    new ThresholdFilter((int) (500 * coef))
            );

    public static ChainBuilder cardioChainBuilder = coef ->
            new FilterChain(
                    new DiameterFilter(100),
                    new ThresholdFilter((int) (10000 * coef))
            );
}

