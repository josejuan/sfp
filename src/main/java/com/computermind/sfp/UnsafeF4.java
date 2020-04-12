package com.computermind.sfp;

@FunctionalInterface
public interface UnsafeF4<A, B, C, D, E> {
    E apply(A a, B b, C c, D d) throws Exception;
}
