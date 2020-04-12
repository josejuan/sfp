package com.computermind.sfp;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.computermind.sfp.Either.left;
import static com.computermind.sfp.Either.right;
import static com.computermind.sfp.Either.sequence;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EitherTest {

    @Test
    public void sequenceTest() {
        final Either<String, Stream<Integer>> result =
                sequence(IntStream.range(1, 4).boxed().map(i -> i % 2 == 0 ? left("even!") : right(i)));
        assertTrue(result.isLeft());
    }

}