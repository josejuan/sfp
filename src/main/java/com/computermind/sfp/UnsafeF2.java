package com.computermind.sfp;

@FunctionalInterface
public interface UnsafeF2<A, B, C> {
    C apply(A a, B b ) throws Exception;
}
