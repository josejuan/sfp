package com.computermind.sfp;

@FunctionalInterface
public interface UnsafeF1<A, B> {
    B apply(A a) throws Exception;
}
