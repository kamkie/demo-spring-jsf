package com.example.tests;

import com.example.utils.SubMillis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

import static org.assertj.core.api.Assertions.assertThat;

class SubMillisTest {
    @Test
    void testBaseUnit() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.getBaseUnit()).isEqualTo(ChronoUnit.MICROS);
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.getBaseUnit()).isEqualTo(ChronoUnit.NANOS);
    }

    @Test
    void testRangeUnit() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.getRangeUnit()).isEqualTo(ChronoUnit.MILLIS);
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.getRangeUnit()).isEqualTo(ChronoUnit.MICROS);
    }

    @Test
    void range() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.range()).isEqualTo(ValueRange.of(0, 999));
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.range()).isEqualTo(ValueRange.of(0, 999));
    }

    @Test
    void isDateBased() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isDateBased()).isFalse();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isDateBased()).isFalse();
    }

    @Test
    void isTimeBased() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isTimeBased()).isTrue();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isTimeBased()).isTrue();
    }

    @Test
    void isSupportedBy() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isSupportedBy(LocalTime.NOON)).isTrue();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isSupportedBy(LocalTime.NOON)).isTrue();

        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isSupportedBy(LocalDate.now())).isFalse();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isSupportedBy(LocalDate.now())).isFalse();
    }

    @Test
    void rangeRefinedBy() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.rangeRefinedBy(LocalTime.NOON)).isEqualTo(ValueRange.of(0, 999));
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.rangeRefinedBy(LocalTime.NOON)).isEqualTo(ValueRange.of(0, 999));
    }

    @Test
    void adjustInto() {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.adjustInto(LocalTime.NOON, 100))
                .isEqualTo(LocalTime.ofNanoOfDay(100_000));
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.adjustInto(LocalTime.NOON, 100))
                .isEqualTo(LocalTime.ofNanoOfDay(100));
    }

    @Test
    void adjustIntoFail1() {
        Assertions.assertThrows(UnsupportedTemporalTypeException.class,
                () -> SubMillis.MICROS_OF_MILLI_SECOND.adjustInto(LocalDate.now(), 100));
    }

    @Test
    void adjustIntoFail2() {
        Assertions.assertThrows(UnsupportedTemporalTypeException.class,
                () -> SubMillis.NANOS_OF_MICRO_SECOND.adjustInto(LocalDate.now(), 100));
    }

}
