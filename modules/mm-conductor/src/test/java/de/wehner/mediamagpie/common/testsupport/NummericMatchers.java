package de.wehner.mediamagpie.common.testsupport;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class NummericMatchers {

    public static enum AberrationMode {
        UP, DOWN, BOTH_DIRECTIONS;
    }

    public static Matcher<Long> almostEquals(final long value1, final long aberration) {
        return almostEquals(value1, aberration, AberrationMode.BOTH_DIRECTIONS);
    }

    public static Matcher<Long> almostEquals(final long value1, final long aberration, final AberrationMode aberrationMode) {
        return new BaseMatcher<Long>() {
            @Override
            public boolean matches(Object value2) {
                Long long2 = (Long) value2;
                switch (aberrationMode) {
                case UP:
                    return long2 >= value1 && value1 + aberration >= long2;
                case DOWN:
                    return long2 <= value1 && value1 - aberration <= long2;
                case BOTH_DIRECTIONS:
                    return Math.abs(value1 - long2) <= aberration;
                default:
                    throw new UnsupportedOperationException(aberrationMode.name());
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" equals " + value1 + " with aberration of " + aberration);
            }
        };
    }
}
