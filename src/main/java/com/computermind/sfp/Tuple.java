package com.computermind.sfp;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Tuple<A, B> {
    private final A fst;
    private final B snd;

    private Tuple(final A fst, final B snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public static <U, V> Tuple<U, V> tuple(final U u, final V v) {
        return new Tuple<>(u, v);
    }

    public static <U> Tuple<U, U> tuple(final U u) {
        return new Tuple<>(u, u);
    }

    public A fst() {
        return fst;
    }

    public B snd() {
        return snd;
    }

    public <U> Tuple<U, B> first(final Function<A, U> f) {
        return tuple(f.apply(fst()), snd());
    }

    public <V> Tuple<A, V> second(final Function<B, V> f) {
        return tuple(fst(), f.apply(snd()));
    }

    public <R> R map(final BiFunction<A, B, R> f) {
        return f.apply(fst(), snd());
    }

    @Override
    public int hashCode() {
        final int h1 = fst() == null ? 0 : fst().hashCode();
        final int h2 = snd() == null ? 0 : snd().hashCode();
        return h1 + 4011 * h2;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Tuple))
            return false;
        final Tuple<A, B> t = (Tuple<A, B>) obj;
        return Objects.equals(this.fst, t.fst) && Objects.equals(this.snd, t.snd);
    }
}
