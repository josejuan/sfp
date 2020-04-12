package com.computermind.sfp;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public abstract class Either<L, R> {
    public static <A, B> Either<A, B> maybe(final Optional<B> x, final A whenFail) {
        return x.<Either<A, B>>map(Either::right).orElseGet(() -> left(whenFail));
    }

    public static <A, B> Either<A, B> left(final A leftValue) {
        return new Left<>(leftValue);
    }

    public static <B> Either<String, B> leftEx(final Throwable ex) {
        return new Left<>(ex.getLocalizedMessage());
    }

    public static <A, B, E extends Throwable> Either<A, B> left(final A leftValue, final E ignoredException) {
        return left(leftValue);
    }

    public static <A, B> Either<A, B> right(final B rightValue) {
        return new Right<>(rightValue);
    }

    public static <A, B> Either<A, B> left(final A leftValue, final Class<B> ignore) {
        return left(leftValue);
    }

    public static <A, B> Either<A, B> right(final B rightValue, final Class<A> ignore) {
        return right(rightValue);
    }

    public static <A, B> Either<A, B> ofNullable(final B nullableValue, final A leftValue) {
        return nullableValue == null ? left(leftValue) : right(nullableValue);
    }

    public static <A, B> Stream<A> lefts(final Stream<Either<A, B>> s) {
        return s.filter(Either::isLeft).map(Either::getLeft);
    }

    public static <A, B> Stream<B> rights(final Stream<Either<A, B>> s) {
        return s.filter(Either::isRight).map(Either::getRight);
    }

    public static <A, B> Either<A, Stream<B>> sequence(final Stream<Either<A, B>> xs) {
        final AtomicReference<A> failed = new AtomicReference<>(null);
        final List<B> rs = xs.takeWhile(x -> {
            if (x.isLeft()) {
                failed.set(x.getLeft());
                return false;
            }
            return true;
        }).map(Either::getRight).collect(toList());
        return failed.get() == null ? right(rs.stream()) : left(failed.get());
    }

    public static <A> Either<Exception, A> unsafe(final UnsafeF0<A> f) {
        try {
            return right(f.get());
        } catch (Exception e) {
            return left(e);
        }
    }

    public static <A, B> Function<A, Either<Exception, B>> unsafe(final UnsafeF1<A, B> f) {
        return a -> {
            try {
                return right(f.apply(a));
            } catch (Exception e) {
                return left(e);
            }
        };
    }

    public static <A, B, C> BiFunction<A, B, Either<Exception, C>> unsafe(final UnsafeF2<A, B, C> f) {
        return (a, b) -> {
            try {
                return right(f.apply(a, b));
            } catch (Exception e) {
                return left(e);
            }
        };
    }

    public Either<String, R> lstring() {
        return mapLeft(e -> e instanceof Exception ? ((Exception) e).getLocalizedMessage() : Objects.toString(e));
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract <X> X either(final Function<L, X> p0, final Function<R, X> p1);

    public final void then(final Consumer<L> whenLeft, final Consumer<R> whenRight) {
        either(l -> {
            whenLeft.accept(l);
            return null;
        }, r -> {
            whenRight.accept(r);
            return null;
        });
    }

    public final Either<L, R> with(final Consumer<L> whenLeft, final Consumer<R> whenRight) {
        then(whenLeft, whenRight);
        return this;
    }

    public final Either<L, R> withLeft(final Consumer<L> whenLeft) {
        return with(whenLeft, ignore -> {
        });
    }

    public final Either<L, R> withRight(final Consumer<R> whenRight) {
        return with(ignore -> {
        }, whenRight);
    }

    public final Either<L, R> guard(final Function<R, Boolean> assertion, final L whenFail) {
        return guard(assertion, ignore -> whenFail);
    }

    public final Either<L, R> guard(final Function<R, Boolean> assertion, final Function<R, L> whenFail) {
        return bind(x -> ((boolean) assertion.apply(x)) ? right(x) : left(whenFail.apply(x)));
    }

    public final Either<L, R> guard(final Supplier<Boolean> assertion, final L whenFail) {
        return guard(ignore -> assertion.get(), whenFail);
    }

    public final Either<L, R> guard(final Supplier<Boolean> assertion, final Function<R, L> whenFail) {
        return guard(ignore -> assertion.get(), whenFail);
    }

    public abstract L getLeft();

    public abstract R getRight();

    public final <G> Either<L, G> map(final Function<R, G> f) {
        return isRight() ? right(f.apply(getRight())) : left(getLeft());
    }

    public final <F> Either<F, R> mapLeft(final Function<L, F> f) {
        return isRight() ? right(getRight()) : left(f.apply(getLeft()));
    }

    public final <G> Either<L, G> bind(final Function<R, Either<L, G>> k) {
        return isLeft() ? left(getLeft()) : k.apply(getRight());
    }

    public final <G> Either<L, G> bind(final Supplier<Either<L, G>> k) {
        return isLeft() ? left(getLeft()) : k.get();
    }

    public final Either<L, R> join(final Either<L, Either<L, R>> w) {
        return isLeft() ? this : w.getRight();
    }

    public final R orElse(final R defaultValue) {
        return either(ignore -> defaultValue, Function.identity());
    }

    public final <X extends Throwable> R orThrown(final X e) throws X, Throwable {
        if (isLeft())
            throw e;
        return getRight();
    }

    public final Either<L, R> orElse(final Function<L, Either<L, R>> f) {
        return either(f, Either::right);
    }

    public final R left2right(final Function<L, R> f) {
        return either(f, x -> x);
    }

    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!getClass().isInstance(other)) {
            return false;
        }
        final Either<L, R> o = (Either<L, R>) other;
        if (isLeft() != o.isLeft())
            return false;
        if (isLeft())
            return Objects.equals(getLeft(), o.getLeft());
        return Objects.equals(getRight(), o.getRight());
    }

    @Override
    public final int hashCode() {
        return either(Object::hashCode, Object::hashCode);
    }

    @Override
    public String toString() {
        return map(r -> String.format("%s { %s }", "Right", r.toString()))
                .left2right(l -> String.format("%s { %s }", "Left", l.toString()));
    }

    private static final class Left<A, B> extends Either<A, B> {
        private final A a;

        Left(final A a) {
            this.a = a;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public <X> X either(final Function<A, X> left, final Function<B, X> right) {
            return left.apply(a);
        }

        @Override
        public A getLeft() {
            return a;
        }

        @Override
        public B getRight() {
            throw new IllegalAccessError("cannot get Right value from Left instance");
        }
    }

    private static final class Right<A, B> extends Either<A, B> {
        private final B b;

        Right(final B b) {
            this.b = b;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public <X> X either(final Function<A, X> left, final Function<B, X> right) {
            return right.apply(b);
        }

        @Override
        public A getLeft() {
            throw new IllegalAccessError("cannot get Left value from Right instance");
        }

        @Override
        public B getRight() {
            return b;
        }
    }
}
