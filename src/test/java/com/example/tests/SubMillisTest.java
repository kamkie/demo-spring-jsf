package com.example.tests;

import com.example.utils.SubMillis;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

import static org.assertj.core.api.Assertions.assertThat;

public class SubMillisTest {
    @Test
    public void getBaseUnit() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.getBaseUnit()).isEqualTo(ChronoUnit.MICROS);
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.getBaseUnit()).isEqualTo(ChronoUnit.NANOS);
    }

    @Test
    public void getRangeUnit() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.getRangeUnit()).isEqualTo(ChronoUnit.MILLIS);
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.getRangeUnit()).isEqualTo(ChronoUnit.MICROS);
    }

    @Test
    public void range() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.range()).isEqualTo(ValueRange.of(0, 999));
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.range()).isEqualTo(ValueRange.of(0, 999));
    }

    @Test
    public void isDateBased() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isDateBased()).isFalse();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isDateBased()).isFalse();
    }

    @Test
    public void isTimeBased() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isTimeBased()).isTrue();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isTimeBased()).isTrue();
    }

    @Test
    public void isSupportedBy() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isSupportedBy(LocalTime.NOON)).isTrue();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isSupportedBy(LocalTime.NOON)).isTrue();

        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.isSupportedBy(LocalDate.now())).isFalse();
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.isSupportedBy(LocalDate.now())).isFalse();
    }

    @Test
    public void rangeRefinedBy() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.rangeRefinedBy(LocalTime.NOON)).isEqualTo(ValueRange.of(0, 999));
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.rangeRefinedBy(LocalTime.NOON)).isEqualTo(ValueRange.of(0, 999));
    }

    @Test
    public void adjustInto() throws Exception {
        assertThat(SubMillis.MICROS_OF_MILLI_SECOND.adjustInto(LocalTime.NOON, 100)).isEqualTo(LocalTime.ofNanoOfDay(100_000));
        assertThat(SubMillis.NANOS_OF_MICRO_SECOND.adjustInto(LocalTime.NOON, 100)).isEqualTo(LocalTime.ofNanoOfDay(100));
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void adjustIntoFail1() throws Exception {
        SubMillis.MICROS_OF_MILLI_SECOND.adjustInto(LocalDate.now(), 100);
    }

    @Test(expected = UnsupportedTemporalTypeException.class)
    public void adjustIntoFail2() throws Exception {
        SubMillis.NANOS_OF_MICRO_SECOND.adjustInto(LocalDate.now(), 100);
    }

}
