package com.computermind.sfp;

@FunctionalInterface
public interface UnsafeF3<A, B, C, D> {
    D apply(A a, B b, C c) throws Exception;
}
