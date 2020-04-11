package com.computermind.sfp;

@FunctionalInterface
public interface Monoid<T> {
    T mappend(final T x);
}
