package com.computermind.sfp;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Parsers {
    private Parsers() {
        throw new IllegalAccessError("`Parsers` is static");
    }

    public static Either<String, Integer> maybeInt(final String integer) {
        try {
            return Either.right(Integer.parseInt(integer));
        } catch (NullPointerException | NumberFormatException e) {
            return Either.left(e.getLocalizedMessage(), e);
        }
    }

    public static Either<String, String> maybeString(final String string) {
        if (string == null || string.isEmpty()) {
            return Either.left("empty string");
        }
        return Either.right(string);
    }

    public static Either<String, Matcher> maybePattern(final Pattern pattern, final String input) {
        final Matcher mx = pattern.matcher(input);
        if (mx.find()) {
            return Either.right(mx);
        }
        return Either.left("pattern do not match");
    }

    public static Either<String, Matcher> maybePattern(final String pattern, final String input) {
        return maybePattern(Pattern.compile(pattern), input);
    }

    public static Either<String, ZonedDateTime> maybeZonedDateTime(final int year, final int month, final int day, final int hour, final int minute, final int second, final int millisecond, final ZoneId zoneId) {
        try {
            return Either.right(ZonedDateTime.of(year, month, day, hour, minute, second, millisecond, zoneId));
        } catch (DateTimeException ex) {
            return Either.leftEx(ex);
        }
    }

    public static Either<String, ZonedDateTime> maybeZonedDateTimeYYYYMMDD(final String yyyymmdd, final ZoneId zoneId) {
        return maybePattern("^([0-9]+)[\\-\\/]([0-9]+)[\\-\\/]([0-9]+)$", yyyymmdd).bind(g -> maybeZonedDateTime(Integer.parseInt(g.group(1)), Integer.parseInt(g.group(2)), Integer.parseInt(g.group(3)), 0, 0, 0, 0, zoneId));
    }
}
